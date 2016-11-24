package com.crossover.trial.weather.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;

import com.crossover.trial.weather.WeatherCore;
import com.crossover.trial.weather.entities.DataPointType;
import com.crossover.trial.weather.exceptions.ValidationException;

/**
 * This class used for validating input REST-paramaters
 * 
 * @author LEIG
 *
 */
public class InputValidator 
{

	private final HashMap<String, Pattern> validators = new HashMap<String, Pattern>() {{
		put("iata", Pattern.compile("[A-Z]{3,3}")); 
		put("pointType", null); 
		put("lat", Pattern.compile("-{0,1}\\d+(\\.\\d+){0,1}")); 
		put("long", Pattern.compile("-{0,1}\\d+(\\.\\d+){0,1}"));
		put("radius", Pattern.compile("\\d+"));
	}};
	
	private final ArrayList<String> headersToValidate = new ArrayList<String>(){{
			add("iata"); add("pointType"); add("lat"); add("long"); add("radius");
		}};
	
	public void validate(Exchange e) throws ValidationException
	{
		for(String header : e.getIn().getHeaders().keySet())
		{
			if(!headersToValidate.contains(header)) continue;
			try
			{
				if(null != validators.get(header))
				{
					Matcher m = null;
					if (!validators.get(header).matcher(e.getIn().getHeader(header, String.class)).matches()){throw new Exception("");}
					
				}
				else if("pointType".equals(header))
				{
					DataPointType.valueOf(DataPointType.class, e.getIn().getHeader(header, String.class));
				}
				if("radius".equals(header) && Double.parseDouble(e.getIn().getHeader(header, String.class)) >= WeatherCore.getInstance().getEathRadius())
					{throw new Exception("");}
			}
			catch(Exception ex)
			{
				throw new ValidationException("Invalid paramater: " + header);
			}
		}
	}
}
