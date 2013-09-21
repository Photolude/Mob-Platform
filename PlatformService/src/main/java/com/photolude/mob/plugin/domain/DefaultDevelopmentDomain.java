package com.photolude.mob.plugin.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.photolude.mob.commons.plugins.ppl.Alias;
import com.photolude.mob.commons.plugins.ppl.AttributeType;
import com.photolude.mob.commons.plugins.ppl.DataCallType;
import com.photolude.mob.commons.plugins.ppl.MainMenuItemType;
import com.photolude.mob.commons.plugins.ppl.PageDefinitionType;
import com.photolude.mob.commons.plugins.ppl.PluginType;
import com.photolude.mob.commons.plugins.ppl.Ppl;
import com.photolude.mob.commons.plugins.ppl.Service;
import com.photolude.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.photolude.mob.commons.plugins.servicemodel.MainMenuItem;
import com.photolude.mob.commons.plugins.servicemodel.PluginDataCall;
import com.photolude.mob.commons.plugins.servicemodel.PluginScript;
import com.photolude.mob.commons.plugins.servicemodel.ServiceAlias;
import com.photolude.mob.commons.plugins.utils.PplUtils;
import com.photolude.mob.plugin.dal.IPluginAccessLayer;
import com.photolude.mob.user.domain.IUserAccountDomain;

public class DefaultDevelopmentDomain implements IDevelopmentDomain {
	
	private int defaultPluginPriority = 50;
	public int getDefaultPluginPriority(){ return this.defaultPluginPriority; }
	public DefaultDevelopmentDomain setDefaultPluginPriority(int value)
	{
		this.defaultPluginPriority = value;
		return this;
	}
	
	private IPluginAccessLayer dataAccessLayer = null;
	public IPluginAccessLayer getDataAccessLayer() { return this.dataAccessLayer; }
	public DefaultDevelopmentDomain setDataAccessLayer(IPluginAccessLayer value) 
	{ 
		this.dataAccessLayer = value;
		return this;
	}
	
	private IUserAccountDomain userAccountService;
	public IUserAccountDomain getUserAccountService(){ return this.userAccountService; }
	public DefaultDevelopmentDomain setUserAccountService(IUserAccountDomain value)
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
		List<Integer> menuItemsCreated = new LinkedList<Integer>();
		List<Integer> scriptsCreated = new LinkedList<Integer>();
		List<Integer> datacallsCreated = new LinkedList<Integer>();
		
		for(PluginType plugin : pluginRequest.getPlugin())
		{
			//
			// Get previous plugin for deletion later
			//
			Integer previousPluginId = this.dataAccessLayer.getPluginByCompanyNameVersionToken(companyName, plugin.getPluginName(), plugin.getVersion(), userToken);
			
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
				}
			}
			
			ServiceAlias[] serviceAliases = null;
			if(plugin.getExternal() != null && plugin.getExternal().getService() != null)
			{
				List<ServiceAlias> aliasList = new LinkedList<ServiceAlias>();
				StringBuilder serviceUrl = new StringBuilder();
				for(Service service : plugin.getExternal().getService())
				{
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
					serviceAliases = aliasList.toArray(new ServiceAlias[aliasList.size()]);
				}
			}
			
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
				pluginId = this.dataAccessLayer.addPlugin(plugin.getPluginName(), companyName, plugin.getVersion(), plugin.getRole(), plugin.getTags(), userToken, serviceAliases, plugin.getDescription(), plugin.getIcon(), plugin.getPriority(), attributions);
				newPluginId = pluginId;
			}
			else
			{
				this.dataAccessLayer.updatePluginData(pluginId, plugin.getRole(), serviceAliases, plugin.getDescription(), plugin.getIcon(), plugin.getTags(), plugin.getPriority(), attributions);
			}
			
			if(pluginId != null)
			{
				if(newPluginId != null)
				{
					pluginIdRollbackList.add(newPluginId);
				}
				
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
					
					if(!retval)
					{
						break;
					}
				}
				
				Pattern pageName = Pattern.compile("@page=[1-9|a-z|A-Z|\\.]*");
				
				for(PageDefinitionType page : plugin.getPageDefinition())
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
							retval = false;
							break;
						}
						
						scriptsCreated.add(scriptId);
					}
					
					if(retval)
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
								retval = false;
								break;
							}
							
							scriptsCreated.add(htmlId);
						}
					}
					
					if(retval && page.getDatacall() != null)
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
								retval = false;
								break;
							}
							
							datacallsCreated.add(datacallId);
						}
					}
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
			
			for(Integer id : removeDataCallOnCommit)
			{
				this.dataAccessLayer.deleteDataCall(id);
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
		}
		
		return retval;
	}
}
