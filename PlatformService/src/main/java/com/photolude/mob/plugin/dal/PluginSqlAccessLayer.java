package com.photolude.mob.plugin.dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.photolude.mob.plugins.commons.servicemodel.MainMenuItem;
import com.photolude.mob.plugins.commons.servicemodel.PluginDefinition;
import com.photolude.mob.plugins.commons.servicemodel.PluginScript;
import com.photolude.mob.plugins.commons.servicemodel.WebPage;

import com.photolude.dal.*;

public class PluginSqlAccessLayer extends MySqlDataAccessLayerBase<PluginSqlAccessLayer> implements IPluginAccessLayer {
	public static final Integer INVALID_PLUGIN_ID = null;
	public static final Integer INVALID_SCRIPT_ID = null;
	
	@Override
	public boolean canConnect() {
		boolean retval = false;
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				Statement statement = connection.createStatement();
				
				statement.execute("Select 1;");
				retval = true;
			} catch (Exception e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error("Could not connect to the database");
				logger.error(e.toString());
			}
		}
		
		this.closeConnection(connection);
		return retval;
	}
	
	@Override
	public PluginScript[] getUserPagePlugins(String userId, String page) {
		List<PluginScript> retval = new ArrayList<PluginScript>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getUserPlugins(?)");
				statement.setString(1, userId);
				ResultSet results = statement.executeQuery();
				
				if(results != null)
				{
					while(results.next())
					{
						retval.add(readPluginScript(results));
					}
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval.toArray(new PluginScript[retval.size()]);
	}
	
	public Integer addPlugin(String pluginName, String company, String version, String role, String deployIdentity, String externalServices)
	{
		Integer retval = INVALID_PLUGIN_ID;
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call addPlugin(?,?,?,?,?,?)");
				statement.setString(1, pluginName);
				statement.setString(2, company);
				statement.setString(3, version);
				statement.setString(4, role);
				statement.setString(5,  deployIdentity);
				statement.setString(6, externalServices);
				
				ResultSet results = statement.executeQuery();
				if(results != null)
				{
					while(results.next())
					{
						retval = results.getInt(1);
					}
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval;
	}
	
	public Integer addScript(int pluginId, int orderId, String script, String type, String pageName)
	{
		Integer retval = INVALID_SCRIPT_ID;
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call addScript(?,?,?,?,?)");
				statement.setInt(1, pluginId);
				statement.setInt(2, orderId);
				statement.setString(3, script);
				statement.setString(4, type);
				statement.setString(5, pageName);
				
				ResultSet results = statement.executeQuery();
				
				if(results.next())
				{
					retval = results.getInt(1);
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		
		return retval;
	}
	
	@Override
	public void deleteScript(int scriptId) {
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call deleteScript(?)");
				statement.setInt(1, scriptId);
				
				statement.execute();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
	}
	
	public WebPage[] getPages(long userStaticId)
	{
		List<WebPage> retval = new ArrayList<WebPage>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPages(?)");
				statement.setLong(1, userStaticId);
				
				ResultSet results = statement.executeQuery();
				
				while(results.next())
				{
					retval.add(new WebPage()
									.setId(results.getInt("idpage"))
									.setName(results.getString("name")));
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval.toArray(new WebPage[retval.size()]);
	}
	
	public PluginScript[] getPageScripts(Long userId, String page)
	{
		List<PluginScript> retval = new ArrayList<PluginScript>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPageScripts(?,?)");
				statement.setString(1, page);
				statement.setLong(2, userId);
				
				ResultSet results = statement.executeQuery();
				
				while(results.next())
				{
					retval.add(new PluginScript()
									.setScript(results.getString("script"))
									.setType(results.getString("type")));
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval.toArray(new PluginScript[retval.size()]);
	}
	
	public void deletePlugin(int pluginId)
	{
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call deletePlugin(?)");
				statement.setInt(1, pluginId);
				
				statement.execute();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
	}
	
	public String getCompanyName(String companykey)
	{
		String retval = null;
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getCompanyName(?)");
				statement.setString(1, companykey);
				ResultSet results = statement.executeQuery();
				
				if(results.next())
				{
					retval = results.getString("name");
				}
				results.close();
			} catch (Exception e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error("Could not connect to the database");
				logger.error(e.toString());
			} 
		}
		
		this.closeConnection(connection);
		return retval;
	}

	@Override
	public Integer addMenuItem(int pluginId, String displayName, String icon, String reference, int defaultPriority) {
		Integer retval = null;
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call addMenuItem(?,?,?,?,?)");
				statement.setString(1, displayName); 
				statement.setString(2, icon);
				statement.setInt(3, pluginId);
				statement.setString(4, reference);
				statement.setInt(5, defaultPriority);
				
				ResultSet results = statement.executeQuery();
				
				if(results.next())
				{
					retval = results.getInt(1);
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval;
	}
	
	@Override
	public MainMenuItem[] getUserMenuItems(Long staticId) {
		List<MainMenuItem> retval = new ArrayList<MainMenuItem>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getUserMenuItems(?)");
				statement.setLong(1, staticId);
				
				ResultSet results = statement.executeQuery();
				
				while(results.next())
				{
					retval.add(new MainMenuItem()
									.setDisplayName(results.getString("displayName"))
									.setIconData(results.getString("icon"))
									.setTarget(results.getString("reference")));
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval.toArray(new MainMenuItem[retval.size()]);
	}
	
	@Override
	public Integer addPage(int pluginId, String name) {
		Integer retval = null;
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call addPage(?,?)");
				statement.setString(1, name); 
				statement.setInt(2, pluginId);
				
				ResultSet results = statement.executeQuery();
				
				if(results.next())
				{
					retval = results.getInt(1);
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval;
	}
	
	@Override
	public Integer getPluginByCompanyNameVersionToken(String companyName, String pluginName, String version, String userToken) 
	{
		Integer retval = null;
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call getPluginByCompanyNameVersionToken(?,?,?,?)");
				statement.setString(1, companyName); 
				statement.setString(2, pluginName);
				statement.setString(3, version);
				statement.setString(4, userToken);
				
				ResultSet results = statement.executeQuery();
				
				if(results.next())
				{
					retval = results.getInt(1);
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval;
	}
	
	@Override
	public void deletePage(int pageId) {
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call deletePage(?)");
				statement.setInt(1, pageId);
				
				statement.execute();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
	}

	@Override
	public PluginDefinition[] getUserPlugins(long staticUserId) {
		List<PluginDefinition> retval = new ArrayList<PluginDefinition>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getUserPlugins(?)");
				statement.setLong(1, staticUserId);
				ResultSet results = statement.executeQuery();
				
				if(results != null)
				{
					while(results.next())
					{
						retval.add(readPluginDefinition(results));
					}
					results.close();
				}
				
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval.toArray(new PluginDefinition[retval.size()]);
	}

	@Override
	public boolean addPluginToUser(long staticUserId, int pluginId) {
		boolean retval = false;
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call addPluginToUser(?)");
				statement.setInt(1, pluginId);
				statement.setLong(2, staticUserId);
				
				statement.execute();
				retval = true;
			} catch (Exception e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error("Could not connect to the database");
				logger.error(e.toString());
			}
		}
		
		this.closeConnection(connection);
		return retval;
	}
	
	@Override
	public PluginDefinition[] getPagePlugins(Long staticUserId, String page) {
		List<PluginDefinition> retval = new ArrayList<PluginDefinition>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPagePlugins(?,?)");
				statement.setLong(1, staticUserId);
				statement.setString(2, page);
				
				ResultSet results = statement.executeQuery();
				
				if(results != null)
				{
					while(results.next())
					{
						retval.add(readPluginDefinition(results));
					}
					results.close();
				}
				
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval.toArray(new PluginDefinition[retval.size()]);
	}
	
	@Override
	public PluginDefinition[] getPlugins() {
		List<PluginDefinition> retval = new ArrayList<PluginDefinition>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPlugins()");
				
				ResultSet results = statement.executeQuery();
				if(results != null)
				{
					while(results.next())
					{
						retval.add(readPluginDefinition(results));
					}
					results.close();
				}
				
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval.toArray(new PluginDefinition[retval.size()]);
	}
	
	private PluginDefinition readPluginDefinition(ResultSet results) throws SQLException
	{
		return new PluginDefinition()
					.setId(results.getInt("idplugin"))
					.setCompany(results.getString("company"))
					.setName(results.getString("name"))
					.setVersion(results.getString("version"))
					.setRawExternalResources(results.getString("externalservices"))
					.setRole(results.getString("role"));
	}
	
	private PluginScript readPluginScript(ResultSet results) throws SQLException
	{
		return new PluginScript()
					.setType(results.getString("type"))
					.setScript(results.getString("script"));
	}
}
