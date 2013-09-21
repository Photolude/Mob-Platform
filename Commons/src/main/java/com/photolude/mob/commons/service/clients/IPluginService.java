package com.photolude.mob.commons.service.clients;

import com.photolude.mob.commons.plugins.servicemodel.MainMenuItem;
import com.photolude.mob.commons.plugins.servicemodel.PluginPage;

public interface IPluginService {

	MainMenuItem[] getUserMenu(String userToken);

	PluginPage getPagePlugins(String userToken, String page);
}
