package com.photolude.mob.plugin.domain;

import com.photolude.mob.plugins.commons.servicemodel.MainMenuItem;
import com.photolude.mob.plugins.commons.servicemodel.PluginCatalog;
import com.photolude.mob.plugins.commons.servicemodel.PluginDefinition;
import com.photolude.mob.plugins.commons.servicemodel.PluginPage;
import com.photolude.mob.plugins.commons.servicemodel.WebPage;

public interface IPluginDomain {
	WebPage[] getPages(String user);
	
	MainMenuItem[] getUserMenu(String userToken);
	PluginDefinition[] getUserPlugins(String userToken);
	PluginPage getPagePlugins(String token, String page);
	PluginCatalog getCatalog(String token);
	
	boolean isHealthy();
}
