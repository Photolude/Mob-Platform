package com.mob.www.platform.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.service.clients.IPluginService;
import com.mob.www.platform.services.IArtCache;
import com.mob.www.platform.services.ServiceCallContext;
import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping("/art/")
public class ArtController {
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private IPluginService pluginService;
	public IPluginService getPluginService(){ return this.pluginService; }
	public ArtController setPluginService(IPluginService value)
	{
		this.pluginService = value;
		return this;
	}
	
	private IArtCache cache;
	public IArtCache getCache(){ return this.cache; }
	public ArtController setCache(IArtCache value)
	{
		this.cache = value;
		return this;
	}
	
	@RequestMapping(value = "{role}/{artPath:.+}", method = RequestMethod.GET )
	public void getArt(@PathVariable String role, @PathVariable String artPath, HttpServletRequest request, HttpServletResponse response)
	{
		if(StringUtils.isNullOrEmpty(role) || StringUtils.isNullOrEmpty(artPath))
		{
			response.setStatus(500);
			return;
		}
		
		ServiceCallContext context = null;
		try
		{
			context = new ServiceCallContext(request);
		}
		catch(IllegalArgumentException e)
		{
			response.setStatus(500);
			return;
		}
		
		Map<String,PluginDefinition> definitions = context.getRoleMap();
		
		if(!definitions.containsKey(role))
		{
			String userToken = context.getUserToken();
			PluginDefinition definition = this.pluginService.getPluginForUserRole(userToken, role);
			if(definition == null)
			{
				logger.warn("Couldn't get the plugin definition for the role " + role + " for user " + userToken);
			}
			else
			{
				synchronized(this)
				{
					definitions.put(role, definition);
				}
			}
		}
		PluginDefinition definition =  definitions.get(role);

		PluginArt art = null;
		if(definition != null)
		{
			art = this.cache.getArt(definition.getId(), artPath);
		}
		
		if(art == null)
		{
			String userToken = context.getUserToken();
			art = this.pluginService.getArt(userToken, role, artPath);
			
			if(art == null)
			{
				response.setStatus(500);
				return;
			}
			
			if(definition != null)
			{
				this.cache.cacheArt(definition.getId(), artPath, art);
			}
		}
		
		byte[] bytes = Base64.decodeBase64(art.getData().getBytes());
		
		response.setContentType(art.getContentType());
		response.setStatus(200);
		response.setHeader("ContentType", art.getContentType());
		try {
			OutputStream stream = response.getOutputStream();
			stream.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
