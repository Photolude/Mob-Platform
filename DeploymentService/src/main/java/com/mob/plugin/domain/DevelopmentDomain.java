package com.mob.plugin.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

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
		return putPluginInDB(null, pluginRequest);
	}
	
	public boolean DeployPluginForDebugging(String userToken, Ppl pluginRequest)
	{
		Logger logger = Logger.getLogger(this.getClass());
		
		if(userToken == null || userToken.length() == 0)
		{
			logger.warn("The user token provided was null or empty");
			return false;
		}
		
		return putPluginInDB(userToken, pluginRequest);
	}
	
	private boolean putPluginInDB(String userToken, Ppl pluginRequest)
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
		boolean retval = true;
		
		//
		// create id lists for roll back scenarios
		//
		List<Integer> pluginIdRollbackList = new LinkedList<Integer>();
		List<Integer> previousPlugins = new LinkedList<Integer>();
		List<Integer> removeMenuOnCommit = new LinkedList<Integer>();
		List<Integer> removeScriptOnCommit = new LinkedList<Integer>();
		List<Integer> removeDataCallOnCommit = new LinkedList<Integer>();
		List<Integer> removeArtOnCommit = new LinkedList<Integer>();
		List<Integer> menuItemsCreated = new LinkedList<Integer>();
		List<Integer> scriptsCreated = new LinkedList<Integer>();
		List<Integer> datacallsCreated = new LinkedList<Integer>();
		List<Integer> artCreated = new LinkedList<Integer>();
		
		for(PluginType plugin : pluginRequest.getPlugin())
		{
			//
			// Get previous plugin for deletion later
			//
			Integer previousPluginId = this.dataAccessLayer.getPluginByCompanyNameVersionToken(companyName, plugin.getPluginName(), plugin.getVersion(), userToken);
			
			this.prepareForOldPluginCleanup(previousPluginId, userToken, previousPlugins, removeMenuOnCommit, removeScriptOnCommit, removeDataCallOnCommit, removeArtOnCommit);
			
			ServiceAlias[] serviceAliases = null;
			StringBuilder externalServices = new StringBuilder();
			serviceAliases = this.buildAliasData(plugin, externalServices);
			
			//
			// Add new plugin
			//
			Integer pluginId = previousPluginId; 
			Integer newPluginId = null;
			
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
				pluginId = this.dataAccessLayer.addPlugin(plugin.getPluginName(), companyName, plugin.getVersion(), plugin.getRole(), plugin.getTags(), userToken, externalServices.toString(), serviceAliases, plugin.getDescription(), plugin.getIcon(), plugin.getPriority(), attributions);
				newPluginId = pluginId;
			}
			else
			{
				this.dataAccessLayer.updatePluginData(pluginId, plugin.getRole(), externalServices.toString(), serviceAliases, plugin.getDescription(), plugin.getIcon(), plugin.getTags(), plugin.getPriority(), attributions);
			}
			
			if(pluginId != null)
			{
				if(newPluginId != null)
				{
					pluginIdRollbackList.add(newPluginId);
				}
				
				if(!this.addMenuItem(plugin, pluginId, menuItemsCreated))
				{
					retval = false;
					break;
				}
				
				Pattern pageName = Pattern.compile("@page=[1-9|a-z|A-Z|\\.|_]*");
				
				for(PageDefinitionType page : plugin.getPageDefinition())
				{
					if(!this.addScripts(plugin, pluginId, page,  pageName, scriptsCreated) || 
						!this.addHtml(pluginId, page, scriptsCreated) ||
						!this.addDataCalls(pluginId, page, datacallsCreated))
					{
						retval = false;
						break;
					}
				}
				
				if(retval && !this.addArt(plugin, pluginId, artCreated))
				{
					retval = false;
				}
			}
			else
			{
				// Do not return so we can roll back
				retval = false;
			}
			
			if(!retval)
			{
				break;
			}
		}
		
		if(retval)
		{
			//
			// Delete any previous plugins
			//
			for(Integer id : previousPlugins)
			{
				this.dataAccessLayer.deletePlugin(id);
			}
			
			// delete previous menu items
			for(Integer id : removeMenuOnCommit)
			{
				this.dataAccessLayer.deleteMenuItem(id);
			}
			
			// delete previous scripts
			for(Integer id : removeScriptOnCommit)
			{
				this.dataAccessLayer.deleteScript(id);
			}
			
			// delete data calls
			for(Integer id : removeDataCallOnCommit)
			{
				this.dataAccessLayer.deleteDataCall(id);
			}
			
			// delete art
			for(Integer id : removeArtOnCommit)
			{
				this.dataAccessLayer.deleteArt(id);
			}
		}
		else
		{
			//
			// Roll back changes
			//
			for(Integer id : pluginIdRollbackList)
			{
				this.dataAccessLayer.deletePlugin(id);
			}
			
			for(Integer id : menuItemsCreated)
			{
				this.dataAccessLayer.deleteMenuItem(id);
			}
			
			for(Integer id : scriptsCreated)
			{
				this.dataAccessLayer.deleteScript(id);
			}
			
			for(Integer id : datacallsCreated)
			{
				this.dataAccessLayer.deleteDataCall(id);
			}
			
			for(Integer id : artCreated)
			{
				this.dataAccessLayer.deleteArt(id);
			}
		}
		
		return retval;
	}
	
	private void prepareForOldPluginCleanup(Integer previousPluginId, String userToken, List<Integer> previousPlugins, List<Integer> removeMenuOnCommit, List<Integer> removeScriptOnCommit, List<Integer> removeDataCallOnCommit, List<Integer> removeArtOnCommit)
	{
		if(previousPluginId != null)
		{
			if(userToken != null)
			{
				// If we're developing then remove the plugin entirely
				previousPlugins.add(previousPluginId);
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
						removeMenuOnCommit.add(item.getId());
					}
				}
				
				PluginScript[] scripts = this.dataAccessLayer.getPluginScripts(previousPluginId);
				if(scripts != null)
				{
					for(PluginScript script : scripts)
					{
						removeScriptOnCommit.add(script.getId());
					}
				}
				
				PluginDataCall[] calls = this.dataAccessLayer.getPluginDataCalls(previousPluginId);
				if(calls != null)
				{
					for(PluginDataCall call : calls)
					{
						removeDataCallOnCommit.add(call.getId());
					}
				}
				
				PluginArt[] artItems = this.dataAccessLayer.getPluginArt(previousPluginId);
				if(artItems != null)
				{
					for(PluginArt art : artItems)
					{
						removeArtOnCommit.add(art.getId());
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
	
	private boolean addMenuItem(PluginType plugin, int pluginId, List<Integer> menuItemsCreated)
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
					retval = false;
					break;
				}
				
				menuItemsCreated.add(menuId);
			}
		}
		
		return retval;
	}
	
	private boolean addScripts(PluginType plugin, int pluginId, PageDefinitionType page, Pattern pageName, List<Integer> scriptsCreated)
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
				return false;
			}
			
			scriptsCreated.add(scriptId);
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
				return false;
			}
			
			scriptsCreated.add(scriptId);
		}
		
		return true;
	}
	
	private boolean addHtml(Integer pluginId, PageDefinitionType page, List<Integer> scriptsCreated)
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
				return false;
			}
			
			scriptsCreated.add(htmlId);
		}
		
		return true;
	}
	
	private boolean addDataCalls(Integer pluginId, PageDefinitionType page, List<Integer> datacallsCreated)
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
					return false;
				}
				
				datacallsCreated.add(datacallId);
			}
		}
		
		return true;
	}
	
	private boolean addArt(PluginType plugin, int pluginId, List<Integer> artCreated)
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
					return false;
				}
				
				artCreated.add(imageId);
			}
		}
		
		return true;
	}
}
