package com.crossover.trial.weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.crossover.trial.weather.entities.Airport;
import com.crossover.trial.weather.entities.AirportSpec;
import com.crossover.trial.weather.entities.AtmosphericInformation;
import com.crossover.trial.weather.entities.DataPoint;
import com.crossover.trial.weather.entities.DataPointType;
import com.crossover.trial.weather.exceptions.WeatherException;

/**
 * All weather calculation operations designed here
 * 
 * @author LEIG
 *
 */
public class WeatherCore 
{
    /** earth radius in KM */
    private final double R = 6372.8;
    
    /** all known airports */
    private Map<String, AirportSpec> airportData = new ConcurrentHashMap<String, AirportSpec>(); 
    //private List<AirportData> airportData = new ArrayList<>();

    /** atmospheric information for each airport, idx corresponds with airportData */
    // removed
    //private List<AtmosphericInformation> atmosphericInformation = new LinkedList<>();

    /**
     * Internal performance counter to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics {@link #ping()}
     */
    // moved to airport spec
    private long totalRequestCount = 0l; 
    //public Map<Airport, Integer> requestFrequency = new HashMap<Airport, Integer>();

    public Map<Double, Integer> radiusFreq = new ConcurrentHashMap<Double, Integer>();
	
    /**
     * Fill airport data, called at application startup
     */
    public void fillAirportData(List<Airport> aList)
    {
    	for(Airport a : aList)
    	{
    		AirportSpec aSpec = new AirportSpec();
    		aSpec.setAirport(a);
    		airportData.put(a.getIata(), aSpec);
    	}
    }
    
    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp a datapoint object holding pointType data
     *
     * @throws WeatherException if the update can not be completed
     */
    public void addDataPoint(String iataCode, String pointType, DataPoint dp){    	
        updateAtmosphericInformation(getAtmosphericInformation(iataCode), pointType, dp);
    }
    
