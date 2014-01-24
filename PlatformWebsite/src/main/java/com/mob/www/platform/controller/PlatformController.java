package com.mob.www.platform.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.service.clients.IPluginService;
import com.mob.commons.service.clients.IUserServiceClient;
import com.mob.www.platform.model.GooglePlusLogin;
import com.mob.www.platform.model.LogonRequest;
import com.mob.www.platform.services.IAnonymousAccess;
import com.mob.www.platform.services.IServiceContracts;
import com.mob.www.platform.services.ServiceCallContext;
import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping("/")
public class PlatformController 
{
	public static final String MODEL_PLUGINS = "plugins"; 
	public static final String MODEL_API_BODY = "contents";
	public static final String MODEL_DATA_CALLS = "datacalls";
	public static final String MODEL_ATTRIBUTIONS = "attributions";
	public static final String REQUEST_HEADER_REFERER = "referer";
	public static final String REDIRECT_TO_HOME = "redirect:/";
	public static final String API_RESPONSE_VIEW = "apiResponse";
	public static final String API_REQUEST_PARAM = "request";
	
	private static final Logger logger = Logger.getLogger(PlatformController.class);
	
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
	
	private IAnonymousAccess anonymousAccess;
	public IAnonymousAccess getAnonymousAccess(){ return this.anonymousAccess; }
	public PlatformController setAnonymousAccess(IAnonymousAccess value)
	{
		this.anonymousAccess = value;
		return this;
	}
	
	@RequestMapping(value = "/logon", method = RequestMethod.POST)
	public ModelAndView logon(LogonRequest logonData, HttpServletRequest request)
	{
		if(logonData.getEmail() != null && logonData.getEmail().length() != 0 &&
				logonData.getPassword() != null && logonData.getPassword().length() != 0 &&
				(this.anonymousAccess == null || !this.anonymousAccess.isAnonymousUsername(logonData.getEmail())))
		{
			String userToken = this.userServiceClient.logon(logonData.getEmail(), logonData.getPassword());
			
			if(userToken != null && userToken.length() > 0)
			{
				loadUserData(request, userToken);
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
	
	@RequestMapping(value = "/logon/anonymous", method = RequestMethod.GET)
	public ModelAndView logonAnonymous(HttpServletRequest request)
	{
		String userToken = this.anonymousAccess.getDefaultIdentity();
		
		if(userToken != null && userToken.length() > 0)
		{
			loadUserData(request, userToken);
			
			return new ModelAndView(REDIRECT_TO_HOME);
		}
		else
		{
			//
			// Logon failed
			//
			return new ModelAndView("redirect:" + request.getHeader(REQUEST_HEADER_REFERER) + "?error=logon");
		}
	}
	
	@RequestMapping(value = "/logon/googleplus", method = RequestMethod.POST)
	public ModelAndView logonGooglePlus(GooglePlusLogin loginInfo, HttpServletRequest request)
	{
		if(loginInfo != null && !StringUtils.isNullOrEmpty(loginInfo.getToken()))
		{
			String userToken = this.userServiceClient.logonViaGoogle(loginInfo.getToken());
			loadUserData(request, userToken);
			
			return new ModelAndView(REDIRECT_TO_HOME);
		}
		else
		{
			//
			// Logon failed
			//
			return new ModelAndView("redirect:" + request.getHeader(REQUEST_HEADER_REFERER) + "?error=logon");
		}
	}
	
	private void loadUserData(HttpServletRequest request, String userToken)
	{
		//
		// Logon succeeded
		//
		request.getSession();
		ServiceCallContext context = ServiceCallContext.getContext(request);
		context.setUserToken(userToken);
		
		MainMenuItem[] menuItems = this.pluginService.getUserMenu(userToken);
		context.setMenuItems(menuItems);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			context.setMenuData(mapper.writeValueAsString(menuItems).replace("\\", "\\\\").replace("\"", "\\\""));
		} catch (JsonGenerationException e) {
			logger.error("Could not load the user menu for client comsumption.");
		} catch (JsonMappingException e) {
			logger.error("Could not load the user menu for client comsumption.");
		} catch (IOException e) {
			logger.error("Could not load the user menu for client comsumption.");
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView homePage(HttpServletRequest request)
	{
		ServiceCallContext context = ServiceCallContext.getContext(request);
		
		MainMenuItem[] menuItems = context.getMenuItems();
		if(menuItems != null && menuItems.length > 0)
		{
			return new ModelAndView("redirect:" + "/" + menuItems[0].getTarget());
		}
		
		return new ModelAndView("index");
	}
	
	@RequestMapping(value = "/status/health", method = RequestMethod.GET)
	public ModelAndView healthPage(ModelMap model)
	{
		return new ModelAndView("index");
	}
	
	@RequestMapping(value = "/session/refresh", method = RequestMethod.GET)
	public ModelAndView refreshSession(HttpServletRequest request)
	{
		ServiceCallContext context = ServiceCallContext.getContext(request);
		
		String userToken = context.getUserToken();
		
		MainMenuItem[] menuItems = this.pluginService.getUserMenu(userToken);
		context.setMenuItems(menuItems);
		
		return new ModelAndView(REDIRECT_TO_HOME);
	}
}