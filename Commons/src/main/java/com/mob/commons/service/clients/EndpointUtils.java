package com.mob.commons.service.clients;

import java.net.URISyntaxException;

public class EndpointUtils {
	public static String ensureEndpointWellFormed(String value) throws URISyntaxException
	{
		if(value == null)
		{
			throw new URISyntaxException("null", "The value provided was null");
		}
		if(!value.endsWith("/"))
		{
			value += "/";
		}
		
		return value;
	}
}
