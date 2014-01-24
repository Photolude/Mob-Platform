package com.mob.plugin.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mob.commons.plugins.ppl.Alias;
import com.mob.commons.plugins.ppl.AttributeType;
import com.mob.commons.plugins.ppl.DataCallType;
import com.mob.commons.plugins.ppl.ImageType;
import com.mob.commons.plugins.ppl.MainMenuItemType;
import com.mob.commons.plugins.ppl.PageDefinitionType;
import com.mob.commons.plugins.ppl.PluginType;
import com.mob.commons.plugins.ppl.Ppl;
import com.mob.commons.plugins.ppl.Service;
import com.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDataCall;
import com.mob.commons.plugins.servicemodel.PluginScript;
import com.mob.commons.plugins.servicemodel.ServiceAlias;
import com.mob.commons.plugins.utils.PplUtils;
import com.mob.commons.service.clients.IUserServiceClient;
import com.mob.plugin.dal.IPluginDeploymentAccessLayer;

public class DevelopmentDomain implements IDevelopmentDomain {
	
	private int defaultPluginPriority = 50;
	public int getDefaultPluginPriority(){ return this.defaultPluginPriority; }
	public DevelopmentDomain setDefaultPluginPriority(int value)
	{
		this.defaultPluginPriority = value;
		return this;
	}
	
	private IPluginDeploymentAccessLayer dataAccessLayer = null;
	public IPluginDeploymentAccessLayer getDataAccessLayer() { return this.dataAccessLayer; }
	public DevelopmentDomain setDataAccessLayer(IPluginDeploymentAccessLayer value) 
	{ 
		this.dataAccessLayer = value;
		return this;
	}
	
	private IUserServiceClient userAccountService;
	public IUserServiceClient getUserAccountService(){ return this.userAccountService; }
	public DevelopmentDomain setUserAccountService(IUserServiceClient value)
	{
		this.userAccountService = value;
		return this;
	}
	
