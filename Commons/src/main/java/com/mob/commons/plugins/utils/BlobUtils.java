package com.mob.commons.plugins.utils;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mysql.jdbc.StringUtils;

public class BlobUtils {
	private static Logger logger = Logger.getLogger(BlobUtils.class);
	
	public static <T> T readBlob(String serializedObject, Class<T> typeClass)
	{
		T retval = null;
		if(!StringUtils.isNullOrEmpty(serializedObject) && typeClass != null)
		{
			final Charset charset = Charset.forName("UTF-8");
			ObjectMapper mapper = new ObjectMapper();
			try {
				retval = mapper.readValue(serializedObject.getBytes(charset), typeClass);
			} catch (JsonParseException e) {
				logger.error("There was an error parsing the service aliases, this tends to indicate there was a problem with deployment");
				logger.debug(e);
			} catch (JsonMappingException e) {
				logger.error("There was an error parsing the service aliases, this tends to indicate there was a problem with deployment");
				logger.debug(e);
			} catch (IOException e) {
				logger.error("There was an error parsing the service aliases, this tends to indicate there was a problem with deployment");
				logger.debug(e);
			}
		}
		
		return retval;
	}
}
