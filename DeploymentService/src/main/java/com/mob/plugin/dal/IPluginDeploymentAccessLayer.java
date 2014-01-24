package com.mob.plugin.dal;

import com.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDataCall;
import com.mob.commons.plugins.servicemodel.PluginScript;
import com.mob.commons.plugins.servicemodel.ServiceAlias;

public interface IPluginDeploymentAccessLayer {
	boolean canConnect();
	
	Integer addPlugin(String pluginName, String company, String version, String role, String tags, String deployIdentity, String externalServices, ServiceAlias[]  serviceAliases, String description, String icon, Integer priority, ExternalAttribution[] attributes, boolean isPublic);
	boolean deletePlugin(int pluginId);
	boolean updatePluginData(int pluginId, String role, String externalServices, ServiceAlias[] serviceAliases, String description, String icon, String tags, Integer priority, ExternalAttribution[] attributeBlob, boolean isPublic);
	
	Integer addScript(int pluginId, int orderId, String script, String type, String pageName, String scriptName);
	boolean deleteScript(int scriptId);
	PluginScript[] getPluginScripts(int pluginId);
	
	Integer addMenuItem(int pluginId, String displayName, String icon, String reference, int defaultPriority);
	MainMenuItem[] getPluginMenuItems(int pluginId);
	boolean deleteMenuItem(int menuId);
	
	Integer addDataCall(int pluginId, String page, String method, String uri, String content, String contentType, String pageVariable);
	PluginDataCall[] getPluginDataCalls(int pluginId);
	boolean deleteDataCall(int datacallId);
	
	Integer addArt(int pluginId, String artPath, String artData, String contentType);
	PluginArt[] getPluginArt(int pluginId);
	boolean deleteArt(int artId);
	
	String getCompanyName(String companykey);
	Integer getPluginByCompanyNameVersionToken(String companyName, String pluginName, String version, String userToken);
}
