package com.mob.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.mob.commons.plugins.ppl.Ppl;
import com.mob.plugin.domain.IDevelopmentDomain;

@Path("/Plugins")
public class DeploymentService {

	IDevelopmentDomain developmentDomain;
	public IDevelopmentDomain getDevelopmentDomain(){ return this.developmentDomain; }
	public DeploymentService setDevelopmentDomain(IDevelopmentDomain value)
	{
		this.developmentDomain = value;
		return this;
	}
	
	@PUT
	@Path("/user/{UserToken}/deploy")
	@Consumes("application/json")
	@Produces("application/json")
	public Response DeployPluginForDebugging(@PathParam("UserToken") String userToken, Ppl pluginRequest)
	{
		if(this.developmentDomain.DeployPluginForDebugging(userToken, pluginRequest))
		{
			return Response.ok().build();
		}
		
		return Response.serverError().build();
	}
	
	@PUT
	@Path("/publish")
	@Consumes("application/json")
	@Produces("application/json")
	public Response publishPlugin(Ppl pluginRequest)
	{
		if(this.developmentDomain.publishPlugin(pluginRequest))
		{
			return Response.ok().build();
		}
		
		return Response.serverError().build();
	}
}
