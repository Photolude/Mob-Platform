package com.photolude.mob.user.dal;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import com.photolude.dal.SqlDataAccessLayerBase;

public class CeemAccessLayer extends SqlDataAccessLayerBase<CeemAccessLayer> implements ICeemAccessLayer {
	
	@Override
	public Long attemptLogOn(String username, String password) {
		Long retval = null;
		Connection conn = this.getConnection();
		
		if(conn != null)
		{
			try {
				// Generate password hash
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] hash = md.digest(password.getBytes("UTF-8"));
				
				CallableStatement statement = conn.prepareCall("exec AttemptLogOn(?,?)");
				statement.setString(1, username);
				statement.setBytes(2, hash);
				
				ResultSet results = statement.executeQuery();
				
				if(results.next())
				{
					retval = results.getLong(1);
				}
			} catch (SQLException | NoSuchAlgorithmException | UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		this.closeConnection(conn);
		
		return retval;
	}
}
