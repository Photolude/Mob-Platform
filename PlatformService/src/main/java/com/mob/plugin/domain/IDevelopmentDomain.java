package com.mob.plugin.domain;

import com.mob.commons.plugins.ppl.Ppl;

public interface IDevelopmentDomain {
	boolean DeployPluginForDebugging(String userToken, Ppl pluginRequest);
	boolean publishPlugin(Ppl pluginRequest);
}
