package com.mob.user.dal;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.Assert;

import com.mob.user.dal.IUserAccessLayer;
import com.mob.user.dal.UserAccessLayer;
import com.mob.user.dal.UserLogonData;

public class UserAccessLayer_UnitTests {
	
	private static final long USER_ID = 123L;
	private static final String TEMP_ID = "Temp Id";
	
	IUserAccessLayer dal;
	Date timeout;
	Date fudgeTimeout;
	
	public UserAccessLayer_UnitTests()
	{
		this.dal = new UserAccessLayer()
					.setDatabaseUrl("jdbc:mysql://localhost/photolude_unittest")
					.setUserName("serviceuser")
					.setPassword("password");
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 5);
		this.timeout = calendar.getTime();
		calendar.add(Calendar.SECOND, -1);
		this.fudgeTimeout = calendar.getTime();
		
		this.dal.removeTemporaryUserId(TEMP_ID);
	}
	
	@Test
	public void setTemporaryUserId()
	{
		this.dal.setTemporaryUserId(USER_ID, TEMP_ID, this.timeout);
		
		UserLogonData data = this.dal.getLogonData(TEMP_ID);
		Assert.assertNotNull(data);
		Assert.assertEquals(USER_ID, data.getStaticId());
		Assert.assertTrue("Timeout not as expected (expected-ish: " + this.fudgeTimeout + ", actual: " + data.getExpiration() + ")",
				this.fudgeTimeout.before(data.getExpiration()));
	}
	
	@Test
	public void removeTemporaryUserId()
	{
		setTemporaryUserId();
		this.dal.removeTemporaryUserId(TEMP_ID);
		Assert.assertEquals(null, this.dal.getLogonData(TEMP_ID));
	}
}
