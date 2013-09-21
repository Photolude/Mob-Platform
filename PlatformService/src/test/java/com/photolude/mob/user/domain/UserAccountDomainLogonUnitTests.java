package com.photolude.mob.user.domain;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.photolude.mob.user.dal.ILogonAccessLayer;
import com.photolude.mob.user.dal.IUserAccessLayer;
import com.photolude.mob.user.domain.UserAccountDomain;

@RunWith(Parameterized.class)
public class UserAccountDomainLogonUnitTests {
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final Long USERID = 1L;
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid Test
    			USERNAME, 
    			PASSWORD,
    			USERID,
    			true,
    			true,
    			true,
    			true
    		},
    		{
    			// 1. logon failed
    			USERNAME, 
    			PASSWORD,
    			null,
    			true,
    			true,
    			false,
    			false
    		},
    		{
    			// 2. null user name
    			null, 
    			PASSWORD,
    			USERID,
    			false,
    			false,
    			false,
    			false
    		},
    		{
    			// 3. null password
    			USERNAME, 
    			null,
    			USERID,
    			false,
    			false,
    			false,
    			false
    		},

    		{
    			// 4. cannot connect to user db
    			USERNAME, 
    			PASSWORD,
    			USERID,
    			true,
    			false,
    			true,
    			false
    		},
		});
	}
    
	private String username;
	private String password;
	private Long userId;
	private boolean ceemCalled;
	private boolean userCalled;
	private boolean callSucceeds;
	
	private UserAccountDomain domain;
	private IUserAccessLayer testUserAccessLayer;
	private ILogonAccessLayer testLogonAccessLayer;
	
	public UserAccountDomainLogonUnitTests(String username, String password, Long userId, boolean ceemCalled, boolean userCall, boolean userCalled, boolean succeeds)
	{
		this.username = username;
		this.password = password;
		this.userId = userId;
		this.ceemCalled = ceemCalled;
		this.userCalled = userCalled;
		this.callSucceeds = succeeds;
		
		this.testUserAccessLayer = mock(IUserAccessLayer.class);
		Mockito.when(this.testUserAccessLayer.setTemporaryUserId(eq(userId), anyString(), any(Date.class))).thenReturn(userCall);
		
		this.testLogonAccessLayer = mock(ILogonAccessLayer.class);
		Mockito.when(this.testLogonAccessLayer.attemptLogOn(username, password)).thenReturn(userId);
		
		this.domain = new UserAccountDomain()
						.setUserAccessLayer(this.testUserAccessLayer)
						.setLogonAccessLayer(this.testLogonAccessLayer);
	}
	
	@Test
	public void testLogon()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 5);
		
		String result = this.domain.logon(this.username, this.password);
		
		if(this.ceemCalled)
		{
			verify(this.testLogonAccessLayer, atLeast(1)).attemptLogOn(this.username, this.password);
		}
		else
		{
			verify(this.testLogonAccessLayer, atMost(0)).attemptLogOn(this.username, this.password);
		}
		
		if(this.userCalled)
		{
			verify(this.testUserAccessLayer, atLeast(1)).setTemporaryUserId(eq(this.userId), any(String.class), any(Date.class));
		}
		else
		{
			verify(this.testUserAccessLayer, atMost(0)).setTemporaryUserId(eq(this.userId), any(String.class), any(Date.class));
		}
		
		if(!this.callSucceeds)
		{
			Assert.assertTrue("Result returned when unexpected", result == null);
		}
		else
		{
			Assert.assertNotNull(result);
		}
	}
}
