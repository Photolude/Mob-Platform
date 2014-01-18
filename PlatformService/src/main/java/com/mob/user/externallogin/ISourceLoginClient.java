package com.mob.user.externallogin;

import com.mob.user.domain.TemporaryId;

public interface ISourceLoginClient {
	String getSourceName();
	TemporaryId login(String token);
}
