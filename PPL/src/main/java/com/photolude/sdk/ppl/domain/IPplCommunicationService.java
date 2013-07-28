package com.photolude.sdk.ppl.domain;

public interface IPplCommunicationService {
	boolean deployPlugin(String target, String token, byte[] content);
}
