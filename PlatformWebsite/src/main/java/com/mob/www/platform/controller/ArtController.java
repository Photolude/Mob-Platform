package com.mob.www.platform.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping("/art/")
public class ArtController {
	public static final String SESSION_ROLE_MAP = "roleMap";
	
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
		HttpSession session = request.getSession(false);
		if(StringUtils.isNullOrEmpty(role) || StringUtils.isNullOrEmpty(artPath) || session == null)
		{
			response.setStatus(500);
			return;
		}
		
		@SuppressWarnings("unchecked")
		Map<String,PluginDefinition> definitions = (Map<String,PluginDefinition>)session.getAttribute(SESSION_ROLE_MAP);
		
		if(definitions == null)
		{
			synchronized(this)
			{
				definitions = new HashMap<String, PluginDefinition>();
				session.setAttribute(SESSION_ROLE_MAP, definitions);
			}
		}
		
		if(!definitions.containsKey(role))
		{
			String userToken = (String)session.getAttribute(PlatformController.SESSION_USER_TOKEN);
			PluginDefinition definition = this.pluginService.getPluginForUserRole(userToken, role);
			if(definition == null)
			{
				Logger logger = Logger.getLogger(this.getClass());
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
			String userToken = (String)session.getAttribute(PlatformController.SESSION_USER_TOKEN);
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