	public boolean publishPlugin(Ppl pluginRequest)
	{
		try {
			return putPluginInDB(null, pluginRequest);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean DeployPluginForDebugging(String userToken, Ppl pluginRequest)
	{
		Logger logger = Logger.getLogger(this.getClass());
		
		if(userToken == null || userToken.length() == 0)
		{
			logger.warn("The user token provided was null or empty");
			return false;
		}
		
		try {
			return putPluginInDB(userToken, pluginRequest);
		} catch (Exception e) {
			return false;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private boolean putPluginInDB(String userToken, Ppl pluginRequest) throws Exception
	{
		Logger logger = Logger.getLogger(this.getClass());
		
		if(!PplUtils.validate(pluginRequest, true))
		{
			return false;
		}
		
		//
		// Make sure we know who the company is
		//
		String companyName = this.dataAccessLayer.getCompanyName(pluginRequest.getCompanykey());
		
		if(companyName == null || companyName.length() == 0)
		{
			logger.warn("Could not find the company name for key " + pluginRequest.getCompanykey());
			return false;
		}
		
		//
		// Start adding the plug-ins
		//
		
		//
		// create id lists for roll back scenarios
		//
		for(PluginType plugin : pluginRequest.getPlugin())
		{
			//
			// Get previous plugin for deletion later
			//
			Integer previousPluginId = this.dataAccessLayer.getPluginByCompanyNameVersionToken(companyName, plugin.getPluginName(), plugin.getVersion(), userToken);
			
			this.deleteOldPluginCleanup(previousPluginId, userToken);
			
			ServiceAlias[] serviceAliases = null;
			StringBuilder externalServices = new StringBuilder();
			serviceAliases = this.buildAliasData(plugin, externalServices);
			
			//
			// Add new plugin
			//
			Integer pluginId = previousPluginId; 
			
			if(plugin.getPriority() == null)
			{
				plugin.setPriority(this.defaultPluginPriority);
			}
			
			ExternalAttribution[] attributions = null;
			
			if(plugin.getAttribute() != null)
			{
				List<ExternalAttribution> attributionsList = new LinkedList<ExternalAttribution>();
				for(AttributeType item : plugin.getAttribute())
				{
					attributionsList.add(new ExternalAttribution()
											.setLink(item.getLink())
											.setReason(item.getReason()));
				}
				
				attributions = attributionsList.toArray(new ExternalAttribution[attributionsList.size()]);
			}
			
			if(pluginId == null || userToken != null)
			{
				// Plugin does not exist, so create it
				pluginId = this.dataAccessLayer.addPlugin(plugin.getPluginName(), companyName, plugin.getVersion(), plugin.getRole(), plugin.getTags(), userToken, externalServices.toString(), serviceAliases, plugin.getDescription(), plugin.getIcon(), plugin.getPriority(), attributions, plugin.getPublic());
				
				if(pluginId == null)
				{
					throw new Exception("Could not create new plugin");
				}
			}
			else
			{
				if(!this.dataAccessLayer.updatePluginData(pluginId, plugin.getRole(), externalServices.toString(), serviceAliases, plugin.getDescription(), plugin.getIcon(), plugin.getTags(), plugin.getPriority(), attributions, plugin.getPublic()))
				{
					throw new Exception("Could not update plugin");
				}
			}
				
			if(!this.addMenuItem(plugin, pluginId))
			{
				throw new Exception(String.format("Adding plugins failed for %s", plugin.getPluginName()));
			}
				
			Pattern pageName = Pattern.compile("@page=[1-9|a-z|A-Z|\\.|_]*");
				
			for(PageDefinitionType page : plugin.getPageDefinition())
			{
				this.addScripts(plugin, pluginId, page,  pageName);
				this.addHtml(pluginId, page);
				this.addDataCalls(pluginId, page);
			}
				
			this.addArt(plugin, pluginId);
		}
		
		return true;
	}
	
	private void deleteOldPluginCleanup(Integer previousPluginId, String userToken)
	{
		if(previousPluginId != null)
		{
			if(userToken != null)
			{
				// If we're developing then remove the plugin entirely
				this.dataAccessLayer.deletePlugin(previousPluginId);
			}
			else
			{
				//
				// If we're not debugging then we're going to swap out the guts of the plug-in
				//
				MainMenuItem[] items = this.dataAccessLayer.getPluginMenuItems(previousPluginId);
				if(items != null)
				{
					for(MainMenuItem item : items)
					{
						this.dataAccessLayer.deleteMenuItem(item.getId());
					}
				}
				
				PluginScript[] scripts = this.dataAccessLayer.getPluginScripts(previousPluginId);
				if(scripts != null)
				{
					for(PluginScript script : scripts)
					{
						this.dataAccessLayer.deleteScript(script.getId());
					}
				}
				
				PluginDataCall[] calls = this.dataAccessLayer.getPluginDataCalls(previousPluginId);
				if(calls != null)
				{
					for(PluginDataCall call : calls)
					{
						this.dataAccessLayer.deleteDataCall(call.getId());
					}
				}
				
				PluginArt[] artItems = this.dataAccessLayer.getPluginArt(previousPluginId);
				if(artItems != null)
				{
					for(PluginArt art : artItems)
					{
						this.dataAccessLayer.deleteArt(art.getId());
					}
				}
			}
		}
	}
	
	private ServiceAlias[] buildAliasData(PluginType plugin, StringBuilder externalServices)
	{
		ServiceAlias[] retval = null;
		if(plugin.getExternal() != null && plugin.getExternal().getService() != null)
		{
			List<ServiceAlias> aliasList = new LinkedList<ServiceAlias>();
			StringBuilder serviceUrl = new StringBuilder();
			for(Service service : plugin.getExternal().getService())
			{
				externalServices.append(service.getRoot());
				externalServices.append("\n");
				for(Alias alias : service.getAlias())
				{
					serviceUrl.delete(0, serviceUrl.length());
					serviceUrl.append(service.getRoot());
					serviceUrl.append(alias.getUri());
					
					aliasList.add(new ServiceAlias().setEndpoint(serviceUrl.toString()).setName(alias.getName()));
				}
			}
			
			if(aliasList.size() > 0)
			{
				retval = aliasList.toArray(new ServiceAlias[aliasList.size()]);
			}
		}
		
		return retval;
	}
	
	private boolean addMenuItem(PluginType plugin, int pluginId) throws Exception
	{
		boolean retval = true;
		if(plugin.getMainMenu() != null)
		{
			for(MainMenuItemType item : plugin.getMainMenu().getItem())
			{
				Integer menuId = this.dataAccessLayer.addMenuItem(pluginId, item.getDisplayName(), item.getImage(), item.getTarget(), item.getDefaultPriority());
				
				if(menuId == null)
				{
					// Do not return so we can roll back
					throw new Exception(String.format("Could not add in plugin (%s)", item.getDisplayName()));
				}
			}
		}
		
		return retval;
	}
	
	private void addScripts(PluginType plugin, int pluginId, PageDefinitionType page, Pattern pageName) throws Exception
	{
		//
		// Create script content
		//
		for(String script : page.getScript())
		{
			String scriptName = "";
			Matcher matcher = pageName.matcher(script);
			if(matcher.find())
			{
				scriptName = script.substring(matcher.start() + 6, matcher.end());
			}
			
			Integer scriptId = this.dataAccessLayer.addScript(pluginId, 0, script, "text/javascript", page.getId(), scriptName);
			
			if(scriptId == null)
			{
				// Do not return so we can roll back
				throw new Exception("Could not create script.");
			}
		}
		
		//
		// Create style content
		//
		for(String style : page.getStyle())
		{
			String scriptName = "";
			Matcher matcher = pageName.matcher(style);
			if(matcher.find())
			{
				scriptName = style.substring(matcher.start() + 6, matcher.end());
			}
			
			Integer scriptId = this.dataAccessLayer.addScript(pluginId, 0, style, "text/css", page.getId(), scriptName);
			
			if(scriptId == null)
			{
				// Do not return so we can roll back
				throw new Exception("Could not create style");
			}
		}
	}
	
	private void addHtml(Integer pluginId, PageDefinitionType page) throws Exception
	{
		//
		// Create html content
		//
		for(String html : page.getHtml())
		{
			Integer htmlId = this.dataAccessLayer.addScript(pluginId, 1, html, "html", page.getId(), "");
			
			if(htmlId == null)
			{
				// Do not return so we can roll back
				throw new Exception("Could not add html");
			}
		}
	}
	
	private void addDataCalls(Integer pluginId, PageDefinitionType page) throws Exception
	{
		if(page.getDatacall() != null)
		{
			//
			// Create data calls
			//
			for(DataCallType datacall : page.getDatacall())
			{
				// previously verified so this is safe

				Integer datacallId = this.dataAccessLayer.addDataCall(pluginId, page.getId(), datacall.getMethod(), datacall.getUri(), datacall.getContent(), datacall.getContentType(), datacall.getPageVariable());
				
				if(datacallId == null)
				{
					// Do not return so we can roll back
					throw new Exception(String.format("Could not add datacall %s", datacall.getUri()));
				}
			}
		}
	}
	
	private void addArt(PluginType plugin, int pluginId) throws Exception
	{
		if(plugin.getArt() != null)
		{
			for(ImageType image : plugin.getArt().getImage())
			{
				String contentType = "";
				String extension = image.getPath().substring(image.getPath().lastIndexOf(".") + 1);
				if(extension.equals("jpg"))
				{
					contentType = "image/jpeg";
				}
				else
				{
					contentType = "image/" + extension.toLowerCase();
				}
				
				Integer imageId = this.dataAccessLayer.addArt(pluginId, image.getPath(), image.getData(), contentType);
				
				if(imageId == null)
				{
					throw new Exception(String.format("Could not add image %s", image.getPath()));
				}
			}
		}
	}
}
