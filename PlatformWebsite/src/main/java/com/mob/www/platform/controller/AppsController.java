package com.mob.www.platform.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.mob.commons.plugins.servicemodel.PluginDataCall;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginPage;
import com.mob.commons.plugins.servicemodel.PluginScript;
import com.mob.commons.service.clients.IPluginService;
import com.mob.www.platform.model.DataCallResponse;
import com.mob.www.platform.services.IServiceCallManager;
import com.mob.www.platform.services.IServiceContracts;
import com.mob.www.platform.services.ServiceCallContext;

@Controller
@RequestMapping("/apps/")
public class AppsController {
	private IPluginService pluginService;
	public IPluginService getPluginService(){ return this.pluginService; }
	public AppsController setPluginService(IPluginService value)
	{
		this.pluginService = value;
		return this;
	}
	
	private IServiceContracts contractService;
	public IServiceContracts getContractService(){ return this.contractService; }
	public AppsController setContractService(IServiceContracts value)
	{
		this.contractService = value;
		return this;
	}
	
	private IServiceCallManager callManager;
	public IServiceCallManager getCallManager(){ return this.callManager; }
	public AppsController setCallManager(IServiceCallManager value)
	{
		this.callManager = value;
		return this;
	}
	
	@RequestMapping(value="/{target}", method = RequestMethod.POST)
	public ModelAndView pluginAppPost(@PathVariable String target, HttpServletRequest request)
	{
		return processPluginRequest(target, request);
	}
	
	@RequestMapping(value = "/{target}", method = RequestMethod.GET)
	public ModelAndView pluginAppGet(@PathVariable String target, HttpServletRequest request)
	{
		return processPluginRequest(target, request);
	}
	
	public ModelAndView processPluginRequest(@PathVariable String target, HttpServletRequest request)
	{
		if(target == null || target.length() == 0)
		{
			return new ModelAndView(PlatformController.REDIRECT_TO_HOME);
		}
		
		ServiceCallContext context = null;
		
		try
		{
			context = new ServiceCallContext(request);
		}
		catch(IllegalArgumentException e)
		{
			return new ModelAndView(PlatformController.REDIRECT_TO_HOME);
		}
		
		PluginPage page = this.pluginService.getPagePlugins(context.getUserToken(), target);

		if(request.getRequestURI() != null && !request.getRequestURI().equals("/") && (page == null || page.getScripts() == null || page.getScripts().length == 0))
		{
			return new ModelAndView(PlatformController.REDIRECT_TO_HOME);
		}
		
		this.contractService.setExternalServices(page, context);

		PluginDataCall[] dataCalls = page.getDataCalls();
		DataCallResponse[] responses = this.callManager.callDataCalls(dataCalls, context);

		context.setActiveScripts(page.getScripts());
		
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

		String attributionString = "[]";
		if(attributionsList.size() > 0)
		{
			ObjectMapper mapper = new ObjectMapper();
			try {
				attributionString = new String(mapper.writeValueAsBytes(attributionsList.toArray(new ExternalAttribution[attributionsList.size()])));
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ModelAndView retval = new ModelAndView("index");
		retval.addObject(PlatformController.MODEL_PLUGINS, page.getScripts());
		retval.addObject(PlatformController.MODEL_DATA_CALLS, responses);
		retval.addObject(PlatformController.MODEL_ATTRIBUTIONS, attributionString.replace("\\", "\\\\").replace("\"", "\\\""));
		return retval;
	}

	@RequestMapping(value = "/script/{scriptName:.+}", method = RequestMethod.GET)
	@ResponseBody
	public String getScript(@PathVariable String scriptName, HttpServletRequest request, HttpServletResponse response)
	{
		ServiceCallContext context = new ServiceCallContext(request);
		
		PluginScript[] scripts = context.getActiveScripts();

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
}
