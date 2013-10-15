package com.mob.plugin.dal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDataCall;
import com.mob.commons.plugins.servicemodel.PluginScript;
import com.mob.commons.plugins.servicemodel.ServiceAlias;
import com.mob.commons.plugins.servicemodel.WebPage;

import com.photolude.dal.*;

public class PluginDeploymentSqlAccessLayer extends MySqlDataAccessLayerBase<PluginDeploymentSqlAccessLayer> implements IPluginDeploymentAccessLayer {
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
	
	public Integer addPlugin(String pluginName, String company, String version, String role, String tags, String deployIdentity, String externalServices, ServiceAlias[] serviceAliases, String description, String icon, Integer priority, ExternalAttribution[] attributions)
	{
		Integer retval = INVALID_PLUGIN_ID;
		
		String attributeBlob = getObjectBlob(attributions, "attributions");
		String servicesBlob = getObjectBlob(serviceAliases, "service aliases");
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call addPlugin(?,?,?,?,?,?,?,?,?,?,?,?)");
				statement.setString(1, pluginName);
				statement.setString(2, company);
				statement.setString(3, version);
				statement.setString(4, role);
				statement.setString(5, tags);
				statement.setString(6,  deployIdentity);
				statement.setString(7, externalServices);
				statement.setString(8, servicesBlob);
				statement.setString(9, description);
				statement.setString(10, icon);
				statement.setInt(11, priority);
				statement.setString(12, attributeBlob);
				
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
	
	@Override
	public void updatePluginData(int pluginId, String role, String externalServices, ServiceAlias[] serviceAliases, String description, String icon, String tags, Integer priority, ExternalAttribution[] attributions)
	{
		String attributeBlob = getObjectBlob(attributions, "attributions");
		String servicesBlob = getObjectBlob(serviceAliases, "service aliases");
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call updatePlugin(?,?,?,?,?,?,?,?,?)");
				statement.setInt(1, pluginId);
				statement.setString(2, role);
				statement.setString(3, externalServices);
				statement.setString(4, servicesBlob);
				statement.setString(5, description);
				statement.setString(6, icon);
				statement.setString(7, tags);
				statement.setInt(8, priority);
				statement.setString(9, attributeBlob);
				
				statement.execute();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
	}
	
	@Override
	public Integer addScript(int pluginId, int orderId, String script, String type, String pageName, String scriptName)
	{
		Integer retval = INVALID_SCRIPT_ID;
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call addScript(?,?,?,?,?,?)");
				statement.setInt(1, pluginId);
				statement.setInt(2, orderId);
				statement.setString(3, script);
				statement.setString(4, type);
				statement.setString(5, pageName);
				statement.setString(6, scriptName);
				
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
	
	public PluginScript[] getPluginScripts(int pluginId)
	{
		List<PluginScript> retval = new ArrayList<PluginScript>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPluginScripts(?)");
				statement.setInt(1, pluginId);
				
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
	public void deleteMenuItem(int id)
	{
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call deleteMenuItem(?)");
				statement.setLong(1, id);
				
				statement.execute();
			}
			catch(SQLException e)
			{
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
	}
	
	@Override
	public MainMenuItem[] getPluginMenuItems(int pluginId) {
		List<MainMenuItem> retval = new ArrayList<MainMenuItem>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPluginMenuItems(?)");
				statement.setLong(1, pluginId);
				
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
	public Integer addDataCall(int pluginId, String page, String method, String uri, String content, String contentType, String pageVariable) {
		Integer retval = null;
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call addDataCall(?,?,?,?,?,?,?)");
				statement.setString(1, page); 
				statement.setString(2, method);
				statement.setString(3, uri);
				statement.setString(4, content);
				statement.setString(5, contentType);
				statement.setString(6, pageVariable);
				statement.setInt(7, pluginId);
				
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
	public PluginDataCall[] getPluginDataCalls(int pluginId) {
		List<PluginDataCall> retval = new ArrayList<PluginDataCall>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPluginDataCall(?)");
				statement.setLong(1, pluginId);
				
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
	public void deleteDataCall(int id) {
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call deleteDataCall(?)");
				statement.setLong(1, id);
				
				statement.execute();
			}
			catch(SQLException e)
			{
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
	}
	
	@Override
	public Integer addArt(int pluginId, String artPath, String artData, String contentType)
	{
		Integer retval = null;
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				
				CallableStatement statement = connection.prepareCall("call addArt(?,?,?,?)");
				statement.setInt(1, pluginId); 
				statement.setString(2, artPath);
				statement.setString(3, artData);
				statement.setString(4, contentType);
				
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
	public PluginArt[] getPluginArt(int pluginId) {
		List<PluginArt> retval = new ArrayList<PluginArt>();
		
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getPluginArt(?)");
				statement.setLong(1, pluginId);
				
				ResultSet results = statement.executeQuery();
				
				while(results.next())
				{
					retval.add(readArt(results));
				}
				results.close();
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
		return retval.toArray(new PluginArt[retval.size()]);
	}

	@Override
	public void deleteArt(int artId) {
		Connection connection = this.openConnection();
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call deleteArt(?)");
				statement.setLong(1, artId);
				
				statement.execute();
			}
			catch(SQLException e)
			{
				Logger logger = Logger.getLogger(this.getClass());
				logger.error(e.getMessage());
			}
		}
		
		this.closeConnection(connection);
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
}
