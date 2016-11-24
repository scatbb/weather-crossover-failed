package com.crossover.trial.weather;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class WeatherEndpointTestRouteBuilder extends RouteBuilder
{

	@Override
	public void configure() throws Exception 
	{
		Properties p = loadProperties();
		
		String restHost = p.getProperty("rest.host");
		String restPort = p.getProperty("rest.port");
		
		/**
		 * Query endpoint routes
		 */
		from("direct:testQueryPing")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.to("http://" + restHost + ":" + restPort + "/query/ping");
		
		from("direct:testQueryWeather")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.to("http://" + restHost + ":" + restPort + "/query/weather/JFK/0");
		
		
		/**
		 * Collector endpoint routes
		 */
		from("direct:testCollectPing")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.to("http://" + restHost + ":" + restPort + "/collect/ping");
	
		from("direct:testCollectAirports")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.to("http://" + restHost + ":" + restPort + "/collect/airports");
		
		from("direct:testCollectAirport")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.to("http://" + restHost + ":" + restPort + "/collect/airport/JFK");
		
		from("direct:testCollectAirportAbsent")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.to("http://" + restHost + ":" + restPort + "/collect/airport/AAA");

		from("direct:testCollectAirportCreate")
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.to("http://" + restHost + ":" + restPort + "/collect/airport/STZ/51.885/0.235");

		from("direct:testCollectUpdateWeather")
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.to("http://" + restHost + ":" + restPort + "/collect/weather/JFK/WIND");
		
		from("direct:testCollectDeleteAirport")
			.setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
			.to("http://" + restHost + ":" + restPort + "/collect/airport/STZ");

		from("direct:testCollectValidatorCheck")
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.to("http://" + restHost + ":" + restPort + "/collect/airport/YY1/12.5/14.3");
		
		from("direct:testCollectExit")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.to("http://" + restHost + ":" + restPort + "/collect/exit");
		
		from("direct:testCollectAirportCreated")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.to("http://" + restHost + ":" + restPort + "/collect/airport/STZ");
	}

	private Properties loadProperties()
	{
		String propertiesFile = System.getProperty("weather.properties");
		Properties p = new Properties();

		try
		{
			p.load(new FileInputStream(new File(propertiesFile)));
		}			
		catch(Exception ex)
		{
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
