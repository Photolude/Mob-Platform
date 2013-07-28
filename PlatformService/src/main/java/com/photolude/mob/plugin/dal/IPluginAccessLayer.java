package com.photolude.mob.plugin.dal;

import com.photolude.mob.plugins.commons.servicemodel.MainMenuItem;
import com.photolude.mob.plugins.commons.servicemodel.PluginDefinition;
import com.photolude.mob.plugins.commons.servicemodel.PluginScript;
import com.photolude.mob.plugins.commons.servicemodel.WebPage;

public interface IPluginAccessLayer {
	public PluginScript[] getUserPagePlugins(String userId, String page);
	public PluginDefinition[] getUserPlugins(long staticUserId);
	boolean addPluginToUser(long staticUserId, int pluginId);
	
	public boolean canConnect();
	public PluginScript[] getPageScripts(Long userId, String page);
	
	Integer addPlugin(String pluginName, String company, String version, String role, String deployIdentity, String externalServices);
	void deletePlugin(int pluginId);
	
	Integer addPage(int pluginId, String id);
	void deletePage(int pageId);
	
	Integer addScript(int pluginId, int orderId, String script, String type, String pageName);
	void deleteScript(int scriptId);
	
	Integer addMenuItem(int pluginId, String displayName, String icon, String reference, int defaultPriority);
	
	WebPage[] getPages(long userStaticId);
	
	String getCompanyName(String companykey);
	MainMenuItem[] getUserMenuItems(Long staticId);
	public Integer getPluginByCompanyNameVersionToken(String companyName, String pluginName, String version, String userToken);
	public PluginDefinition[] getPagePlugins(Long staticUserId, String page);
	public PluginDefinition[] getPlugins();
}