    /**
     * update atmospheric information with the given data point for the given point type
     *
     * @param ai the atmospheric information object to update
     * @param pointType the data point type as a string
     * @param dp the actual data point
     */
    public void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp){
    	if(null == ai) return;
        final DataPointType dptype = DataPointType.valueOf(pointType.toUpperCase());

        boolean setUpdateTime = true; 
    	switch(dptype)
    	{
    		case WIND:
                if (dp.getMean() < 0) return;
                ai.setWind(dp);                
    			break;
    		case TEMPERATURE:
                if (dp.getMean() < -50 || dp.getMean() >= 100) return;
                ai.setTemperature(dp);
    			break;
    		case HUMIDTY:
                if (dp.getMean() <0 || dp.getMean() >= 100) return;
                ai.setHumidity(dp);
    			break;
    		case PRESSURE:
                if (dp.getMean() < 650 || dp.getMean() > 800) return;                
                ai.setPressure(dp);
    			break;
    		case CLOUDCOVER:
                if (dp.getMean() <0 || dp.getMean() >= 100) return;
                ai.setCloudCover(dp);
    			break;
    		case PRECIPITATION:
                if (dp.getMean() <0  || dp.getMean() >= 100) return;
                ai.setPrecipitation(dp);
    			break;
    		default:
    			setUpdateTime = false;
    			break;
    	}
    	if(setUpdateTime) {ai.setLastUpdateTime(System.currentTimeMillis());}
    }
     
    /**
     * Update frequence information
     */
    public synchronized void updateRequestFrequency(String iataCode, Double radius)
    {
    	if(!airportData.containsKey(iataCode)) return;
    	airportData.get(iataCode).incReqCount();
    	Integer radiusReqCount = radiusFreq.containsKey(radius) ? radiusFreq.get(radius) : 0;
    	++radiusReqCount;
    	++totalRequestCount;
    	radiusFreq.put(radius, radiusReqCount);    	
    }
    
    /**
     * Get Atmospheric Information routine
     * 
     * @param iataCode as a string
     * @return AtmosphericInformation
     */
    public AtmosphericInformation getAtmosphericInformation(String iataCode)
    {
    	if(!airportData.containsKey(iataCode)) return null;
    	return airportData.get(iataCode).getAi();
    }
    
    /**
     * Get Airport by iataCode
     * 
     * @param iataCode as a string
     * @return AtmosphericInformation
     */
    public Airport getAirport(String iataCode)
    {
    	if(!airportData.containsKey(iataCode)) return null;
    	return airportData.get(iataCode).getAirport();
    }
    
    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode as a string
     * @return airport data or null if not found
     */
    public Airport findAirportData(String iataCode) {
    	if(!airportData.containsKey(iataCode)) return null;
        return airportData.get(iataCode).getAirport();
    }
    /**
     * Get request frequency by airports
     */
    public Map<String, Double> getAirportReqFrequency()
    {
    	Map<String, Double> freq = new HashMap<>();
    	
    	if(0 == totalRequestCount) return freq;
    	
        for (AirportSpec data : airportData.values()) {
            double frac = (double) (data.getReqCount() + 0d) / (totalRequestCount + 0d);
            freq.put(data.getAirport().getIata(), frac);
        }
    	
    	return freq;
    }
    /**
     * Count all non-empty datapoints for all airports
     * 
     * @return int count
     */
    public int getDataSize()
    {
    	int result = 0;
    	
    	
        for (AirportSpec as : airportData.values()) {
        	
            // we only count recent readings
            if (!allNulls(
            		new Object[]{
            			as.getAi().getCloudCover(), 
	            		as.getAi().getHumidity(),
		                as.getAi().getPressure(),
		                as.getAi().getPrecipitation(),
		                as.getAi().getTemperature(),
		                as.getAi().getWind()}))            
            {
                // updated in the last day
                if (as.getAi().getLastUpdateTime() <= System.currentTimeMillis() - 86400000) continue;
                
                ++result;
            }
        }
    	    	
    	return result;
    }
    
    private boolean allNulls(Object[] objects)
    {
    	for(Object o : objects)
    	{
    		if(o != null) return false;
    	}
    	return true;
    }
    
    /**
     * Collecting history of requests by radius
     * 
     * Strongly not sure, that such statistics could be useful
     * 
     * @author LEIG
     * 
     * @return 
     */
    public int[] getRadiusReqFrequency()
    {
    	if(0 == radiusFreq.keySet().size()) return new int[]{};
    	
        int m = radiusFreq.keySet().stream()
                .max(Double::compare)
                .orElse(1000.0).intValue() + 1;

        int[] hist = new int[m];
        for (Map.Entry<Double, Integer> e : radiusFreq.entrySet()) {
            int i = e.getKey().intValue() % 10;
            hist[i] += e.getValue();
        }
        return hist;
    }
    

    /**
     * Extracts the requested airport information and return a list of matching atmosphere information.
     * 
     * @param iataCode
     * @param radius
     * @return
     */
    public List<AtmosphericInformation> getAtmosphericInformation(String iataCode, Double radius)
    {
        List<AtmosphericInformation> retval = new ArrayList<AtmosphericInformation>();

        AtmosphericInformation targetAirport = WeatherCore.getInstance().getAtmosphericInformation(iataCode); 
        if(null == targetAirport) return retval;
        
        WeatherCore.getInstance().updateRequestFrequency(iataCode, radius);

        addToList(targetAirport, retval);
        
        if (radius > 0) 
        {
            Airport ad = WeatherCore.getInstance().getAirport(iataCode);
            
            for(AirportSpec as : airportData.values())
            {
            	if(as.getAirport().getIata().equals(iataCode)) continue;
                
            	if (calculateDistance(ad, as.getAirport()) > radius) continue;
                
                addToList(getAtmosphericInformation(as.getAirport().getIata()), retval);
                            	
            }            
        }    
        return retval;
    }
    
    private void addToList(AtmosphericInformation ai, List<AtmosphericInformation> retval)
    {
    	if(null == ai) return;
        if (!allNulls(
        		new Object[]{
        				ai.getCloudCover(), 
        				ai.getHumidity(),
        				ai.getPressure(),
        				ai.getPrecipitation(),
        				ai.getTemperature(),
        				ai.getWind()}))            
        	retval.add(ai);
    	
    }
    
    /**
     * Haversine distance between two airports.
     *
     * @param ad1 airport 1
     * @param ad2 airport 2
     * @return the distance in KM
     */
    public double calculateDistance(Airport ad1, Airport ad2) {
        double deltaLat = Math.toRadians(Double.parseDouble(ad2.getLatitude()) - Double.parseDouble(ad1.getLatitude()));
        double deltaLon = Math.toRadians(Double.parseDouble(ad2.getLongitude()) - Double.parseDouble(ad1.getLongitude()));
        double a =  Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(Double.parseDouble(ad1.getLatitude())) * Math.cos(Double.parseDouble(ad2.getLatitude()));
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
    
    
	private static WeatherCore instance;
	
	public static WeatherCore getInstance()
	{
		if(null == instance)
		{
			instance = new WeatherCore();
		}
		return instance;
	}
	private WeatherCore()
	{
		
	}

	public double getEathRadius() {
		return R;
	}
	
	
	
}
