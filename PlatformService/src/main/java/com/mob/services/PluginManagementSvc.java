package com.mob.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Logger;

import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginCatalog;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginPage;
import com.mob.commons.plugins.servicemodel.WebPage;
import com.mob.plugin.domain.IPluginDomain;

@Path("/Plugins")
public class PluginManagementSvc {
	private static final Logger logger = Logger.getLogger(PluginManagementSvc.class);
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
	@Path("/user/{userToken}/menu")
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
		PluginPage retval = null;
		try {
			retval = this.domain.getPagePlugins(URIUtil.decode(userToken), page);
		} catch (URIException e) {
			logger.error(e);
		}
		
		if(retval == null)
		{
			return Response.status(STATUS_BAD_REQUEST).build();
		}
		return Response.ok(retval).build();
	}
	
	@GET
	@Path("/user/{userToken}/role/{role}")
	@Produces("application/json")
	public Response getUserPluginForRole(@PathParam("userToken") String userToken, @PathParam("role") String role)
	{
		PluginDefinition retval = null;
		try {
			retval = this.domain.getPluginForRole(URIUtil.decode(userToken), role);
		} catch (URIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(retval == null)
		{
			return Response.serverError().build();
		}
		
		return Response.ok(retval).build();
	}
	
	
	@GET
	@Path("/user/{UserToken}/catalog")
	@Produces("application/json")
	public Response getCatalog(@PathParam("UserToken") String userToken)
	{
		PluginCatalog catalog = null;
		try {
			catalog = this.domain.getCatalog(URIUtil.decode(userToken));
		} catch (URIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(catalog == null)
		{
			return Response.serverError().build();
		}
		
		return Response.ok(catalog).build();
	}
	
	@GET
	@Path("/user/{UserToken}/plugin/{PluginId}/install")
	public Response installPluginForUser(@PathParam("PluginId") int pluginId, @PathParam("UserToken") String userToken)
	{
		try {
			if(this.domain.installPluginForUser(pluginId, URIUtil.decode(userToken)))
			{
				return Response.ok().build();
			}
		} catch (URIException e) {
			logger.error(e);
		}
		return Response.serverError().build();
	}
	
	@GET
	@Path("/user/{UserToken}/plugin/{PluginId}/uninstall")
	public Response uninstallPluginForUser(@PathParam("PluginId") int pluginId, @PathParam("UserToken") String userToken)
	{
		try {
			if(this.domain.uninstallPluginForUser(pluginId, URIUtil.decode(userToken)))
			{
				return Response.ok().build();
			}
		} catch (URIException e) {
			logger.error(e);
		}
		return Response.serverError().build();
	}
	
	@GET
	@Path("/user/{UserToken}/art/{role}/{artPath:.+}")
	@Produces("application/json")
	public Response getArt(@PathParam("UserToken") String userToken, @PathParam("role") String role, @PathParam("artPath") String artPath)
	{
		PluginArt art = null;
		try {
			art = this.domain.getArt(URIUtil.decode(userToken), role, artPath);
		} catch (URIException e) {
			logger.error(e);
		}
		
		if(art == null)
		{
			return Response.serverError().build();
		}
		
		return Response.ok(art).build();
	}
	
	@GET
	@Path("/roles/required")
	@Produces("application/json")
	public Response getRequiredRoles()
	{
		String[] roles = this.domain.getRequiredRoles();
		
		if(roles != null)
		{
			return Response.ok(roles).build();
		}
		
		return Response.serverError().build();
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
