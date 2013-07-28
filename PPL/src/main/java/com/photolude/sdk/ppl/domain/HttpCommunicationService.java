package com.photolude.sdk.ppl.domain;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

public class HttpCommunicationService implements IPplCommunicationService {

	public boolean deployPlugin(String target, String token, byte[] content) {
		Logger logger = Logger.getLogger(this.getClass());
		HttpClient client = new DefaultHttpClient();
		
		String endpointUrl = target + "/Plugins/user/" + token + "/deploy";
		
		if(!endpointUrl.startsWith("http"))
		{
			logger.info("transforming target to an endpoint url");
			endpointUrl = "http://" + endpointUrl;
		}
		
		logger.info("Send put message to server: " + endpointUrl);
		HttpPut put = new HttpPut(endpointUrl);
		put.setEntity(new ByteArrayEntity(content));
		HttpResponse response;
		try {
			response = client.execute(put);
		
			if(response.getStatusLine().getStatusCode() == 200)
			{
				logger.info("Package posted");
				return true;
			}
			else
			{
				logger.info("The server responded with a bad status code ( " + response.getStatusLine().getStatusCode() + " )");
			}
		} catch (ClientProtocolException e) {
			logger.info(e);
		} catch (IOException e) {
			logger.info(e);
		}
		
		return false;
	}

}
