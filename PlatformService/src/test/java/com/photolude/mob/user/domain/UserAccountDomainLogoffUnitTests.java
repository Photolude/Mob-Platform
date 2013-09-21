package com.photolude.mob.user.domain;

import org.junit.Test;

import static org.mockito.Mockito.*;

import com.photolude.mob.user.dal.ILogonAccessLayer;
import com.photolude.mob.user.dal.IUserAccessLayer;
import com.photolude.mob.user.domain.UserAccountDomain;

public class UserAccountDomainLogoffUnitTests {

	private UserAccountDomain domain;
	private IUserAccessLayer testUserAccessLayer;
	private ILogonAccessLayer testCeemAccessLayer;
	
	public UserAccountDomainLogoffUnitTests()
	{
		this.testUserAccessLayer = mock(IUserAccessLayer.class);
		this.testCeemAccessLayer = mock(ILogonAccessLayer.class);
		
		this.domain = new UserAccountDomain()
						.setUserAccessLayer((IUserAccessLayer) this.testUserAccessLayer)
						.setLogonAccessLayer(this.testCeemAccessLayer);
	}
	
	@Test
	public void testLogoff_Valid() throws Exception
	{
		String token = "Valid Token";
		this.domain.logoff(token);
		verify(this.testUserAccessLayer, atLeast(1)).removeTemporaryUserId(token);
	}
	
	@Test
	public void testLogoff_Null() throws Exception
	{
		this.domain.logoff(null);
		verify(this.testUserAccessLayer, atMost(0)).removeTemporaryUserId(null);
	}
	
	@Test
	public void testLogoff_Empty() throws Exception
	{
		this.domain.logoff("");
		verify(this.testUserAccessLayer, atMost(0)).removeTemporaryUserId("");
	}
}
