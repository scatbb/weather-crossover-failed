package com.crossover.trial.weather.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.camel.Exchange;

import com.crossover.trial.weather.entities.DataPointType;

/**
 * The interface shared to airport weather collection systems.
 *
 * @author code test administartor
 */
public interface ICollectorEndpoint {

    /**
     * A liveliness check for the collection endpoint.
     *
     * @return 1 if the endpoint is alive functioning, 0 otherwise
     */
    @GET
    @Path("/ping")
    Object ping();

    /**
     * Update the airports atmospheric information for a particular pointType with
     * json formatted data point information.
     *
     * @param iataCode the 3 letter airport code
     * @param pointType the point type, {@link DataPointType} for a complete list
     * @param datapointJson a json dict containing mean, first, second, thrid and count keys
     *
     * @return HTTP Object code
     */
    @POST
    @Path("/weather/{iata}/{pointType}")
    Object updateWeather(Exchange e);

    /**
     * Return a list of known airports as a json formatted list
     *
     * @return HTTP Object code and a json formatted list of IATA codes
     */
    @GET
    @Path("/airports")
    @Produces(MediaType.APPLICATION_JSON)
    Object getAirports(Exchange e);

    /**
     * Retrieve airport data, including latitude and longitude for a particular airport
     *
     * @param iata the 3 letter airport code
     * @return an HTTP Object with a json representation of {@link AirportData}
     */
    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    Object getAirport(Exchange e) throws Exception;

    /**
     * Add a new airport to the known airport list.
     *
     * @param iata the 3 letter airport code of the new airport
     * @param latString the airport's latitude in degrees as a string [-90, 90]
     * @param longString the airport's longitude in degrees as a string [-180, 180]
     * @return HTTP Object code for the add operation
     */
    @POST
    @Path("/airport/{iata}/{lat}/{long}")
    Object addAirport(Exchange e) throws Exception;

    /**
     * Remove an airport from the known airport list
     *
     * @param iata the 3 letter airport code
     * @return HTTP Repsonse code for the delete operation
     */
    @DELETE
    @Path("/airport/{iata}")
    Object deleteAirport(Exchange e) throws Exception;

    @GET
    @Path("/exit")
    Object exit();
}
