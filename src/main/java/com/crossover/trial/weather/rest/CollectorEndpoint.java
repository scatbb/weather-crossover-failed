package com.crossover.trial.weather.rest;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crossover.trial.weather.WeatherCore;
import com.crossover.trial.weather.dao.WeatherAirportsDAO;
import com.crossover.trial.weather.entities.Airport;
import com.crossover.trial.weather.entities.DataPoint;

public class CollectorEndpoint implements ICollectorEndpoint
{
	private static final Logger logger = LoggerFactory.getLogger(CollectorEndpoint.class);
	
	@Override
	public Object ping() {
		return "ready";
	}

	@Override
	public Object updateWeather(Exchange e) {
		logger.info("Weather update started");
		
		WeatherCore.getInstance().addDataPoint(
				e.getIn().getHeader("iata", String.class), 
				e.getIn().getHeader("pointType", String.class), 
				e.getIn().getBody(DataPoint.class)
			);

		logger.info("Weather update completed");
		return "";
	}

	@Override
	public Object getAirports(Exchange e) {
		logger.info("Get airports started");
		WeatherAirportsDAO dao = e.getContext().getRegistry().lookupByNameAndType("dao", WeatherAirportsDAO.class);
		logger.info("Get airports completed");
		return dao.getAirports();
	}

	@Override
	public Object getAirport(Exchange e) throws Exception {
		logger.info("Get airport started");
		WeatherAirportsDAO dao = e.getContext().getRegistry().lookupByNameAndType("dao", WeatherAirportsDAO.class);
		logger.info("Get airport completed");
		return dao.getAirportByIATA(e.getIn().getHeader("iata", String.class));
	}

	@Override
	public Object addAirport(Exchange e) throws Exception
	{
		logger.info("Add airport started");
		Airport a = e.getIn().getBody(Airport.class);
		a.setIata(e.getIn().getHeader("iata", String.class));
		a.setLatitude(e.getIn().getHeader("lat", String.class));
		a.setLongitude(e.getIn().getHeader("long", String.class));
		
		WeatherAirportsDAO dao = e.getContext().getRegistry().lookupByNameAndType("dao", WeatherAirportsDAO.class);
		
		boolean found = true;
		try{dao.getAirportByIATA(a.getIata());}catch(Exception ex){found = false;}
		if(found) {throw new Exception("Airport with IATA " + a.getIata() + " already exists!");}
		
		dao.createAirport(a);
		
		logger.info("Add airport completed");
		return "";
	}

	@Override
	public Object deleteAirport(Exchange e) throws Exception {
		logger.info("Delete airport started");
		WeatherAirportsDAO dao = e.getContext().getRegistry().lookupByNameAndType("dao", WeatherAirportsDAO.class);
		boolean found = true;
		try{dao.getAirportByIATA(e.getIn().getHeader("iata", String.class));}catch(Exception ex){found = false;}
		if(!found) {throw new Exception("Airport with IATA " + e.getIn().getHeader("iata", String.class) + " not found!");}
		dao.deleteAirportByIATA(e.getIn().getHeader("iata", String.class));
		logger.info("Delete airport completed");
		return "";
	}

	@Override
	public Object exit() {
		return "";
	}
}
