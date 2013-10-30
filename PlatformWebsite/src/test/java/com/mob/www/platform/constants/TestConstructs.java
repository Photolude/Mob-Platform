package com.mob.www.platform.constants;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class TestConstructs {
	public static Map<String,String> getTokenMap(String[] tokenList)
	{
		Map<String,String> retval = new HashMap<String,String>();
		
		if(tokenList != null)
		{
			for(String key : tokenList)
			{
				retval.put(key, "data:" + key);
			}
		}
		
		return retval;
	}
	
	public static HttpResponse getHttpResponse(int statusCode)
	{
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(statusCode);
		
		HttpResponse retval = mock(HttpResponse.class);
		when(retval.getStatusLine()).thenReturn(statusLine);
		
		return retval;
	}
}
