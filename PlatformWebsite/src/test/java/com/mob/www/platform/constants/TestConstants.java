package com.mob.www.platform.constants;

import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDefinition;

public class TestConstants {
	public static final String SERVICE_CALL_VALID = "role/call";
	public static final String ENDPOINT_VALID = "http://serviceCall/";
	public static final String RESPONSE_BODY_VALID = "This is the response";
	
	public static final String ROLE_VALID = "role";
	public static final String ART_PATH_VALID = "artPath";
	public static final String BODY_VALID = "valid body";
	public static final String CONTENT_TYPE_VALID = "image/png";
	public static final int STATUS_CODE_OK = 200;
	public static final int STATUS_CODE_FAIL = 500;
	
	public static final PluginArt ART_VALID = new PluginArt().setId(1).setData(BODY_VALID).setContentType(CONTENT_TYPE_VALID);
	public static final PluginDefinition PLUGIN_VALID = new PluginDefinition().setId(1);
}
