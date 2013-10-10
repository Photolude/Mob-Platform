package com.mob.user.domain;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.mob.user.dal.ILogonAccessLayer;
import com.mob.user.dal.IUserAccessLayer;
import com.mob.user.dal.UserLogonData;
import com.mob.user.domain.UserAccountDomain;

@RunWith(Parameterized.class)
public class UserAccountDomainGetLogonDetailsUnitTests {
	private static final String USER_ID_VALID = "valid id";
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid
    			USER_ID_VALID,
    			true,
    			5,
    			true
    		},
    		{
    			// 1. Userid null
    			null,
    			true,
    			5,
    			false
    		},

    		{
    			// 2. Userid empty
    			"",
    			true,
    			5,
    			false
    		},
    		{
    			// 3. Expired
    			USER_ID_VALID,
    			true,
    			-1,
    			false
    		},
    		{
    			// 4. Null UserLogonData
    			USER_ID_VALID,
    			false,
    			5,
    			false
    		},
		});
	}
	
	private UserAccountDomain domain;
	private IUserAccessLayer testUserAccessLayer;
	private ILogonAccessLayer testLogonAccessLayer;
	private String userId;
	private boolean loggedInResult;

	public UserAccountDomainGetLogonDetailsUnitTests(String userId, boolean hasUserData, int expirationMinutes, boolean loggedInResult)
	{
		this.userId = userId;
		this.loggedInResult = loggedInResult;
		
		this.testUserAccessLayer = mock(IUserAccessLayer.class);
		
		if(hasUserData)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, expirationMinutes);
			Mockito.when(this.testUserAccessLayer.getLogonData(userId)).thenReturn(new UserLogonData()
																					.setStaticId(1)
																					.setExpiration(calendar.getTime()));
		}
		else
		{
			Mockito.when(this.testUserAccessLayer.getLogonData(userId)).thenReturn(null);
		}
		
		this.testLogonAccessLayer = mock(ILogonAccessLayer.class);
		
		this.domain = new UserAccountDomain()
						.setUserAccessLayer((IUserAccessLayer) this.testUserAccessLayer)
						.setLogonAccessLayer((ILogonAccessLayer) this.testLogonAccessLayer);
	}
	
	@Test
	public void TestGetLogonDetails()
	{
		Assert.assertEquals(this.loggedInResult, this.domain.isLoggedIn(this.userId));
	}
}
