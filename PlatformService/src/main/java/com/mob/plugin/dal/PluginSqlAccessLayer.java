package com.mob.plugin.dal;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDataCall;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginScript;
import com.mob.commons.plugins.servicemodel.WebPage;

@SuppressWarnings("unchecked")
public class PluginSqlAccessLayer extends SqlMapClientDaoSupport  implements IPluginAccessLayer {
	public static final Integer INVALID_PLUGIN_ID = null;
	public static final Integer INVALID_SCRIPT_ID = null;
	private static final Logger logger = Logger.getLogger(PluginSqlAccessLayer.class);
	
	@Override
	public boolean canConnect() {
		boolean retval = false;
		try {
			super.getSqlMapClient().queryForObject("canConnect");
			retval = true;
		} catch (Exception e) {
			logger.error("Could not connect to the database");
			logger.error(e.toString());
		}
		
		return retval;
	}

	@Override
	public WebPage[] getPages(long userStaticId)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("user_staticId", userStaticId);

		return queryForArray("getPages", params, Long.toString(userStaticId), WebPage.class);
	}

	@Override
	public PluginScript[] getPageScripts(Long userStaticId, String page)
	{
		Map<Object,Object> params = new HashMap<Object,Object>();
		params.put("staticUserId", userStaticId);
		params.put("page", page);
		
		return queryForArray("getPageScripts", params, Long.toString(userStaticId), PluginScript.class);
	}
	
	@Override
	public MainMenuItem[] getUserMenuItems(Long userStaticId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userStaticId", userStaticId);
		
		return queryForArray("getUserMenuItems", params, Long.toString(userStaticId), MainMenuItem.class);
	}

	@Override
	public PluginDefinition[] getUserPlugins(long userStaticId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("staticUserId", userStaticId);
		
		return queryForArray("getUserPlugins", params, Long.toString(userStaticId), PluginDefinition.class);
	}

	@Override
	public boolean addPluginToUser(long userStaticId, int pluginId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userStaticId", userStaticId);
		params.put("pluginId", pluginId);
		
		return updateCall("addPluginToUser", params, Long.toString(userStaticId));
	}
	
	@Override
	public boolean removePluginFromUser(long userStaticId, int pluginId) {
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userStaticId", userStaticId);
		params.put("pluginId", pluginId);
		
		return updateCall("removePluginFromUser", params, Long.toString(userStaticId));
	}
	
	@Override
	public PluginDefinition[] getPagePlugins(Long userStaticId, String page) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("staticUserId", userStaticId);
		params.put("page", page);
		
		return queryForArray("getPagePlugins", params, Long.toString(userStaticId), PluginDefinition.class);
	}
	
	@Override
	public PluginDefinition[] getPlugins(String token) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userTempId", token);
		
		return queryForArray("getPlugins", params, token, PluginDefinition.class);
	}
	
	@Override
	public PluginDataCall[] getPageDataCalls(long userStaticId, String page)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("staticUserId", userStaticId);
		params.put("page", page);
		
		return queryForArray("getDataCall", params, Long.toString(userStaticId), PluginDataCall.class);
	}
		
	@Override
	public PluginDefinition getPluginById(int pluginId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("pluginId", pluginId);
		
		return queryForObject("getPluginById", params, "");
	}

	@Override
	public String[] getRequiredRoles() {
		Map<String,Object> params = new HashMap<String,Object>();
		return queryForArray("getRequiredRoles", params, "", String.class);
	}
	
	@Override
	public PluginArt getArt(String userToken, String role, String artPath) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userToken", userToken);
		params.put("role", role);
		params.put("path", artPath);
		
		return queryForObject("getPluginArtByPath", params, userToken);
	}
	
	@Override
	public PluginDefinition getPluginByUserAndRole(String userToken, String role) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userToken", userToken);
		params.put("role", role);
		
		return queryForObject("getPluginByUserAndRole", params, userToken);
	}
	
	private <T> T queryForObject(String queryId, Object params, String userIdentifier)
	{
		T retval = null;
		try {
			retval = (T)super.getSqlMapClient().queryForObject(queryId, params);
		} catch (SQLException e) {
			final String warningFormat = "An exception occured while trying to %s for user %s"; 
			logger.warn(String.format(warningFormat, queryId, userIdentifier));
			logger.debug(e);
		}
		
		return retval;
	}
	
	private <T> T[] queryForArray(String queryId, Object params, String userIdentifier, Class<T> resultClass)
	{
		List<T> retval = null;
		try {
			retval = (List<T>)super.getSqlMapClient().queryForList(queryId, params);
		} catch (SQLException e) {
			final String warningFormat = "An exception occured while trying to %s for user %s"; 
			logger.warn(String.format(warningFormat, queryId, userIdentifier));
			logger.debug(e);
		}
		
		if(retval == null)
		{
			return null;
		}

		return retval.toArray((T[])java.lang.reflect.Array.newInstance(resultClass, retval.size()));
	}
	
	private <T> boolean updateCall(String queryId, Map<String,Object> params, String userIdentifier)
	{
		boolean retval = false;
		
		int updateResult = 0;
		try {
			updateResult = super.getSqlMapClient().update(queryId, params);
		} catch (SQLException e) {
			logger.warn(String.format("An error occured while trying to %s for user %s", queryId, userIdentifier));
			logger.debug(e);
		}
		if(updateResult > 0)
		{
			retval = true;
		}
		
		return retval;
	}
}
