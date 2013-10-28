package com.mob.www.platform.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mob.www.platform.constants.TestConstants;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class ExternalServiceContract_callServiceWithGet_UnitTests {
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid call
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.ENDPOINT_VALID,
    			getTokenMap(new String[]{"usertoken"}),
    			getHttpResponse(200),
    			getHttpResponse(200)
    		},
    		{
    			// 1. Null service call
    			null,
    			TestConstants.ENDPOINT_VALID,
    			getTokenMap(new String[]{"usertoken"}),
    			getHttpResponse(200),
    			null
    		},
    		{
    			// 2. Empty service call
    			"",
    			TestConstants.ENDPOINT_VALID,
    			getTokenMap(new String[]{"usertoken"}),
    			getHttpResponse(200),
    			null
    		},
    		{
    			// 3. Null endpoint
    			TestConstants.SERVICE_CALL_VALID,
    			null,
    			getTokenMap(new String[]{"usertoken"}),
    			getHttpResponse(200),
				null
			},
    		{
    			// 4. Empty endpoint
    			TestConstants.SERVICE_CALL_VALID,
    			"",
    			getTokenMap(new String[]{"usertoken"}),
    			getHttpResponse(200),
				getHttpResponse(200)
			},
    		{
    			// 5. Valid call with token replacement component
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.ENDPOINT_VALID + "/${usertoken}",
    			getTokenMap(new String[]{"usertoken"}),
    			getHttpResponse(200),
				getHttpResponse(200)
			},
    		{
    			// 6. Invalid call with missing token replacement
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.ENDPOINT_VALID + "/${usertoken}/${othertoken}",
    			getTokenMap(new String[]{"usertoken"}),
    			getHttpResponse(200),
				null
			},
		});
	}
	
	private ServiceCallManager service = new ServiceCallManager();
	private ServiceCallContext context;
	private String serviceCall;
	private HttpResponse expectedResponse;
	
	@SuppressWarnings("unchecked")
	public ExternalServiceContract_callServiceWithGet_UnitTests(String serviceCall, String serviceEndpoint, Map<String,String> tokenLookup, HttpResponse httpResponse, HttpResponse expectedResponse) throws InterruptedException, ExecutionException
	{
		this.serviceCall = serviceCall;
		this.expectedResponse = expectedResponse;

		this.context = mock(ServiceCallContext.class);
		when(this.context.getTokenLookup()).thenReturn(tokenLookup);

		IServiceContracts contract = mock(IServiceContracts.class);
		when(contract.getEndpointFromAlias(serviceCall, context)).thenReturn(serviceEndpoint);

		Future<HttpResponse> response = mock(Future.class);
		when(response.get()).thenReturn(httpResponse);

		DefaultHttpAsyncClient client = mock(DefaultHttpAsyncClient.class);
		when(client.execute(any(HttpUriRequest.class), any(FutureCallback.class))).thenReturn(response);
		
		this.service.setHttpClient(client)
					.setServiceContracts(contract);
	}
	
	@Test
	public void callServiceWithGet()
	{
		HttpResponse response = this.service.callServiceWithGet(this.serviceCall, this.context);
		
		if(this.expectedResponse != null)
		{
			Assert.assertEquals(this.expectedResponse.getStatusLine().getStatusCode(), response.getStatusLine().getStatusCode());
		}
		else
		{
			Assert.assertNull(response);
		}
	}
	
	private static Map<String,String> getTokenMap(String[] tokenList)
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
	
	private static HttpResponse getHttpResponse(int statusCode)
	{
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(statusCode);
		
		HttpResponse retval = mock(HttpResponse.class);
		when(retval.getStatusLine()).thenReturn(statusLine);
		
		return retval;
	}
}
