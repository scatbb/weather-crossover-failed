package com.crossover.trial.weather.camel.routebuilders;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ShutdownRunningTask;
import org.apache.camel.builder.RouteBuilder;

import com.crossover.trial.weather.entities.Airport;
import com.crossover.trial.weather.entities.DataPoint;

import com.crossover.trial.weather.rest.CollectorEndpoint;
import com.crossover.trial.weather.rest.InputValidator;

/**
 * Route builder for Collect endpoint
 * 
 * @author LEIG
 *
 */
public class CollectRouteBuilder extends RouteBuilder
{

	@Override
	public void configure() throws Exception {
		rest("/collect/")
			.get("ping").to("direct:collectPing")
			
			.post("weather/{iata}/{pointType}").type(DataPoint.class).to("direct:updateWeather")
			.get("airports").outType(List.class).to("direct:getAirports")
			.get("airport/{iata}").outType(Airport.class).to("direct:getAirport")
			.post("airport/{iata}/{lat}/{long}").type(Airport.class).to("direct:addAirport")
			.delete("airport/{iata}").to("direct:deleteAirport")
			
			.get("exit").to("direct:collectExit");		
		
		/**
		 * Ping routine
		 */
		from("direct:collectPing").id("d:c:ping")
			.doTry()
				.bean(InputValidator.class, "validate")
				.bean(CollectorEndpoint.class, "ping")
			.doCatch(Exception.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.setBody(exceptionMessage())
				.log(LoggingLevel.ERROR, "Error: ${body}")
			.end();
			

		/**
		 * Update weather routine
		 */
		from("direct:updateWeather").id("d:c:updateWeather")
			.doTry()
				.bean(InputValidator.class, "validate")
				.bean(CollectorEndpoint.class, "updateWeather")
			.doCatch(Exception.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.setBody(exceptionMessage())
				.log(LoggingLevel.ERROR, "Error: ${body}")
			.end();
					
		/**
		 * Get Airports routine
		 */
		from("direct:getAirports").id("d:c:getAirports")
			.doTry()
				.bean(InputValidator.class, "validate")
				.bean(CollectorEndpoint.class, "getAirports")
			.doCatch(Exception.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.setBody(exceptionMessage())
				.log(LoggingLevel.ERROR, "Error: ${body}")
			.end();
		
		/**
		 * Get Airport by IATA routine
		 */
		from("direct:getAirport").id("d:c:getAirport")
			.doTry()
				.bean(InputValidator.class, "validate")
				.bean(CollectorEndpoint.class, "getAirport")
			.doCatch(Exception.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.setBody(exceptionMessage())
				.log(LoggingLevel.ERROR, "Error: ${body}")
			.end();

		
		/**
		 * Add Airport routine
		 */
		from("direct:addAirport").id("d:c:addAirport")
			.doTry()
				.bean(InputValidator.class, "validate")
				.bean(CollectorEndpoint.class, "addAirport")
			.doCatch(Exception.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.setBody(exceptionMessage())
				.log(LoggingLevel.ERROR, "Error: ${body}")
			.end();
		
		/**
		 * Delete Airport by IATA routine
		 */
		from("direct:deleteAirport").id("d:c:deleteAirport")
			.doTry()
				.bean(InputValidator.class, "validate")
				.bean(CollectorEndpoint.class, "deleteAirport")
			.doCatch(Exception.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.setBody(exceptionMessage())
				.log(LoggingLevel.ERROR, "Error: ${body}")
			.end();
				
		/**
		 * Exit routine
		 */
		from("direct:collectExit").id("d:c:exit")
			.process(new Processor() {				
				@Override
				public void process(Exchange exchange) throws Exception {
					exchange.getOut().setBody("ready");					
				}
			})
			.wireTap("direct:stop").stop();
		
		/**
		 * Route for stop Camel
		 */
		from("direct:stop").id("d:stop")
			.shutdownRunningTask(ShutdownRunningTask.CompleteAllTasks)
			.process(new Processor() {				
				@Override
				public void process(Exchange exchange) throws Exception {
					getContext().getShutdownStrategy().setTimeout(3);
					getContext().stop();
				}
			})
			.to("log:foo");
	}
}
