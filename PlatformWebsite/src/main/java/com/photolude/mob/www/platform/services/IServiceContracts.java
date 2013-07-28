package com.photolude.mob.www.platform.services;

import javax.servlet.http.HttpSession;

import com.photolude.mob.plugins.commons.servicemodel.PluginPage;

public interface IServiceContracts {
	void setExternalServices(PluginPage page, HttpSession session);
	boolean isCallAllowed(HttpSession session, String serviceCall);
	String callServiceWithGet(String parameter);
}
