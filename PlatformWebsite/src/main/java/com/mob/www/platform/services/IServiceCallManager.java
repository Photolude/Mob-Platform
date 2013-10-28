package com.mob.www.platform.services;

import org.apache.http.HttpResponse;

import com.mob.commons.plugins.servicemodel.PluginDataCall;
import com.mob.www.platform.model.DataCallResponse;

public interface IServiceCallManager {
	HttpResponse callServiceWithGet(String endpoint, ServiceCallContext context);
	HttpResponse callServiceWithPost(String endpoint, String data, String contentType, ServiceCallContext context);
	HttpResponse callServiceWithPut(String endpoint, String data, String contentType, ServiceCallContext context);
	HttpResponse callServiceWithDelete(String endpoint, String data, String contentType, ServiceCallContext context);
	
	DataCallResponse[] callDataCalls(PluginDataCall[] dataCallInfo, ServiceCallContext context);
}
