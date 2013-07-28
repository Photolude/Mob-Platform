package com.photolude.mob.www.platform.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.photolude.mob.www.platform.model.LogonRequest;
import com.photolude.mob.www.platform.services.IPluginService;
import com.photolude.mob.www.platform.services.IServiceContracts;
import com.photolude.mob.www.platform.services.IUserService;
import com.photolude.mob.plugins.commons.servicemodel.MainMenuItem;
import com.photolude.mob.plugins.commons.servicemodel.PluginPage;

@Controller
@RequestMapping("/")
public class PlatformController 
{
	public static final String SESSION_USER_TOKEN = "userToken";
	public static final String SESSION_MENU = "menu";
	
	public static final String MODEL_PLUGINS = "plugins"; 
	public static final String MODEL_API_BODY = "contents";
	
	private static final String REQUEST_HEADER_REFERER = "referer";
	
	private static final String REDIRECT_TO_HOME = "redirect:/";
	
	public static final String API_RESPONSE_VIEW = "apiResponse";
	public static final String API_REQUEST_PARAM = "request";
	
	private IUserService userServiceClient;
	public IUserService getUserServiceClient(){ return this.userServiceClient; }
	public PlatformController setUserServiceClient(IUserService value)
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
	public ModelAndView homePage()
	{
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
		
		if(page == null || page.getScripts() == null || page.getScripts().length == 0)
		{
			return new ModelAndView(REDIRECT_TO_HOME);
		}
		
		this.contractService.setExternalServices(page, session);
		
		ModelAndView retval = new ModelAndView("index");
		retval.addObject(MODEL_PLUGINS, page.getScripts());
		
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
	public ModelAndView externalRequestGet(HttpServletRequest request)
	{
		String contents = "";
		
		HttpSession session = request.getSession();
		String serviceCall = request.getParameter(API_REQUEST_PARAM);
		if(this.contractService.isCallAllowed(session, serviceCall))
		{
			contents = this.contractService.callServiceWithGet(serviceCall);
		}
		
		if(contents == null)
		{
			contents = "";
		}
		
		ModelAndView retval = new ModelAndView(API_RESPONSE_VIEW);
		retval.addObject(MODEL_API_BODY, contents);
		return retval;
	}
}