package com.photolude.mob.plugin.domain;

import com.photolude.mob.commons.plugins.ppl.Ppl;

public interface IDevelopmentDomain {
	boolean DeployPluginForDebugging(String userToken, Ppl pluginRequest);
	boolean publishPlugin(Ppl pluginRequest);
}
