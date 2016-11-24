package com.crossover.trial.weather.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crossover.trial.weather.WeatherCore;
import com.crossover.trial.weather.entities.AtmosphericInformation;

public class QueryEndpoint implements IQueryEndpoint
{
	private static final Logger logger = LoggerFactory.getLogger(QueryEndpoint.class);

    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
	@Override
	public Object ping() {
		logger.info("Ping started");
		
        Map<String, Object> retval = new HashMap<>();

        // count of non-empty datapoints 
        retval.put("datasize", WeatherCore.getInstance().getDataSize());
        // fraction of queries by airports
        retval.put("iata_freq", WeatherCore.getInstance().getAirportReqFrequency());
        // fraction of queries by radius
        retval.put("radius_freq", WeatherCore.getInstance().getRadiusReqFrequency());
		
        logger.info("Ping completed");
		return retval;
	}

    /**
     * Extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iata the iataCode
     * @param radiusString the radius in km
     *
     * @return a list of atmospheric information
     */
	@Override
	public Object weather(Exchange e) {
		logger.info("Weather started");

		String iataCode = e.getIn().getHeader("iata", String.class);
		String radiusString = e.getIn().getHeader("radius", String.class);
		
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);

        WeatherCore.getInstance().updateRequestFrequency(iataCode, radius);
        List<AtmosphericInformation> retval = WeatherCore.getInstance().getAtmosphericInformation(iataCode, radius);
        
		logger.info("Weather completed");
		return retval;
	}
	
}
