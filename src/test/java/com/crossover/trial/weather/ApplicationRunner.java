package com.crossover.trial.weather;

public class ApplicationRunner implements Runnable
{

	@Override
	public void run() {
    	try 
    	{
			WeatherServer.main(new String[]{});
		} 
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}
	}

}
