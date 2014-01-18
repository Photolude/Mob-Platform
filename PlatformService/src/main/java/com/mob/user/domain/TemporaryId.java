package com.mob.user.domain;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class TemporaryId {
	private static final Logger logger = Logger.getLogger(TemporaryId.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private Date expiration;
	public Date getExpiration(){ return this.expiration; }
	public TemporaryId setExpiration(Date value)
	{
		this.expiration = value;
		return this;
	}
	
	private String source;
	public String getSource(){ return this.source; }
	public TemporaryId setSource(String value)
	{
		this.source = value;
		return this;
	}
	
	private String uniqueId;
	public String getUniqueId(){ return this.uniqueId; }
	public TemporaryId setUniqueId(String value)
	{
		this.uniqueId = value;
		return this;
	}
	
	public TemporaryId(String source, Date expiration)
	{
		this.expiration = expiration;
		this.source = source;
		this.uniqueId = UUID.randomUUID().toString();
	}
	
	@Override
	public String toString()
	{
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonGenerationException e) {
			logger.warn(e);
		} catch (JsonMappingException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}
		return null;
	}
	
	public static TemporaryId getTemporaryId(String data)
	{
		try {
			return mapper.readValue(data, TemporaryId.class);
		} catch (JsonParseException e) {
			logger.warn(e);
		} catch (JsonMappingException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}
		
		return null;
	}
}
