package com.mob.sdk.ppl.domain;

public interface IPplCommunicationService {
	boolean deployPlugin(String target, String token, byte[] content);
	boolean publishPlugin(String target, byte[] content);
}
