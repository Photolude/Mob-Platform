package com.mob.plugin.dal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDataCall;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginScript;
import com.mob.commons.plugins.servicemodel.ServiceAlias;
import com.mob.commons.plugins.servicemodel.WebPage;
import com.mysql.jdbc.StringUtils;

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

	@Override
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
					retval.add(readPluginScript(results));
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
					retval.add(readMenuItem(results));
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
				CallableStatement statement = connection.prepareCall("call addPluginToUser(?,?)");
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
	public boolean removePluginFromUser(long staticUserId, int pluginId) {
		boolean retval = false;
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call removePluginFromUser(?,?)");
				statement.setLong(1, staticUserId);
				statement.setInt(2, pluginId);
				
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
	public PluginDefinition[] getPlugins(String token) {
		List<PluginDefinition> retval = new ArrayList<PluginDefinition>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPlugins(?)");
				statement.setString(1, token);
				
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
	public PluginDataCall[] getPageDataCalls(long staticUserId, String page)
	{
		List<PluginDataCall> retval = new ArrayList<PluginDataCall>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getDataCall(?, ?)");
				statement.setLong(1, staticUserId);
				statement.setString(2, page);
				
				ResultSet results = statement.executeQuery();
				
				while(results.next())
				{
					retval.add(readDataCall(results));
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval.toArray(new PluginDataCall[retval.size()]);
	}
		
	@Override
	public PluginDefinition getPluginById(int pluginId) {
		PluginDefinition retval = null;
		Connection connection = this.openConnection();
		
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPluginById(?)");
				statement.setInt(1, pluginId);
				
				ResultSet results = statement.executeQuery();
				if(results != null && results.next())
				{
					retval = readPluginDefinition(results);
					results.close();
				}
				
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval;
	}

	@Override
	public String[] getRequiredRoles() {
		List<String> retval = new ArrayList<String>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getRequiredRoles()");
				
				ResultSet results = statement.executeQuery();
				
				while(results.next())
				{
					retval.add(results.getString(1));
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval.toArray(new String[retval.size()]);
	}
	
	@Override
	public PluginArt getArt(String userToken, String role, String artPath) {
		PluginArt retval = null;
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call getPluginArtByPath(?,?,?)");
				statement.setString(1, userToken); 
				statement.setString(2, role);
				statement.setString(3, artPath);
				
				ResultSet results = statement.executeQuery();
				
				if(results.next())
				{
					retval = readArt(results);
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
	public PluginDefinition getPluginByUserAndRole(String userToken, String role) {
		PluginDefinition retval = null;
		Connection connection = this.openConnection();
		
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPluginByUserAndRole(?,?)");
				statement.setString(1, userToken);
				statement.setString(2, role);
				
				ResultSet results = statement.executeQuery();
				if(results != null && results.next())
				{
					retval = readPluginDefinition(results);
					results.close();
				}
				else
				{
					Logger logger = Logger.getLogger(this.getClass());
					logger.error("No results for user " + userToken + " , role " + role);
				}
				
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval;
	}
	
	private MainMenuItem readMenuItem(ResultSet results) throws SQLException
	{
		return new MainMenuItem()
					.setId(results.getInt("idmenu"))
					.setDisplayName(results.getString("displayName"))
					.setIconData(results.getString("icon"))
					.setTarget(results.getString("reference"))
					.setPriority(results.getInt("defaultPriority"));
	}
	
	private PluginDefinition readPluginDefinition(ResultSet results) throws SQLException
	{
		PluginDefinition retval = new PluginDefinition()
									.setId(results.getInt("idplugin"))
									.setCompany(results.getString("company"))
									.setName(results.getString("name"))
									.setVersion(results.getString("version"))
									.setRole(results.getString("role"))
									.setDescription(results.getString("description"))
									.setIcon(results.getString("icon"))
									.setTags(results.getString("tags"))
									.setPriority(results.getInt("priority"))
									.setExternalServices(results.getString("externalservices"))
									.setServiceAliases(readBlob(results.getString("serviceCalls"), ServiceAlias[].class))
									.setAttributions(readBlob(results.getString("attributeBlob"), ExternalAttribution[].class));

		return retval;
	}
	
	private PluginScript readPluginScript(ResultSet results) throws SQLException
	{
		return new PluginScript()
					.setId(results.getInt("idscript"))
					.setScript(results.getString("script"))
					.setType(results.getString("type"))
					.setName(results.getString("scriptName"))
					.setPage(results.getString("pageName"));
	}
	
	private PluginDataCall readDataCall(ResultSet results) throws SQLException
	{
		return new PluginDataCall()
					.setId(results.getInt("iddatacall"))
					.setMethod(results.getString("method"))
					.setUri(results.getString("uri"))
					.setPageVariable(results.getString("pageVariable"))
					.setContent(results.getString("content"))
					.setContentType(results.getString("contentType"));
	}
	
	private PluginArt readArt(ResultSet results) throws SQLException
	{
		return new PluginArt()
					.setId(results.getInt("idart"))
					.setData(results.getString("data"))
					.setContentType(results.getString("contentType"));
	}

	private String getObjectBlob(Object obj, String objectTypeName)
	{
		if(obj == null)
		{
			return null;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			mapper.writeValue(stream, obj);
		} catch (JsonGenerationException e) {
			Logger.getLogger(this.getClass()).error("There was an error serializing the " + objectTypeName + ".");
			Logger.getLogger(this.getClass()).debug(e);
		} catch (JsonMappingException e) {
			Logger.getLogger(this.getClass()).error("There was an error serializing the " + objectTypeName + ".");
			Logger.getLogger(this.getClass()).debug(e);
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error("There was an error serializing the " + objectTypeName + ".");
			Logger.getLogger(this.getClass()).debug(e);
		}
		return new String(stream.toByteArray());
	}
	
	private <T> T readBlob(String serializedObject, Class<T> typeClass)
	{
		T retval = null;
		if(!StringUtils.isNullOrEmpty(serializedObject) && typeClass != null)
		{
			ObjectMapper mapper = new ObjectMapper();
			try {
				retval = mapper.readValue(serializedObject.getBytes(), typeClass);
			} catch (JsonParseException e) {
				Logger.getLogger(this.getClass()).error("There was an error parsing the service aliases, this tends to indicate there was a problem with deployment");
				Logger.getLogger(this.getClass()).debug(e);
			} catch (JsonMappingException e) {
				Logger.getLogger(this.getClass()).error("There was an error parsing the service aliases, this tends to indicate there was a problem with deployment");
				Logger.getLogger(this.getClass()).debug(e);
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).error("There was an error parsing the service aliases, this tends to indicate there was a problem with deployment");
				Logger.getLogger(this.getClass()).debug(e);
			}
		}
		
		return retval;
	}
}
