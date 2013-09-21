package com.photolude.sdk.ppl.domain;

import org.apache.log4j.Logger;

import com.photolude.mob.commons.plugins.ppl.*;
import com.photolude.mob.commons.plugins.utils.PplUtils;

import com.photolude.sdk.ppl.dal.DefaultSystemAccessLayer;
import com.photolude.sdk.ppl.dal.ISystemAccessLayer;

public class PhotoludePluginDomain {
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
		return PplUtils.validate(ppl, false);
	}
	
	public boolean deployForDevelopment(Ppl ppl, String target, String token)
	{
		Logger logger = Logger.getLogger(this.getClass());
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
		Logger logger = Logger.getLogger(this.getClass());
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
}
