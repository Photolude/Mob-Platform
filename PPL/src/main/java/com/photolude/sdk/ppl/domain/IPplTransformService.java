package com.photolude.sdk.ppl.domain;

import com.photolude.mob.commons.plugins.ppl.Ppl;

public interface IPplTransformService {
	boolean packagePpl(Ppl ppl);
	byte[] generateTransportPackage(Ppl ppl);
}
