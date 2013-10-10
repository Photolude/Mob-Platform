package com.mob.www.platform.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mob.www.platform.model.ExternalPostRequest;
import com.mob.www.platform.services.IServiceContracts;

@Controller
@RequestMapping("/externalrequest/")
public class ExternalRequestController {
	private IServiceContracts contractService;
	public IServiceContracts getContractService(){ return this.contractService; }
	public ExternalRequestController setContractService(IServiceContracts value)
	{
		this.contractService = value;
		return this;
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public void get(HttpServletRequest request, HttpServletResponse response)
	{
		HttpSession session = request.getSession();
		String serviceCall = request.getParameter(PlatformController.API_REQUEST_PARAM);
		
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
	
	@RequestMapping(value = "/post", method = RequestMethod.POST )
	public void post(ExternalPostRequest requestDetails, HttpServletRequest request, HttpServletResponse response)
	{
		Logger logger = Logger.getLogger(this.getClass());
		HttpSession session = request.getSession();
		if(requestDetails != null && requestDetails.getRequest() != null && this.contractService.isCallAllowed(session, requestDetails.getRequest()))
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
	
	@RequestMapping(value = "/put", method = RequestMethod.POST )
	public void put(ExternalPostRequest requestDetails, HttpServletRequest request, HttpServletResponse response)
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
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST )
	public void delete(ExternalPostRequest requestDetails, HttpServletRequest request, HttpServletResponse response)
	{
		Logger logger = Logger.getLogger(this.getClass());
		HttpSession session = request.getSession();
		if(requestDetails != null && this.contractService.isCallAllowed(session, requestDetails.getRequest()))
		{
			HttpResponse serviceResponse = this.contractService.callServiceWithDelete(session, requestDetails.getRequest(), requestDetails.getData(), requestDetails.getRequestDataType());
			
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
