package com.crossover.trial.weather.camel.routebuilders;

import java.util.Properties;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class WeatherRouteBuilder extends RouteBuilder
{
	@Override
	public void configure() throws Exception 
	{
		
		String restPort = ((Properties)getContext().getRegistry().lookupByName("props")).get("rest.port").toString();
		String restHost = ((Properties)getContext().getRegistry().lookupByName("props")).get("rest.host").toString();
		
		restConfiguration().component("jetty")
			.host(restHost)
			.port(restPort)
			.bindingMode(RestBindingMode.json)
			.dataFormatProperty("json.in.disableFeatures", "FAIL_ON_UNKNOWN_PROPERTIES");
	
		getContext().addRoutes(new CollectRouteBuilder());
		getContext().addRoutes(new QueryRouteBuilder());
		
	}
}
