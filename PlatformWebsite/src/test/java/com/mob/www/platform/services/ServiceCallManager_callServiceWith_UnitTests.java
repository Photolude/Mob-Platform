package com.mob.www.platform.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mob.www.platform.constants.TestConstants;
import com.mob.www.platform.constants.TestConstructs;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class ServiceCallManager_callServiceWith_UnitTests {
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid call
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.DATA_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.ENDPOINT_VALID,
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
    			TestConstructs.getHttpResponse(200),
    			false
    		},
    		{
    			// 1. Null service call
    			null,
    			TestConstants.DATA_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.ENDPOINT_VALID,
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
    			null,
    			false
    		},
    		{
    			// 2. Empty service call
    			"",
    			TestConstants.DATA_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.ENDPOINT_VALID,
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
    			null,
    			false
    		},
    		{
    			// 3. Null endpoint
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.DATA_VALID,
    			null,
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
				null,
    			false
			},
    		{
    			// 4. Empty endpoint
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.DATA_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			"",
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
    			null,
    			false
			},
    		{
    			// 5. Valid call with token replacement component
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.DATA_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.ENDPOINT_VALID + "/${usertoken}",
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
    			TestConstructs.getHttpResponse(200),
    			false
			},
    		{
    			// 6. Invalid call with missing token replacement
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.DATA_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.ENDPOINT_VALID + "/${usertoken}/${othertoken}",
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
				null,
    			false
			},
    		{
    			// 7. Valid data with data to be inserted
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.DATA_WITH_INSERT_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.ENDPOINT_VALID,
    			TestConstructs.getTokenMap(new String[]{"usertoken", "otherdata"}),
    			TestConstructs.getHttpResponse(200),
    			TestConstructs.getHttpResponse(200),
    			true
			},
    		{
    			// 8. Missing data
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.DATA_WITH_INSERT_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.ENDPOINT_VALID,
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
    			null,
    			true
			},
    		{
    			// 9. Null data
    			TestConstants.SERVICE_CALL_VALID,
    			null,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.ENDPOINT_VALID,
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
    			TestConstructs.getHttpResponse(200),
    			true
			},
    		{
    			// 10. Empty data
    			TestConstants.SERVICE_CALL_VALID,
    			"",
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.ENDPOINT_VALID,
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
    			TestConstructs.getHttpResponse(200),
    			true
			},
    		{
    			// 11. Empty content type
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.DATA_VALID,
    			"",
    			TestConstants.ENDPOINT_VALID,
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
    			TestConstructs.getHttpResponse(200),
    			true
			},
			{
    			// 12. Null content type
    			TestConstants.SERVICE_CALL_VALID,
    			TestConstants.DATA_VALID,
    			null,
    			TestConstants.ENDPOINT_VALID,
    			TestConstructs.getTokenMap(new String[]{"usertoken"}),
    			TestConstructs.getHttpResponse(200),
    			TestConstructs.getHttpResponse(200),
    			true
			},
		});
	}
	
	private ServiceCallManager service = new ServiceCallManager();
	private ServiceCallContext context;
	private String serviceCall;
	private String data;
	private String contentType;
	private HttpResponse expectedResponse;
	private DefaultHttpAsyncClient client;
	private boolean isDataTest;
	
	@SuppressWarnings("unchecked")
	public ServiceCallManager_callServiceWith_UnitTests(String serviceCall, String data, String contentType, String serviceEndpoint, Map<String,String> tokenLookup, HttpResponse httpResponse, HttpResponse expectedResponse, boolean isDataTest) throws InterruptedException, ExecutionException
	{
		this.serviceCall = serviceCall;
		this.expectedResponse = expectedResponse;
		this.data = data;
		this.contentType = contentType;
		this.isDataTest = isDataTest;

		this.context = mock(ServiceCallContext.class);
		when(this.context.getTokenLookup()).thenReturn(tokenLookup);

		IServiceContracts contract = mock(IServiceContracts.class);
		when(contract.getEndpointFromAlias(serviceCall, context)).thenReturn(serviceEndpoint);

		Future<HttpResponse> response = mock(Future.class);
		when(response.get()).thenReturn(httpResponse);

		this.client = mock(DefaultHttpAsyncClient.class);
		when(this.client.execute(any(HttpUriRequest.class), any(FutureCallback.class))).thenReturn(response);
		
		this.service.setHttpClient(client)
					.setServiceContracts(contract);
	}
	
	@Test
	public void callServiceWithGet()
	{
		HttpResponse response = this.service.callServiceWithGet(this.serviceCall, this.context);
		verifyResult(response, false);
	}
	
	@Test
	public void callServiceWithPost()
	{
		HttpResponse response = this.service.callServiceWithPost(this.serviceCall, this.data, this.contentType, this.context);
		verifyResult(response, true);
	}
	
	@Test
	public void callServiceWithDelete()
	{
		HttpResponse response = this.service.callServiceWithDelete(this.serviceCall, this.data, this.contentType, this.context);
		verifyResult(response, true);
	}
	
	@Test
	public void callServiceWithPut()
	{
		HttpResponse response = this.service.callServiceWithPut(this.serviceCall, this.data, this.contentType, this.context);
		verifyResult(response, true);
	}
	
	private void verifyResult(HttpResponse response, boolean withData)
	{
		if((this.isDataTest && withData) || !this.isDataTest)
		{
			if(this.expectedResponse != null)
			{
				Assert.assertEquals(this.expectedResponse.getStatusLine().getStatusCode(), response.getStatusLine().getStatusCode());
			}
			else
			{
				Assert.assertNull(response);
			}
		}
	}
}
