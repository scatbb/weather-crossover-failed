package com.crossover.trial.weather.camel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crossover.trial.weather.WeatherCore;
import com.crossover.trial.weather.dao.WeatherAirportsDAO;
import com.crossover.trial.weather.entities.Airport;

import au.com.bytecode.opencsv.CSVReader;

public class Events extends MainListenerSupport {
	private static final Logger logger = LoggerFactory.getLogger(Events.class);

	
	private Main camel;
	
	public Events(Main camel)
	{
		this.camel = camel;
	}
	
	private void log(String msg)
	{
		System.out.println(msg);
		logger.info(msg);
	}
	
	/**
	 * For test proposes we need ability to reinit database 
	 * 
	 * @param dao
	 */
	private void clearAndReinitDatabase(WeatherAirportsDAO dao)
	{
		try
		{
			dao.deleteAllAirports();
			for(Airport a : loadAirports())
			{
				//System.out.println("Airport: " + a.toString());
				dao.createAirport(a);
				System.out.println("Airport created: " + a.getAname() + "[" + a.getId() + "]");
			}
		}
		catch(Exception ex)
		{
			log("Database reinit falied");
		}
	}
	
	/**
	 * Loading airports data from csv 
	 * 
	 * @return
	 */
	private List<Airport> loadAirports()
	{
        ArrayList<Airport> result = new ArrayList<Airport>();
		try
		{
			
			Reader stringReader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("airports.dat")));
			CSVReader reader = new CSVReader(stringReader);
			
	        String[] nextLine;
	        while ((nextLine = reader.readNext()) != null) 
	        {
	        	if(nextLine.length < 11) continue;
	        	
	        	Airport a = new Airport();
	        	
	        	a.setAname(nextLine[1]);
	        	a.setCity(nextLine[2]);
	        	a.setCountry(nextLine[3]);
	        	a.setIata(nextLine[4]);
	        	a.setCode4(nextLine[5]);
	        	a.setLatitude(nextLine[6]);
	        	a.setLongitude(nextLine[7]);
	        	a.setValue1(nextLine[8]);
	        	a.setValue2(nextLine[9]);
	        	a.setValue3(nextLine[10]);	        	
	        	
	        	result.add(a);
	        }
		}
		catch(Exception ex)
		{
			log("Error while reading airport from airports.dat");
		}
		return result;
	}
	
	
	@Override
    public void beforeStart(MainSupport main) {
		
		log("Initializing application environment before start");
		
		Properties p = loadProperties();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.crossover.trial.weather.entities", p);
		WeatherAirportsDAO dao = new WeatherAirportsDAO(emf);

		/**
		 * Reinit database on startup
		 */
		if("1".equals(p.getProperty("db.reinit")))
		{
			clearAndReinitDatabase(dao);
		}
		
		/**
		 * Initializing airportData before REST becomes active
		 */
		WeatherCore.getInstance().fillAirportData(dao.getAirports());
		log("Airport data fetched from database");
		
		camel.bind("props", p);
		camel.bind("emf", emf);
		camel.bind("dao", dao);
		
		log("Starting application");
	}

	@Override
	public void afterStart(MainSupport main) {
		Properties p = main.getCamelContexts().get(0).getRegistry().lookupByNameAndType("props", Properties.class);
		log("Application started on " + p.getProperty("rest.host") + ":" + p.getProperty("rest.port") + ", use Ctrl+C or REST operation to terminate it");
	}
	
	@Override
	public void beforeStop(MainSupport main) {
		((EntityManagerFactory)main.getCamelContexts().get(0).getRegistry().lookupByName("emf")).close();
		log("Application stopping");
	}
	@Override
	public void afterStop(MainSupport main) {
		log("Application stopped");
	}
	
	/**
	 * Properties loading routine
	 * 
	 * If system property weather.properties defined via -Dweather.properties=filename system will attempts to load properties from 
	 * filename, otherwise or in case of failure properties would be loaded from classpath:app.properties
	 * 
	 * @return
	 */
	private Properties loadProperties()
	{
		String propertiesFile = System.getProperty("weather.properties");
		Properties p = new Properties();

		try
		{
			p.load(new FileInputStream(new File(propertiesFile)));
			log("Properties loaded from external file: paygate.properties = " + propertiesFile);
		}			
		catch(Exception ex)
		{
			log("Properties loading from external file failed, reason: weather.properties = " + 
					propertiesFile + ", " + ex.getMessage());
			try
			{
				p.load(this.getClass().getClassLoader().getResourceAsStream("app.properties"));
			}
			catch(Exception ex1)
			{
				throw new RuntimeException("Application properties load fails");
			}			
		}
		return p;
	}
}
