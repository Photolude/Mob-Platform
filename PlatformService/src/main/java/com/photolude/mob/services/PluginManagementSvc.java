package com.photolude.mob.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.photolude.mob.plugin.domain.IDevelopmentDomain;
import com.photolude.mob.plugin.domain.IPluginDomain;
import com.photolude.mob.plugins.commons.ppl.Ppl;
import com.photolude.mob.plugins.commons.servicemodel.MainMenuItem;
import com.photolude.mob.plugins.commons.servicemodel.PluginCatalog;
import com.photolude.mob.plugins.commons.servicemodel.PluginPage;
import com.photolude.mob.plugins.commons.servicemodel.WebPage;

@Path("/Plugins")
public class PluginManagementSvc {
	
	public static final int STATUS_SUCCEEDED = 200;
	public static final int STATUS_BAD_REQUEST = 400;
	public static final int STATUS_NOT_IMPLEMENTED = 501;
	
	IPluginDomain domain = null;
	public IPluginDomain getDomain(){ return this.domain; }
	public PluginManagementSvc setDomain(IPluginDomain value)
	{
		this.domain = value;
		return this;
	}
	
	IDevelopmentDomain developmentDomain;
	public IDevelopmentDomain getDevelopmentDomain(){ return this.developmentDomain; }
	public PluginManagementSvc setDevelopmentDomain(IDevelopmentDomain value)
	{
		this.developmentDomain = value;
		return this;
	}
	
	@GET
	@Path("user/{usertoken}/pages/get")
	@Produces("application/json")
	public Response getPages(@PathParam("usertoken") String usertoken)
	{
		WebPage[] results = this.domain.getPages(usertoken);
		
		if(results == null)
		{
			return Response.status(STATUS_BAD_REQUEST).build();
		}
		
		return Response.ok().entity(results).build();
	}
	
	@GET
	@Path("/user/{userToken}/menu/get")
	@Produces("application/json")
	public Response getUserMenu(@PathParam("userToken") String userToken)
	{
		MainMenuItem[] items = this.domain.getUserMenu(userToken);
		
		if(items == null)
		{
			return Response.serverError().build();
		}
		
		return Response.ok(items).build();
	}
	
	@GET
	@Path("/user/{userToken}/page/{page}/get")
	@Produces("application/json")
	public Response getUserPagePlugins(@PathParam("userToken") String userToken, @PathParam("page") String page)
	{	
		PluginPage retval = this.domain.getPagePlugins(userToken, page);
		
		if(retval == null)
		{
			return Response.status(STATUS_BAD_REQUEST).build();
		}
		return Response.ok(retval).build();
	}
	
	@PUT
	@Path("/user/{UserToken}/deploy")
	@Consumes("application/json")
	@Produces("application/json")
	public Response DeployPluginForDebugging(@PathParam("UserToken") String userToken, Ppl pluginRequeset)
	{
		if(this.developmentDomain.DeployPluginForDebugging(userToken, pluginRequeset))
		{
			return Response.ok().build();
		}
		
		return Response.serverError().build();
	}
	
	@GET
	@Path("/user/{UserToken}/catalog")
	@Produces("application/json")
	public Response getCatalog(@PathParam("UserToken") String userToken)
	{
		PluginCatalog catalog = this.domain.getCatalog(userToken);
		
		if(catalog == null)
		{
			return Response.serverError().build();
		}
		
		return Response.ok(catalog).build();
	}
	
	@GET
	@Path("/Status/Health")
	public Response HealthStatus()
	{
		if(this.domain.isHealthy())
		{
			return Response.ok().build();
		}

		return Response.serverError().build();
	}
}
