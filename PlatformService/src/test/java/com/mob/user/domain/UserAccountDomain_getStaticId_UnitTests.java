package com.mob.user.domain;

import org.junit.Assert;

import org.junit.Test;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.mob.user.dal.IUserAccessLayer;
import com.mob.user.domain.UserAccountDomain;

public class UserAccountDomain_getStaticId_UnitTests {
	private static final String TOKEN_VALID = "Token";
	private static final String SOURCE_VALID = "ValidSource";
	
	private UserAccountDomain domain;
	private IUserAccessLayer dal;
	
	public UserAccountDomain_getStaticId_UnitTests()
	{
		this.dal = mock(IUserAccessLayer.class);
		
		this.domain = new UserAccountDomain()
						.setUserAccessLayer(this.dal);
	}
	
	@Test
	public void getStaticIdFromToken_Valid()
	{
		Mockito.when(this.dal.getUserIdFromToken(TOKEN_VALID)).thenReturn(1L);
		Long staticId = this.domain.getStaticIdFromToken(TOKEN_VALID);
		
		Assert.assertEquals((Long)1L, staticId);
	}
	
	@Test
	public void getStaticIdFromToken_NullToken()
	{
		Mockito.when(this.dal.getUserIdFromToken(null)).thenReturn(1L);
		Long staticId = this.domain.getStaticIdFromToken(null);
		
		Assert.assertEquals((Long)null, staticId);
	}
	
	@Test
	public void getStaticIdFromToken_EmptyToken()
	{
		Mockito.when(this.dal.getUserIdFromToken("")).thenReturn(1L);
		Long staticId = this.domain.getStaticIdFromToken("");
		
		Assert.assertEquals((Long)null, staticId);
	}
	
	@Test
	public void getStaticIdFromEmail_Valid()
	{
		Mockito.when(this.dal.getUserIdFromEmail(TOKEN_VALID, SOURCE_VALID)).thenReturn(1L);
		Long staticId = this.domain.getStaticIdFromEmail(TOKEN_VALID, SOURCE_VALID);
		
		Assert.assertEquals((Long)1L, staticId);
	}
	
	@Test
	public void getStaticIdFromEmail_NullToken()
	{
		Mockito.when(this.dal.getUserIdFromEmail(null, SOURCE_VALID)).thenReturn(1L);
		Long staticId = this.domain.getStaticIdFromEmail(null, SOURCE_VALID);
		
		Assert.assertEquals((Long)null, staticId);
	}
	
	@Test
	public void getStaticIdFromEmail_EmptyToken()
	{
		Mockito.when(this.dal.getUserIdFromEmail("", SOURCE_VALID)).thenReturn(1L);
		Long staticId = this.domain.getStaticIdFromEmail("", SOURCE_VALID);
		
		Assert.assertEquals((Long)null, staticId);
	}
}
