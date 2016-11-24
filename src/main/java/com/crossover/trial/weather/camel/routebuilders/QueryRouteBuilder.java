package com.crossover.trial.weather.camel.routebuilders;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import com.crossover.trial.weather.rest.InputValidator;
import com.crossover.trial.weather.rest.QueryEndpoint;

/**
 * Route builder for Query endpoint
 * 
 * @author LEIG
 *
 */
public class QueryRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		rest("/query/")
			.get("ping").to("direct:queryPing")
			.get("weather/{iata}/{radius}").to("direct:queryWeather");

		/**
		 * Ping routine
		 */
		from("direct:queryPing").id("d:q:ping")
			.doTry()
				.bean(InputValidator.class, "validate")
				.bean(QueryEndpoint.class, "ping")
			.doCatch(Exception.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.setBody(exceptionMessage())
				.log(LoggingLevel.ERROR, "Error: ${body}")
			.end();
		
		/**
		 * Weather routine
		 */
		from("direct:queryWeather").id("d:q:weather")
			.doTry()
				.bean(InputValidator.class, "validate")
				.bean(QueryEndpoint.class, "weather")
			.doCatch(Exception.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.setBody(exceptionMessage())
				.log(LoggingLevel.ERROR, "Error: ${body}")
			.end();
	}

}
