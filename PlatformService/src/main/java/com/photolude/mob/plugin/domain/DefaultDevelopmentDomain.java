package com.photolude.mob.plugin.domain;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.photolude.mob.plugin.dal.IPluginAccessLayer;
import com.photolude.mob.user.domain.IUserAccountDomain;
import com.photolude.mob.plugins.commons.ppl.MainMenuItemType;
import com.photolude.mob.plugins.commons.ppl.PageDefinitionType;
import com.photolude.mob.plugins.commons.ppl.PluginType;
import com.photolude.mob.plugins.commons.ppl.Ppl;
import com.photolude.mob.plugins.commons.utils.PplUtils;

public class DefaultDevelopmentDomain implements IDevelopmentDomain {
	
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
	
	public boolean DeployPluginForDebugging(String userToken, Ppl pluginRequest)
	{
		Logger logger = Logger.getLogger(this.getClass());
		
		if(userToken == null || userToken.length() == 0)
		{
			logger.warn("The user token provided was null or empty");
			return false;
		}
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
		
		for(PluginType plugin : pluginRequest.getPlugin())
		{
			//
			// Get previous plugin for deletion later
			//
			Integer previousPluginId = this.dataAccessLayer.getPluginByCompanyNameVersionToken(companyName, plugin.getPluginName(), plugin.getVersion(), userToken);
			
			if(previousPluginId != null)
			{
				previousPlugins.add(previousPluginId);
			}
			
			StringBuilder externalServices = new StringBuilder();
			if(plugin.getExternal() != null && plugin.getExternal().getService() != null)
			{
				for(String service : plugin.getExternal().getService())
				{
					if(externalServices.length() != 0)
					{
						externalServices.append(";");
					}
					
					externalServices.append(service);
				}
			}
			
			//
			// Add new plugin
			//
			Integer pluginId = this.dataAccessLayer.addPlugin(plugin.getPluginName(), companyName, plugin.getVersion(), plugin.getRole(), userToken, externalServices.toString());
			
			if(pluginId != null)
			{
				pluginIdRollbackList.add(pluginId);
				
				for(MainMenuItemType item : plugin.getMainMenu().getItem())
				{
					Integer menuId = this.dataAccessLayer.addMenuItem(pluginId, item.getDisplayName(), item.getImage(), item.getTarget(), 50);
					
					if(menuId == null)
					{
						// Do not return so we can roll back
						retval = false;
						break;
					}
				}
				
				if(!retval)
				{
					break;
				}
				
				for(PageDefinitionType page : plugin.getPageDefinition())
				{
					//
					// Add the new page for the plug-in
					//
					Integer pageId = this.dataAccessLayer.addPage(pluginId, page.getId());
					
					if(pageId != null)
					{
						//
						// Create script content
						//
						for(String script : page.getScript())
						{
							Integer scriptId = this.dataAccessLayer.addScript(pluginId, 0, script, "text/javascript", page.getId());
							
							if(scriptId == null)
							{
								// Do not return so we can roll back
								retval = false;
								break;
							}
						}
						
						if(retval)
						{
							//
							// Create html content
							//
							for(String html : page.getHtml())
							{
								Integer scriptId = this.dataAccessLayer.addScript(pluginId, 1, html, "html", page.getId());
								
								if(scriptId == null)
								{
									// Do not return so we can roll back
									retval = false;
									break;
								}
							}
						}
					}
					else
					{
						// Do not return so we can roll back
						retval = false;
						break;
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
		}
		
		return retval;
	}
}
