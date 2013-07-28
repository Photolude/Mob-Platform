package com.photolude.mob.www.platform.services;

import com.photolude.mob.plugins.commons.servicemodel.MainMenuItem;
import com.photolude.mob.plugins.commons.servicemodel.PluginPage;

public interface IPluginService {

	MainMenuItem[] getUserMenu(String userToken);

	PluginPage getPagePlugins(String userToken, String page);
}
