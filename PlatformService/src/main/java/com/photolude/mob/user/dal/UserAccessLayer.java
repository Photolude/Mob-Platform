package com.photolude.mob.user.dal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;


import com.photolude.dal.MySqlDataAccessLayerBase;

public class UserAccessLayer extends MySqlDataAccessLayerBase<UserAccessLayer> implements IUserAccessLayer {

	@Override
	public boolean setTemporaryUserId(Long staticUserId, String temporaryId, Date expiration) {
		boolean retval = false; 
		Connection connection = this.openConnection();
		
		if(connection != null)
		{
			java.sql.Timestamp date = new java.sql.Timestamp(expiration.getTime());
			
			try {
				CallableStatement statement = connection.prepareCall("call setTemporaryUserId (?,?,?)");
				statement.setLong(1, staticUserId);
				statement.setString(2, temporaryId);
				statement.setTimestamp(3, date);
				
				statement.execute();
				retval = true;
				
			} catch (SQLException e) {
				Logger logger = Logger.getLogger(this.getClass());
				logger.error("call setTemporaryUserId(" + staticUserId + ", '" + temporaryId + "', " + expiration + ")");
				logger.error(e);
			}
		}
		
		this.closeConnection(connection);
		
		return retval;
	}

	@Override
	public void removeTemporaryUserId(String temporaryId) {
		Connection connection = this.openConnection();
		
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call removeTemporaryUserId (?)");
				statement.setString(1, temporaryId);
				statement.execute();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.closeConnection(connection);
	}

	@Override
	public UserLogonData getLogonData(String temporaryId) {
		UserLogonData retval = null; 
		Connection connection = this.openConnection();
		
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getLogonData (?)");
				statement.setString(1, temporaryId);
				
				ResultSet results = statement.executeQuery();
				
				if(results.next())
				{
					retval = new UserLogonData()
								.setStaticId(results.getLong(1))
								.setExpiration(new Date(results.getTimestamp(2).getTime()));
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.closeConnection(connection);
		
		return retval;
	}

	@Override
	public Long getUserIdFromToken(String temporaryId) {
		Logger logger = Logger.getLogger(this.getClass());
		Connection connection = this.openConnection();
		Long retval = null;
		
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getUserIdFromToken (?)");
				statement.setString(1, temporaryId);
				
				ResultSet results = statement.executeQuery();
				
				if(results.next())
				{
					retval = results.getLong(1);
				}
				else
				{
					logger.warn("Could not get an id for " + temporaryId);
				}
				
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		
		this.closeConnection(connection);
		
		return retval;
	}

	@Override
	public Long getUserIdFromEmail(String email) {
		Logger logger = Logger.getLogger(this.getClass());
		Connection connection = this.openConnection();
		Long retval = null;
		
		if(connection != null)
		{
			try {
				CallableStatement statement = connection.prepareCall("call getUserIdFromEmail (?)");
				statement.setString(1, email);
				
				ResultSet results = statement.executeQuery();
				
				if(results.next())
				{
					retval = results.getLong(1);
				}
				else
				{
					logger.warn("Could not get an id for " + email);
				}
				
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		
		this.closeConnection(connection);
		
		return retval;
	}

}
