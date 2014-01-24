package com.mob.www.platform.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.mob.www.platform.model.ExternalPostRequest;
import com.mob.www.platform.services.IServiceCallManager;
import com.mob.www.platform.services.ServiceCallContext;

@Controller
@RequestMapping("/externalrequest/")
public class ExternalRequestController {
	private static final Logger logger = Logger.getLogger(ExternalRequestController.class);
	
	private IServiceCallManager serviceCallManager;
	public IServiceCallManager getServiceCallManager(){ return this.serviceCallManager; }
	public ExternalRequestController setServiceCallManager(IServiceCallManager value)
	{
		this.serviceCallManager = value;
		return this;
	}
	
	@RequestMapping(value = "/get/**", method = RequestMethod.GET)
	public void get(HttpServletRequest request, HttpServletResponse response)
	{
		String serviceCall = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		ServiceCallContext context = ServiceCallContext.getContext(request);
		
		HttpResponse serviceResponse = this.serviceCallManager.callServiceWithGet(serviceCall, context);
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
	
	@RequestMapping(value = "/post", method = RequestMethod.POST )
	public void post(ExternalPostRequest requestDetails, HttpServletRequest request, HttpServletResponse response)
	{
		ServiceCallContext context = ServiceCallContext.getContext(request);
		HttpResponse serviceResponse = this.serviceCallManager.callServiceWithPost(requestDetails.getRequest(), requestDetails.getData(), requestDetails.getRequestDataType(), context);

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
		else
		{
			try {
				response.sendError(500);
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}
	
	@RequestMapping(value = "/put", method = RequestMethod.POST )
	public void put(ExternalPostRequest requestDetails, HttpServletRequest request, HttpServletResponse response)
	{
		ServiceCallContext context = ServiceCallContext.getContext(request);
		HttpResponse serviceResponse = this.serviceCallManager.callServiceWithPut(requestDetails.getRequest(), requestDetails.getData(), requestDetails.getRequestDataType(), context);
		
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
		else
		{
			try {
				response.sendError(500);
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST )
	public void delete(ExternalPostRequest requestDetails, HttpServletRequest request, HttpServletResponse response)
	{
		ServiceCallContext context = ServiceCallContext.getContext(request);
		HttpResponse serviceResponse = this.serviceCallManager.callServiceWithDelete(requestDetails.getRequest(), requestDetails.getData(), requestDetails.getRequestDataType(), context);

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
