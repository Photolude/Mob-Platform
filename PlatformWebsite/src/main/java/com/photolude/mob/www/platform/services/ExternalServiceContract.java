package com.photolude.mob.www.platform.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.photolude.mob.plugins.commons.servicemodel.PluginDefinition;
import com.photolude.mob.plugins.commons.servicemodel.PluginPage;

public class ExternalServiceContract implements IServiceContracts {
	private static final String SESSION_EXTERNALS = "externalservices";
	private static final int STATUS_OK = 200;
	
	public void setExternalServices(PluginPage page, HttpSession session)
	{
		List<String> externalServices = new LinkedList<String>();
		
		for(PluginDefinition plugin : page.getPlugins())
		{
			for(String service : plugin.getExternalResources())
			{
				if(service != null && service.length() != 0)
				{
					int count = 0;
					for(int i = 0; i < externalServices.size(); i++)
					{
						String existing = externalServices.get(i);
						
						if(existing.startsWith(service))
						{
							if(count == 0)
							{
								externalServices.set(i, service);
							}
							else
							{
								externalServices.remove(i);
								i--;
							}
							
							count++;
						}
						else if(service.startsWith(existing))
						{
							count++;
						}
					}
					
					if(count == 0)
					{
						externalServices.add(service);
					}
				}
			}
		}
		
		session.setAttribute(SESSION_EXTERNALS, externalServices);
	}
	
	public boolean isCallAllowed(HttpSession session, String serviceCall)
	{
		Object sessionObject = session.getAttribute(SESSION_EXTERNALS);
		if(sessionObject instanceof List)
		{
			@SuppressWarnings("unchecked")
			List<String> externalServices = (List<String>)sessionObject;
			
			if(externalServices != null)
			{
				for(String service : externalServices)
				{
					if(serviceCall.startsWith(service))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public String callServiceWithGet(String endpoint) {
		String retval = null;
		Logger logger = Logger.getLogger(this.getClass());
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		try {
			HttpResponse response = client.execute(new HttpGet(endpoint));
			
			if(response.getStatusLine().getStatusCode() == STATUS_OK)
			{
				// Copy output to retval
				InputStream content = response.getEntity().getContent();
				StringWriter writer = new StringWriter();
				IOUtils.copy(content, writer);
				retval = writer.toString();
				
				content.close();
			}
		} catch (URIException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		} catch (URISyntaxException e) {
			logger.warn(e);
		}
		
		return retval;
	}
}
