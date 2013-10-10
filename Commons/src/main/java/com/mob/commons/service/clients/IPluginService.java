package com.mob.commons.service.clients;

import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginPage;

public interface IPluginService {

	MainMenuItem[] getUserMenu(String userToken);
	PluginPage getPagePlugins(String userToken, String page);
	PluginArt getArt(String userToken, String role, String artPath);
	PluginDefinition getPluginForUserRole(String userToken, String role);
}
