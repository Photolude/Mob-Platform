package com.photolude.mob.www.platform.services;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;

import com.photolude.mob.commons.plugins.servicemodel.PluginDataCall;
import com.photolude.mob.commons.plugins.servicemodel.PluginPage;
import com.photolude.mob.www.platform.model.DataCallResponse;

public interface IServiceContracts {
	void setExternalServices(PluginPage page, HttpSession session);
	boolean isCallAllowed(HttpSession session, String serviceCall);
	HttpResponse callServiceWithGet(HttpSession session, String endpoint);
	HttpResponse callServiceWithPost(HttpSession session, String endpoint, String data, String contentType);
	HttpResponse callServiceWithPut(HttpSession session, String endpoint, String data, String contentType);
	DataCallResponse[] callDataCalls(PluginDataCall[] dataCallInfo, HttpSession session, String userToken);
}
