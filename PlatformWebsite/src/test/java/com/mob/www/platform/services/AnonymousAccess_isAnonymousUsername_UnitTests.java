package com.mob.www.platform.services;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AnonymousAccess_isAnonymousUsername_UnitTests {
	private static final String USER_1_VALID = "test1";
	private static final String USER_2_VALID = "test1";
	private static final String[] USERNAMES_VALID = new String[]{ USER_1_VALID, USER_2_VALID };
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid user
    			USER_1_VALID,
    			USERNAMES_VALID,
    			true
    		},
    		{
    			// 1. Valid user not first in the list
    			USER_2_VALID,
    			USERNAMES_VALID,
    			true
    		},
    		{
    			// 2. User who isn't in the list
    			"Not There",
    			USERNAMES_VALID,
    			false
    		},
    		{
    			// 3. Empty user name
    			"",
    			USERNAMES_VALID,
    			false
    		},
    		{
    			// 4. Null user name
    			null,
    			USERNAMES_VALID,
    			false
    		},
    		{
    			// 5. null anonymous users
    			USER_1_VALID,
    			null,
    			false
    		},
    		{
    			// 6. same user name twice in anonymous users
    			USER_1_VALID,
    			new String[]{USER_1_VALID, USER_1_VALID},
    			true
    		},
		});
	}
	
	private AnonymousAccess domain = new AnonymousAccess();
	private String username;
	private boolean expectedResult;
	
	public AnonymousAccess_isAnonymousUsername_UnitTests(String username, String[] anonymousUserNames, boolean expectedResult)
	{
		this.username = username;
		this.expectedResult = expectedResult;
		
		this.domain.setUserNames(anonymousUserNames);
	}
	
	@Test
	public void isAnonymousUsername() throws Exception
	{
		boolean result = this.domain.isAnonymousUsername(username);
		
		Assert.assertEquals(this.expectedResult, result);
	}
}
