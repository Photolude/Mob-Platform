package com.photolude.mob.plugin.dal;

import com.photolude.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.photolude.mob.commons.plugins.servicemodel.MainMenuItem;
import com.photolude.mob.commons.plugins.servicemodel.PluginDataCall;
import com.photolude.mob.commons.plugins.servicemodel.PluginDefinition;
import com.photolude.mob.commons.plugins.servicemodel.PluginScript;
import com.photolude.mob.commons.plugins.servicemodel.ServiceAlias;
import com.photolude.mob.commons.plugins.servicemodel.WebPage;

public interface IPluginAccessLayer {
	public PluginScript[] getUserPagePlugins(String userId, String page);
	public PluginDefinition[] getUserPlugins(long staticUserId);

	boolean addPluginToUser(long staticUserId, int pluginId);
	boolean removePluginFromUser(long staticUserId, int pluginId);
	
	public boolean canConnect();
	public PluginScript[] getPageScripts(Long userId, String page);
	
	Integer addPlugin(String pluginName, String company, String version, String role, String tags, String deployIdentity, String externalServices, ServiceAlias[]  serviceAliases, String description, String icon, Integer priority, ExternalAttribution[] attributes);
	void deletePlugin(int pluginId);
	void updatePluginData(int pluginId, String role, String externalServices, ServiceAlias[] serviceAliases, String description, String icon, String tags, Integer priority, ExternalAttribution[] attributeBlob);
	
	Integer addPage(int pluginId, String id);
	void deletePage(int pageId);
	
	Integer addScript(int pluginId, int orderId, String script, String type, String pageName, String scriptName);
	void deleteScript(int scriptId);
	PluginScript[] getPluginScripts(int pluginId);
	
	Integer addMenuItem(int pluginId, String displayName, String icon, String reference, int defaultPriority);
	MainMenuItem[] getPluginMenuItems(int pluginId);
	void deleteMenuItem(int menuId);
	
	Integer addDataCall(int pluginId, String page, String method, String uri, String content, String contentType, String pageVariable);
	PluginDataCall[] getPluginDataCalls(int pluginId);
	void deleteDataCall(int datacallId);
	
	WebPage[] getPages(long userStaticId);
	
	String getCompanyName(String companykey);
	MainMenuItem[] getUserMenuItems(Long staticId);
	Integer getPluginByCompanyNameVersionToken(String companyName, String pluginName, String version, String userToken);
	PluginDefinition[] getPagePlugins(Long staticUserId, String page);
	PluginDefinition[] getPlugins(String token);
	PluginDataCall[] getPageDataCalls(long staticUserId, String page);
	
	PluginDefinition getPluginById(int pluginId);
	String[] getRequiredRoles();
	
}
