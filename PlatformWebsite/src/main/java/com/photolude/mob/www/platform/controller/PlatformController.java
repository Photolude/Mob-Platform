package com.photolude.mob.www.platform.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.photolude.mob.www.platform.model.DataCallResponse;
import com.photolude.mob.www.platform.model.ExternalPostRequest;
import com.photolude.mob.www.platform.model.LogonRequest;
import com.photolude.mob.www.platform.services.IServiceContracts;
import com.photolude.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.photolude.mob.commons.plugins.servicemodel.MainMenuItem;
import com.photolude.mob.commons.plugins.servicemodel.PluginDataCall;
import com.photolude.mob.commons.plugins.servicemodel.PluginDefinition;
import com.photolude.mob.commons.plugins.servicemodel.PluginPage;
import com.photolude.mob.commons.plugins.servicemodel.PluginScript;
import com.photolude.mob.commons.service.clients.IPluginService;
import com.photolude.mob.commons.service.clients.IUserServiceClient;

@Controller
@RequestMapping("/")
public class PlatformController 
{
	public static final String SESSION_USER_TOKEN = "userToken";
	public static final String SESSION_MENU = "menu";
	
	public static final String MODEL_PLUGINS = "plugins"; 
	public static final String MODEL_API_BODY = "contents";
	public static final String MODEL_DATA_CALLS = "datacalls";
	public static final String MODEL_ATTRIBUTIONS = "attributions";
	
	private static final String REQUEST_HEADER_REFERER = "referer";
	
	private static final String REDIRECT_TO_HOME = "redirect:/";
	
	public static final String API_RESPONSE_VIEW = "apiResponse";
	public static final String API_REQUEST_PARAM = "request";
	
	public static final String ACTIVE_SCRIPTS = "activeScripts";
	
	private IUserServiceClient userServiceClient;
	public IUserServiceClient getUserServiceClient(){ return this.userServiceClient; }
	public PlatformController setUserServiceClient(IUserServiceClient value)
	{
		this.userServiceClient = value;
		return this;
	}
	
	private IPluginService pluginService;
	public IPluginService getPluginService(){ return this.pluginService; }
	public PlatformController setPluginService(IPluginService value)
	{
		this.pluginService = value;
		return this;
	}
	
	private IServiceContracts contractService;
	public IServiceContracts getContractService(){ return this.contractService; }
	public PlatformController setContractService(IServiceContracts value)
	{
		this.contractService = value;
		return this;
	}
	
	@RequestMapping(value = "/logon", method = RequestMethod.POST)
	public ModelAndView logon(LogonRequest logonData, HttpServletRequest request)
	{
		if(logonData.getEmail() != null && logonData.getEmail().length() != 0 &&
				logonData.getPassword() != null && logonData.getPassword().length() != 0)
		{
			String userToken = this.userServiceClient.logon(logonData.getEmail(), logonData.getPassword());
			
			if(userToken != null && userToken.length() > 0)
			{
				//
				// Logon succeeded
				//
				HttpSession session = request.getSession();
				session.setAttribute(SESSION_USER_TOKEN, userToken);
				
				MainMenuItem[] menuItems = this.pluginService.getUserMenu(userToken);
				session.setAttribute(SESSION_MENU, menuItems);
			}
			else
			{
				//
				// Logon failed
				//
				return new ModelAndView("redirect:" + request.getHeader(REQUEST_HEADER_REFERER) + "?error=logon");
			}
		}
		else
		{
			//
			// Logon failed
			//
			return new ModelAndView("redirect:" + request.getHeader(REQUEST_HEADER_REFERER) + "?error=logon");
		}
		
		return new ModelAndView(REDIRECT_TO_HOME);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView homePage(HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			MainMenuItem[] menuItems = (MainMenuItem[])session.getAttribute(SESSION_MENU);
			if(menuItems != null && menuItems.length > 0)
			{
				return new ModelAndView("redirect:" + "/apps/" + menuItems[0].getTarget());
			}
		}
		
		return new ModelAndView("index");
	}
	
	@RequestMapping(value = "/apps/{target}", method = RequestMethod.GET)
	public ModelAndView pluginApp(@PathVariable String target, HttpServletRequest request)
	{
		if(target == null || target.length() == 0)
		{
			return new ModelAndView(REDIRECT_TO_HOME);
		}
		HttpSession session = request.getSession();
		String userToken = (String)session.getAttribute(SESSION_USER_TOKEN);
		
		PluginPage page = this.pluginService.getPagePlugins(userToken, target);
		
		if(request.getRequestURI() != null && !request.getRequestURI().equals("/") && (page == null || page.getScripts() == null || page.getScripts().length == 0))
		{
			return new ModelAndView(REDIRECT_TO_HOME);
		}
		
		this.contractService.setExternalServices(page, session);
		
		PluginDataCall[] dataCalls = page.getDataCalls();
		DataCallResponse[] responses = this.contractService.callDataCalls(dataCalls, session, userToken);

		session.setAttribute(ACTIVE_SCRIPTS, page.getScripts());
		
		
		List<ExternalAttribution> attributionsList = new LinkedList<ExternalAttribution>();
		if(page.getPlugins() != null)
		{
			for(PluginDefinition plugin : page.getPlugins())
			{
				if(plugin.getAttributions() != null)
				{
					for(ExternalAttribution attribute : plugin.getAttributions())
					{
						attributionsList.add(attribute);
					}
				}
			}
		}
		
		ModelAndView retval = new ModelAndView("index");
		retval.addObject(MODEL_PLUGINS, page.getScripts());
		retval.addObject(MODEL_DATA_CALLS, responses);
		retval.addObject(MODEL_ATTRIBUTIONS, attributionsList.toArray(new ExternalAttribution[attributionsList.size()]));
		return retval;
	}
	
