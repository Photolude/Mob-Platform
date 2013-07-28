package com.photolude.mob.plugin.domain;

import org.apache.log4j.Logger;

import com.photolude.mob.plugin.dal.IPluginAccessLayer;
import com.photolude.mob.user.domain.IUserAccountDomain;
import com.photolude.mob.plugins.commons.servicemodel.MainMenuItem;
import com.photolude.mob.plugins.commons.servicemodel.PluginCatalog;
import com.photolude.mob.plugins.commons.servicemodel.PluginDefinition;
import com.photolude.mob.plugins.commons.servicemodel.PluginPage;
import com.photolude.mob.plugins.commons.servicemodel.PluginScript;
import com.photolude.mob.plugins.commons.servicemodel.WebPage;

public class PluginDomain implements IPluginDomain {
	private static final int MAX_USER_ID_LENGTH = 100;
	
	private IPluginAccessLayer dataAccessLayer = null;
	public IPluginAccessLayer getDataAccessLayer() { return this.dataAccessLayer; }
	public PluginDomain setDataAccessLayer(IPluginAccessLayer value) 
	{ 
		this.dataAccessLayer = value;
		return this;
	}
	
	private String defaultPluginUser = null;
	public String getDefaultPluginUser(){ return this.defaultPluginUser; }
	public PluginDomain setDefaultPluginUser(String value)
	{
		this.defaultPluginUser = value;
		return this;
	}
	
	/**
	 * 
	 */
	private IUserAccountDomain userAccountService;
	public IUserAccountDomain getUserAccountService(){ return this.userAccountService; }
	public PluginDomain setUserAccountService(IUserAccountDomain value)
	{
		this.userAccountService = value;
		return this;
	}

	@Override
	public WebPage[] getPages(String usertoken) {
		if(usertoken == null || usertoken.length() == 0)
		{
			return null;
		}
		
		Long userStaticId = this.userAccountService.getStaticIdFromToken(usertoken);
		WebPage[] retval = null;
		if(userStaticId != null)
		{
			retval = this.dataAccessLayer.getPages(userStaticId.longValue());
			
			if(retval == null || retval.length == 0)
			{
				//
				// The user is new, so we need to setup their default plug-ins based
				// of the default user settings
				//
				Long defaultId = this.userAccountService.getStaticIdFromEmail(this.defaultPluginUser);
				
				if(defaultId != null)
				{
					PluginDefinition[] defaultPlugins = this.dataAccessLayer.getUserPlugins(defaultId.longValue());
					
					if(defaultPlugins != null && userStaticId != null)
					{
						for(PluginDefinition page : defaultPlugins)
						{
							if(page != null)
							{
								this.dataAccessLayer.addPluginToUser(userStaticId.longValue(), page.getId());
							}
						}
						
						retval = this.dataAccessLayer.getPages(userStaticId.longValue());
					}
				}
				else
				{
					Logger.getLogger(this.getClass()).error("Could not find the default user");
				}
			}
		}
		return retval;
	}
	
	@Override
	public MainMenuItem[] getUserMenu(String userToken) {
		if(userToken == null || userToken.length() == 0)
		{
			return null;
		}
		
		Logger logger = Logger.getLogger(this.getClass());
		
		Long staticId = this.userAccountService.getStaticIdFromToken(userToken);
		if(staticId == null)
		{
			logger.warn("The static id for " + userToken + " could not be found");
			return null;
		}
		
		MainMenuItem[] retval = this.dataAccessLayer.getUserMenuItems(staticId);
		
		if(retval == null || retval.length == 0)
		{
			ConfigureUserPlugins(staticId);
			retval = this.dataAccessLayer.getUserMenuItems(staticId);
		}
		
		return retval;
	}
	
	@Override
	public PluginDefinition[] getUserPlugins(String userToken) {
		if(userToken == null || userToken.trim().length() == 0 || userToken.length() > MAX_USER_ID_LENGTH)
		{
			return null;
		}
		
		Long userStaticId = this.userAccountService.getStaticIdFromToken(userToken);
		
		PluginDefinition[] retval = this.dataAccessLayer.getUserPlugins(userStaticId);
		
		if(retval == null)
		{
			retval = new PluginDefinition[0];
		}
		
		return retval;
	}
	
	@Override
	public PluginPage getPagePlugins(String token, String page) {
		if(token == null || token.length() == 0 || page == null || page.length() == 0)
		{
			return null;
		}
		
		Long staticUserId = this.userAccountService.getStaticIdFromToken(token);
		
		if(staticUserId == null)
		{
			return null;
		}
		
		PluginScript[] scripts = this.dataAccessLayer.getPageScripts(staticUserId, page);
		if(scripts == null)
		{
			scripts = new PluginScript[0];
		}
		
		PluginDefinition[] plugins = this.dataAccessLayer.getPagePlugins(staticUserId, page);
		if(plugins == null)
		{
			plugins = new PluginDefinition[0];
		}
		
		return new PluginPage().setPlugins(plugins).setScripts(scripts);
	}
	
	@Override
	public boolean isHealthy() {
		Logger logger = Logger.getLogger(this.getClass());
		boolean succeeded = false;
		Long defaultId = null;
		WebPage[] pages = null;
		try
		{
			succeeded = this.dataAccessLayer.canConnect();
			
			if(!succeeded) logger.error("Could not connect to server");
			
			if(succeeded)
			{
				defaultId = this.userAccountService.getStaticIdFromEmail(this.defaultPluginUser);
				
				if(defaultId == null)
				{
					succeeded = false;
					logger.error("Could not get default user's static id");
				}
			}
			
			if(succeeded)
			{
				pages = this.dataAccessLayer.getPages(defaultId.longValue());
				succeeded = pages != null;
				
				if(!succeeded) logger.error("Could not get pages from db");
			}
			
		} catch(Exception e)
		{
			succeeded = false;
		}
		
		return succeeded;
	}
	
	private void ConfigureUserPlugins(Long userStaticId)
	{
		//
		// The user is new, so we need to setup their default plug-ins based
		// of the default user settings
		//
		Long defaultId = this.userAccountService.getStaticIdFromEmail(this.defaultPluginUser);
		
		if(defaultId != null)
		{
			PluginDefinition[] defaultPlugins = this.dataAccessLayer.getUserPlugins(defaultId.longValue());
			
			if(defaultPlugins != null && userStaticId != null)
			{
				for(PluginDefinition page : defaultPlugins)
				{
					if(page != null)
					{
						this.dataAccessLayer.addPluginToUser(userStaticId.longValue(), page.getId());
					}
				}
			}
		}
	}
	@Override
	public PluginCatalog getCatalog(String token) {
		Long staticUserId = this.userAccountService.getStaticIdFromToken(token);
		
		if(staticUserId == null)
		{
			return null;
		}
		
		return new PluginCatalog()
					.setPlugins(this.dataAccessLayer.getPlugins())
					.setUserPlugins(this.dataAccessLayer.getUserPlugins(staticUserId));
	}
}
