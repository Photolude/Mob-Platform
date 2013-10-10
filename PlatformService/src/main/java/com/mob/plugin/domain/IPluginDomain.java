package com.mob.plugin.domain;

import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginCatalog;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginPage;
import com.mob.commons.plugins.servicemodel.WebPage;

public interface IPluginDomain {
	WebPage[] getPages(String user);
	
	MainMenuItem[] getUserMenu(String userToken);
	PluginDefinition[] getUserPlugins(String userToken);
	PluginDefinition getPluginForRole(String userToken, String role);
	
	PluginPage getPagePlugins(String token, String page);
	PluginCatalog getCatalog(String token);
	boolean installPluginForUser(int pluginId, String userToken);
	boolean uninstallPluginForUser(int pluginId, String userToken);
	String[] getRequiredRoles();
	
	boolean isHealthy();

	PluginArt getArt(String userToken, String role, String artPath);
}