	@RequestMapping(value = "apps/script/{scriptName:.+}", method = RequestMethod.GET)
	@ResponseBody
	public String getScript(@PathVariable String scriptName, HttpServletRequest request, HttpServletResponse response)
	{
		HttpSession session = request.getSession();
		PluginScript[] scripts = (PluginScript[]) session.getAttribute(ACTIVE_SCRIPTS);
		
		if(scripts == null)
		{
			return "";
		}
		
		String retval = "";
		
		for(PluginScript script : scripts)
		{
			if(script.getName() != null && script.getName().equals(scriptName))
			{
				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/javascript");
				retval = script.getScript();
				break;
			}
		}
		
		return retval;
	}
	
	@RequestMapping(value = "/status/health", method = RequestMethod.GET)
	public ModelAndView healthPage(ModelMap model)
	{
		return new ModelAndView("index");
	}
	
	@RequestMapping(value = "/session/refresh", method = RequestMethod.GET)
	public ModelAndView refreshSession(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		String userToken = (String)session.getAttribute(SESSION_USER_TOKEN);
		
		MainMenuItem[] menuItems = this.pluginService.getUserMenu(userToken);
		session.setAttribute(SESSION_MENU, menuItems);
		
		return new ModelAndView(REDIRECT_TO_HOME);
	}
	
	@RequestMapping(value = "/externalrequest/get", method = RequestMethod.GET)
	public void externalRequestGet(HttpServletRequest request, HttpServletResponse response)
	{
		HttpSession session = request.getSession();
		String serviceCall = request.getParameter(API_REQUEST_PARAM);
		
		if(this.contractService.isCallAllowed(session, serviceCall))
		{
			HttpResponse serviceResponse = this.contractService.callServiceWithGet(session, serviceCall);
			
			if(serviceResponse != null)
			{
				for(Header header : serviceResponse.getAllHeaders())
				{
					response.setHeader(header.getName(), header.getValue());
				}
				
				try {
					InputStream serviceStream = serviceResponse.getEntity().getContent();
					OutputStream output =  response.getOutputStream();
					StreamUtils.copy(serviceStream, output);
				} catch (IllegalStateException e) {
					Logger.getLogger(this.getClass()).warn(e);
					try {
						response.sendError(550);
					} catch (IOException e1) {
						Logger.getLogger(this.getClass()).warn(e1);
						e1.printStackTrace();
					}
				} catch (IOException e) {
					Logger.getLogger(this.getClass()).warn(e);
					try {
						response.sendError(550);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						Logger.getLogger(this.getClass()).warn(e1);
					}
				}
			}
		}
		else
		{
			try {
				response.sendError(500);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value = "/externalrequest/post", method = RequestMethod.POST )
	public void externalRequestPost(ExternalPostRequest requestDetails, HttpServletRequest request, HttpServletResponse response)
	{
		Logger logger = Logger.getLogger(this.getClass());
		HttpSession session = request.getSession();
		if(requestDetails != null && this.contractService.isCallAllowed(session, requestDetails.getRequest()))
		{
			HttpResponse serviceResponse = this.contractService.callServiceWithPost(session, requestDetails.getRequest(), requestDetails.getData(), requestDetails.getRequestDataType());
			
			if(serviceResponse != null)
			{
				try {
					InputStream serviceStream = serviceResponse.getEntity().getContent();
					OutputStream output =  response.getOutputStream();
					StreamUtils.copy(serviceStream, output);
				} catch (IllegalStateException e) {
					logger.warn(e);
					try {
						response.sendError(550);
					} catch (IOException e1) {
						logger.warn(e1);
					}
				} catch (IOException e) {
					logger.warn(e);
					try {
						response.sendError(550);
					} catch (IOException e1) {
						logger.warn(e1);
					}
				}
				for(Header header : serviceResponse.getAllHeaders())
				{
					response.setHeader(header.getName(), header.getValue());
				}
			}
		}
		else
		{
			try {
				response.sendError(500);
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}
	
	@RequestMapping(value = "/externalrequest/put", method = RequestMethod.PUT )
	public void externalRequestPut(ExternalPostRequest requestDetails, HttpServletRequest request, HttpServletResponse response)
	{
		Logger logger = Logger.getLogger(this.getClass());
		HttpSession session = request.getSession();
		if(requestDetails != null && this.contractService.isCallAllowed(session, requestDetails.getRequest()))
		{
			HttpResponse serviceResponse = this.contractService.callServiceWithPut(session, requestDetails.getRequest(), requestDetails.getData(), requestDetails.getRequestDataType());
			
			if(serviceResponse != null)
			{
				try {
					InputStream serviceStream = serviceResponse.getEntity().getContent();
					OutputStream output =  response.getOutputStream();
					StreamUtils.copy(serviceStream, output);
				} catch (IllegalStateException e) {
					logger.warn(e);
					try {
						response.sendError(550);
					} catch (IOException e1) {
						logger.warn(e1);
					}
				} catch (IOException e) {
					logger.warn(e);
					try {
						response.sendError(550);
					} catch (IOException e1) {
						logger.warn(e1);
					}
				}
				for(Header header : serviceResponse.getAllHeaders())
				{
					response.setHeader(header.getName(), header.getValue());
				}
			}
		}
		else
		{
			try {
				response.sendError(500);
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}
}