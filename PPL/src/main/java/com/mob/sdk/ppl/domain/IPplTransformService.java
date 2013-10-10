package com.mob.sdk.ppl.domain;

import com.mob.commons.plugins.ppl.Ppl;

public interface IPplTransformService {
	boolean packagePpl(Ppl ppl);
	byte[] generateTransportPackage(Ppl ppl);
}
