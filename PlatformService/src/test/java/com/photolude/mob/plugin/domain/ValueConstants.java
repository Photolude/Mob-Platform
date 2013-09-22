package com.photolude.mob.plugin.domain;

import com.photolude.mob.commons.plugins.servicemodel.PluginDefinition;

public class ValueConstants {
	public static final int PLUGIN_ID_VALID = 1;
	public static final int PLUGIN_ID_INVALID = -1;
	
	public static final String USERTOKEN_VALID = "ValidToken";
	public static final String USERTOKEN_INVALID = "";
	
	public static final Long STATIC_ID_VALID = 1L;
	public static final Long STATIC_ID_INVALID = null;
	
	public static final PluginDefinition PLUGIN_STORAGE = new PluginDefinition().setRole("Storage");
	public static final PluginDefinition PLUGIN_DEBUG = new PluginDefinition().setRole("Debug");
	
	public static final PluginDefinition[] PLUGINS_NONE = new PluginDefinition[0];
}
