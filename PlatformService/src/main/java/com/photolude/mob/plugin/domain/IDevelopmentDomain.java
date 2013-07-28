package com.photolude.mob.plugin.domain;

import com.photolude.mob.plugins.commons.ppl.Ppl;

public interface IDevelopmentDomain {
	boolean DeployPluginForDebugging(String userToken, Ppl pluginRequest);
}
