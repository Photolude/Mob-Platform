package com.mob.user.dal;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class UserAccessLayer extends SqlMapClientDaoSupport implements IUserAccessLayer {
	private static Logger logger = Logger.getLogger(UserAccessLayer.class);
	
	@Override
	public boolean setTemporaryUserId(Long staticUserId, String temporaryId, Date expiration, String sourceData) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("staticId", staticUserId);
		params.put("temporaryId", temporaryId);
		params.put("expiration", expiration);
		params.put("sourceData", sourceData);
		
		return updateCall("setTemporaryUserId", params, Long.toString(staticUserId));
	}

	@Override
	public void removeTemporaryUserId(String temporaryId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("temporaryId", temporaryId);
		
		updateCall("removeTemporaryUserId", params, temporaryId);
	}

	@Override
	public UserLogonData getLogonData(String temporaryId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("temporaryId", temporaryId);
		
		return queryForObject("getLogonData", params, temporaryId);
	}

	@Override
	public Long getUserIdFromToken(String temporaryId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("token", temporaryId);
		
		return queryForObject("getUserIdFromToken", params, temporaryId);
	}

	@Override
	public Long getUserIdFromEmail(String email, String source) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("email", email);
		params.put("source", source);
		
		return queryForObject("getUserIdFromEmail", params, email);
	}
	
	@Override
	public boolean addUser(String email, String userSource, String temporaryId, Date expiration, String sourceData) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("email", email);
		params.put("userSource", userSource);
		params.put("temporaryId", temporaryId);
		params.put("expiration", expiration);
		params.put("sourceData", sourceData);
		
		return updateCall("addUser", params, temporaryId);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T queryForObject(String queryId, Map<String,Object> params, String userIdentifier)
	{
		T retval = null;
		try {
			List<T> retvalList = (List<T>)super.getSqlMapClient().queryForList(queryId, params);
			
			if(retvalList.size() > 0)
			{
				retval = retvalList.get(0);
			}
		} catch (SQLException e) {
			final String warningFormat = "An exception occured while trying to %s for user %s"; 
			logger.warn(String.format(warningFormat, queryId, userIdentifier));
			logger.debug(e);
		}
		
		return retval;
	}
	
	private <T> boolean updateCall(String queryId, Map<String,Object> params, String userIdentifier)
	{
		boolean retval = false;
		
		int updateResult = 0;
		try {
			super.getSqlMapClient().update(queryId, params);
			retval = true;
		} catch (SQLException e) {
			logger.warn(String.format("An error occured while trying to %s for user %s", queryId, userIdentifier));
			logger.debug(e);
		}
		
		return retval;
	}
}
