package com.photolude.mob.plugin.domain;

import com.photolude.mob.commons.plugins.servicemodel.MainMenuItem;
import com.photolude.mob.commons.plugins.servicemodel.PluginCatalog;
import com.photolude.mob.commons.plugins.servicemodel.PluginDefinition;
import com.photolude.mob.commons.plugins.servicemodel.PluginPage;
import com.photolude.mob.commons.plugins.servicemodel.WebPage;

public interface IPluginDomain {
	WebPage[] getPages(String user);
	
	MainMenuItem[] getUserMenu(String userToken);
	PluginDefinition[] getUserPlugins(String userToken);
	PluginPage getPagePlugins(String token, String page);
	PluginCatalog getCatalog(String token);
	boolean installPluginForUser(int pluginId, String userToken);
	boolean uninstallPluginForUser(int pluginId, String userToken);
	String[] getRequiredRoles();
	
	boolean isHealthy();
}
