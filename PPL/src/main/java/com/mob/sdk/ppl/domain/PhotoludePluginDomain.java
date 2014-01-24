package com.mob.sdk.ppl.domain;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationBuilder;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.mob.commons.plugins.ppl.*;
import com.mob.commons.plugins.utils.PplUtils;
import com.mob.sdk.ppl.dal.DefaultSystemAccessLayer;
import com.mob.sdk.ppl.dal.ISystemAccessLayer;


public class PhotoludePluginDomain {
	private Logger logger = Logger.getLogger(this.getClass());
	
	private ISystemAccessLayer systemAccessLayer = new DefaultSystemAccessLayer();
	public ISystemAccessLayer getSystemAccessLayer(){ return this.systemAccessLayer; }
	public PhotoludePluginDomain setSystemAccessLayer(ISystemAccessLayer value)
	{
		this.systemAccessLayer = value;
		return this;
	}
	
	private IPplTransformService transformService = new PplJsonTransformService();
	public IPplTransformService getTransformService(){ return this.transformService; }
	public PhotoludePluginDomain setTransformService(IPplTransformService value)
	{
		this.transformService = value;
		return this;
	}
	
	private IPplCommunicationService communicationService = new HttpCommunicationService();
	public IPplCommunicationService getCommunicationService(){ return this.communicationService; }
	public PhotoludePluginDomain setCommunicationService(IPplCommunicationService value)
	{
		this.communicationService = value;
		return this;
	}
	
	public boolean validate(Ppl ppl)
	{
		constructPplIdentity(ppl);
		return PplUtils.validate(ppl, false);
	}
	
	public boolean deployForDevelopment(Ppl ppl, String target, String token)
	{
		constructPplIdentity(ppl);
		logger.info("Packaging up the plugin module");
		if(!this.transformService.packagePpl(ppl))
		{
			return false;
		}
		
		logger.info("marshalling the object for transport");
		byte[] content = this.transformService.generateTransportPackage(ppl);
		if(content != null)
		{
			logger.info("marshalling the object for transport");
			if(!this.communicationService.deployPlugin(target, token, content))
			{
				logger.error("Server communication failed");
				return false;
			}
		}
		else
		{
			logger.error("Could not transform plugin successfully");
			return false;
		}
		
		return true;
	}
	
	public boolean publish(Ppl ppl, String target)
	{
		constructPplIdentity(ppl);
		logger.info("Packaging up the plugin module");
		if(!this.transformService.packagePpl(ppl))
		{
			return false;
		}
		
		logger.info("marshalling the object for transport");
		byte[] content = this.transformService.generateTransportPackage(ppl);
		if(content != null)
		{
			logger.info("marshalling the object for transport");
			if(!this.communicationService.publishPlugin(target, content))
			{
				logger.error("Server communication failed");
				return false;
			}
		}
		else
		{
			logger.error("Could not transform plugin successfully");
			return false;
		}
		
		return true;
	}
	
	private void constructPplIdentity(Ppl ppl)
	{
		File file = new File(System.getProperty("user.home") + "/.ppl/ppl.properties");
		
		if(file.getAbsoluteFile().exists())
		{
			
			try {
				Configuration config = new PropertiesConfiguration(file.getAbsolutePath());
				
				ppl.setCompanykey(config.getString("company.key"));
				
			} catch (ConfigurationException e) {
				logger.error(e);
			}
		}
	}
}
