package com.crossover.trial.weather;

import org.apache.camel.main.Main;

import com.crossover.trial.weather.camel.Events;
import com.crossover.trial.weather.camel.routebuilders.WeatherRouteBuilder;

public class WeatherServer
{	
	private Main camel;

	public static void main(String[] args) throws Exception {
		WeatherServer application = new WeatherServer();
		application.boot();
	}

	public void boot() throws Exception {
		camel = new Main();
		camel.addRouteBuilder(new WeatherRouteBuilder());
		camel.addMainListener(new Events(camel));
		camel.run();
	}
}
