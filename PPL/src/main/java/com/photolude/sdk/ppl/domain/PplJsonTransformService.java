package com.photolude.sdk.ppl.domain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.photolude.plugins.commons.ppl.MainMenuItemType;
import com.photolude.plugins.commons.ppl.PageDefinitionType;
import com.photolude.plugins.commons.ppl.PluginType;
import com.photolude.plugins.commons.ppl.Ppl;

public class PplJsonTransformService implements IPplTransformService {

	public boolean packagePpl(Ppl ppl) {
		Logger logger = Logger.getLogger(this.getClass());
		
		for(PluginType plugin : ppl.getPlugin())
		{
			//
			// Convert images to data
			//
			if(!plugin.getIcon().startsWith("data:"))
			{
				plugin.setIcon(ConvertImageToData(plugin.getIcon()));
			}
			
			for(MainMenuItemType item : plugin.getMainMenu().getItem())
			{
				if(!item.getImage().startsWith("data:"))
				{
					item.setImage(ConvertImageToData(item.getImage()));
					
					if(item.getImage() == null)
					{
						return false;
					}
				}
			}
			
			for(PageDefinitionType page : plugin.getPageDefinition())
			{
				//
				// Convert all the files and pull their information in
				//
				for(int i = 0; i < page.getHtml().size(); i++)
				{
					try {
						String contents = Files.toString(new File(page.getHtml().get(i)), Charsets.UTF_8);
						
						page.getHtml().set(i, contents);
					} catch (IOException e) {
						logger.error("An error occured while trying to read " + page.getHtml().get(i));
						logger.error(e);
						return false;
					} 
				}
				
				for(int i = 0; i < page.getScript().size(); i++)
				{
					try {
						String contents = Files.toString(new File(page.getScript().get(i)), Charsets.UTF_8);
						
						page.getScript().set(i, contents);
					} catch (IOException e) {
						logger.error("An error occured while trying to read " + page.getHtml().get(i));
						logger.error(e);
						return false;
					} 
				}
			}
		}
		
		return true; 
	}

	public byte[] generateTransportPackage(Ppl ppl) 
	{
		Logger logger = Logger.getLogger(this.getClass());
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(stream, ppl);

			return stream.toByteArray();
		} catch (JsonGenerationException e) {
			logger.error(e);
		} catch (JsonMappingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return null;
	}
	

	private String ConvertImageToData(String imagePath)
	{
		Logger logger = Logger.getLogger(this.getClass());
		
		File file = new File(imagePath);
		String type = "image/jpeg";
		String contents = null;
		
		if(file.exists())
		{
			try {
				byte[] fileData = Files.toByteArray(file);
				 contents = Base64.encodeBase64String(fileData);
			} catch (IOException e) {
				logger.error("An error occured while generating the image data");
				logger.error(e);
				return null;
			}
		}
		
		int lastIndex = imagePath.lastIndexOf('.');
		String extension = imagePath.substring(lastIndex);
		
		if(extension.equals("png"))
		{
			type = "image/png";
		}
		else if(extension.equals("gif"))
		{
			type = "image/gif";
		}
		
		if(contents != null)
		{
			return "data:" + type + ";base64," + contents;
		}
		else
		{
			return null;
		}
	}
}
