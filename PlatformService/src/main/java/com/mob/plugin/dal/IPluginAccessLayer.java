package com.mob.plugin.dal;

import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDataCall;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginScript;
import com.mob.commons.plugins.servicemodel.WebPage;

public interface IPluginAccessLayer {
	public PluginDefinition[] getUserPlugins(long staticUserId);

	boolean addPluginToUser(long staticUserId, int pluginId);
	boolean removePluginFromUser(long staticUserId, int pluginId);
	
	public boolean canConnect();
	public PluginScript[] getPageScripts(Long userId, String page);
	
	WebPage[] getPages(long userStaticId);
	
	MainMenuItem[] getUserMenuItems(Long staticId);
	PluginDefinition[] getPagePlugins(Long staticUserId, String page);
	PluginDefinition[] getPlugins(String token);
	PluginDataCall[] getPageDataCalls(long staticUserId, String page);
	PluginDefinition getPluginByUserAndRole(String userToken, String role);
	
	PluginDefinition getPluginById(int pluginId);
	String[] getRequiredRoles();
	
	PluginArt getArt(String userToken, String role, String artPath);
}
