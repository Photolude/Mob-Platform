package com.photolude.mob.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.photolude.mob.user.domain.IUserAccountDomain;

@Path("/user")
public class UserManagementSvc {
	//private static final int STATUS_NOT_IMPLEMENTED = 501;
	
	private IUserAccountDomain userDomain;
	public IUserAccountDomain getUserDomain(){ return this.userDomain; }
	public UserManagementSvc setUserDomain(IUserAccountDomain value)
	{
		this.userDomain = value;
		return this;
	}
	
	@GET
	@Path("/logon/{username}/{password}")
	public Response logon(@PathParam("username") String username, @PathParam("password") String password)
	{
		String output = this.userDomain.logon(username, password);
		
		Response retval;
		
		if(output == null || output.length() == 0)
		{
			retval = Response.serverError().build();
		}
		else
		{
			retval = Response.ok(output).build();
		}
		
		return retval;
	}
	
	@GET
	@Path("/{usertoken}/logoff")
	public Response logoff(@PathParam("usertoken") String userToken)
	{
		this.userDomain.logoff(userToken);
		return Response.ok().build();
	}
}
