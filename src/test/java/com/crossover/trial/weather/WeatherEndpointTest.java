package com.crossover.trial.weather;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;

public class WeatherEndpointTest {
	private CamelContext ctx = new DefaultCamelContext();
	/**
	 * Turn on application
	 * 
	 * @throws Exception
	 */
    	
    @Test
    public void testWeatherService() throws Exception
    {
    	ScheduledExecutorService executor =  Executors.newSingleThreadScheduledExecutor();
    	executor.schedule(new ApplicationRunner(), 1, TimeUnit.SECONDS);
    	System.out.println("Waiting 10 seconds for application to start");
    	Thread.sleep(20000);
    	
    	/**
    	 * Starting endpoints for testing via producerTemplate
    	 */
    	ctx.addRoutes(new WeatherEndpointTestRouteBuilder());
    	ctx.start();
    	
    	ProducerTemplate pt = ctx.createProducerTemplate();
    	
    	/**
    	 * ==================================
    	 * Query tests
    	 * ==================================
    	 */
    	
    	/**
    	 * query/ping test
    	 */
    	String result = pt.requestBodyAndHeaders("direct:testQueryPing", null, new HashMap<String, Object>(), String.class);
    	System.out.println("direct:testQueryPing result is: " + result);
    	assertEquals("{\"iata_freq\":{},\"radius_freq\":[],\"datasize\":0}", result);
    	
    	/**
    	 * query/weather test
    	 */
    	result = pt.requestBodyAndHeaders("direct:testQueryWeather", null, new HashMap<String, Object>(), String.class);
    	System.out.println("direct:testQueryWeather result is: " + result);
    	assertEquals("[]", result);
	
    	/**
    	 * ==================================
    	 * Collector tests
    	 * ==================================
    	 */
    	    	
    	/**
    	 * collect/ping test
    	 */
    	result = pt.requestBodyAndHeaders("direct:testCollectPing", null, new HashMap<String, Object>(), String.class);
    	System.out.println("direct:testCollectPing result is: " + result);
    	assertEquals("\"ready\"", result);
    	
    	/**
    	 * collect/airports GET
    	 */    	
    	result = pt.requestBodyAndHeaders("direct:testCollectAirports", null, new HashMap<String, Object>(), String.class);
    	System.out.println("direct:testCollectAirports result is: " + result);
    	assertEquals(getContents("json/collectAirportsAnswer.json"), result);
    	
    	/**
    	 * collect/airport GET
    	 */
    	result = pt.requestBodyAndHeaders("direct:testCollectAirport", null, new HashMap<String, Object>(), String.class);
    	System.out.println("direct:testCollectAirport result is: " + result);
    	assertEquals(getContents("json/collectAirportAnswer.json"), result);

    	/**
    	 * collect/airport POST
    	 */
    	System.out.println("direct:testCollectAirportCreate request is : " + getContents("json/collectAirportCreateRequest.json"));
    	result = pt.requestBodyAndHeaders("direct:testCollectAirportCreate", getContents("json/collectAirportCreateRequest.json"), new HashMap<String, Object>(), String.class);
    	System.out.println("direct:testCollectAirportCreate result is: " + result);
    	assertEquals("\"\"", result);
    	
    	result = pt.requestBodyAndHeaders("direct:testCollectAirportCreated", null, new HashMap<String, Object>(), String.class);
    	System.out.println("direct:testCollectAirportCreated result is: " + result);
    	assertEquals(getContents("json/collectAirportCreateCreatedAnswer.json"), result);
    	
    	/**
    	 * collect/airport DELETE
    	 */
    	result = pt.requestBodyAndHeaders("direct:testCollectDeleteAirport", null, new HashMap<String, Object>(), String.class);
    	System.out.println("direct:testCollectDeleteAirport result is: " + result);
    	assertEquals("\"\"", result);

    	try
    	{
    		result = pt.requestBodyAndHeaders("direct:testCollectAirportCreated", null, new HashMap<String, Object>(), String.class);
    		assertEquals(0, 1);
    	}
    	catch(Exception ex)
    	{
    		assertEquals(1, 1);
    	}    	    	
    	    	
    	/**
    	 * collect/airport on absent airport iata GET
    	 */
    	try
    	{
	    	result = pt.requestBodyAndHeaders("direct:testCollectAirportAbsent", null, new HashMap<String, Object>(), String.class);
			assertEquals(0, 1);
		}
		catch(Exception ex)
		{
			assertEquals(1, 1);
		}    	    	
    	
    	/**
    	 * collect/airport for validator working GET
    	 */
    	try
    	{
    		result = pt.requestBodyAndHeaders("direct:testCollectValidatorCheck", null, new HashMap<String, Object>(), String.class);
			assertEquals(0, 1);
		}
		catch(Exception ex)
		{
			assertEquals(1, 1);
		}    	    	
    	
    	/**
    	 * collect/weather POST
    	 */
    	result = pt.requestBodyAndHeaders("direct:testCollectUpdateWeather", getContents("json/collectWeatherCreateRequest.json"), new HashMap<String, Object>(), String.class);
    	System.out.println("direct:testCollectUpdateWeather result is: " + result);
    	assertEquals("\"\"", result);
    	    	
    	/**
    	 * query/weather GET after adding weather
    	 */
    	result = pt.requestBodyAndHeaders("direct:testQueryWeather", null, new HashMap<String, Object>(), String.class);
    	System.out.println("direct:testQueryWeather result is: " + result);
    	assertEquals(getContents("json/collectWeatherCreateAnswer.json"), result);
    	
    	
    	/**
    	 * collect/exit
    	 */
    	System.out.println("Turning application off");
    	pt.requestBodyAndHeaders("direct:testCollectExit", null, new HashMap<String, Object>());
    	    	
    	/**
    	 * Stopping endpoints for testing via producerTemplate
    	 */
    	ctx.stop();    
    }
    
    private String getContents(String filePath)
    {
    	String result = "";
    	BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(filePath)));
    	try
    	{
    		return br.readLine();
    	}
    	catch(Exception ex)
    	{
    		
    	}
    	return null;
    }
    
 }