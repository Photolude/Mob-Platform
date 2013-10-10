package com.mob.www.platform.services;

import com.mob.commons.plugins.servicemodel.PluginArt;

public interface IArtCache {
	PluginArt getArt(int pluginId, String path);
	boolean cacheArt(int pluginId, String path, PluginArt art);
}
