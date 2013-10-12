package com.mob.www.platform.services;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AnonymousAccess_isAnonymousIdentity_UnitTests {
	private static final String IDENTITY_1_VALID = "1234";
	private static final String IDENTITY_2_VALID = "5678";
	private static final String[] ANONYMOUS_IDENTITIES = new String[] { IDENTITY_1_VALID, IDENTITY_2_VALID };

	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid user
    			IDENTITY_1_VALID,
    			ANONYMOUS_IDENTITIES,
    			true
    		},
    		{
    			// 1. Valid user not first in the list
    			IDENTITY_2_VALID,
    			ANONYMOUS_IDENTITIES,
    			true
    		},
    		{
    			// 2. User who isn't in the list
    			"Not There",
    			ANONYMOUS_IDENTITIES,
    			false
    		},
    		{
    			// 3. Empty user name
    			"",
    			ANONYMOUS_IDENTITIES,
    			false
    		},
    		{
    			// 4. Null user name
    			null,
    			ANONYMOUS_IDENTITIES,
    			false
    		},
    		{
    			// 5. null anonymous users
    			IDENTITY_1_VALID,
    			null,
    			false
    		},
    		{
    			// 6. same user name twice in anonymous users
    			IDENTITY_1_VALID,
    			new String[]{IDENTITY_1_VALID, IDENTITY_1_VALID},
    			true
    		},
		});
	}

	private AnonymousAccess domain = new AnonymousAccess();
	private String identity;
	private boolean expectedResult;
	
	public AnonymousAccess_isAnonymousIdentity_UnitTests(String identity, String[] anonymousIdentities, boolean expectedResult)
	{
		this.identity = identity;
		this.expectedResult = expectedResult;
		
		this.domain.setAvailableIdentities(anonymousIdentities);
	}
	
	@Test
	public void isAnonymousIdentity() throws Exception
	{
		boolean result = this.domain.isAnonymousIdentity(identity);
		
		Assert.assertEquals(this.expectedResult, result);
	}
}
