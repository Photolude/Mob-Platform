package com.mob.sdk.ppl.domain;

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
import com.mob.commons.plugins.ppl.ImageType;
import com.mob.commons.plugins.ppl.MainMenuItemType;
import com.mob.commons.plugins.ppl.PageDefinitionType;
import com.mob.commons.plugins.ppl.PluginType;
import com.mob.commons.plugins.ppl.Ppl;
import com.mysql.jdbc.StringUtils;

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
			
			if(plugin.getMainMenu() != null)
			{
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
						logger.error("An error occured while trying to read " + page.getScript().get(i));
						logger.error(e);
						return false;
					} 
				}
				
				for(int i = 0; i < page.getStyle().size(); i++)
				{
					try {
						String contents = Files.toString(new File(page.getStyle().get(i)), Charsets.UTF_8);
						
						page.getStyle().set(i, contents);
					} catch (IOException e) {
						logger.error("An error occured while trying to read " + page.getStyle().get(i));
						logger.error(e);
						return false;
					} 
				}
			}
			
			logger.info("checking art to package");
			if(plugin.getArt() != null)
			{
				logger.info("packaging art");
				String root = plugin.getArt().getImageRoot();
				if(root == null)
				{
					root = ".";
				}
				if(root != null)
				{
					if(!root.endsWith("/") && !root.endsWith("\\"))
					{
						root += "/";
					}
					
					for(ImageType image : plugin.getArt().getImage())
					{
						if(StringUtils.isNullOrEmpty(image.getData()))
						{
							String data = ConvertImageToData(root + image.getPath());
							logger.info("packaging art: " + root + image.getPath() + " : " + ((data != null)? "succeeded" : "failed"));
							if(data == null)
							{
								return false;
							}
							
							image.setData(data);
						}
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
		
		if(extension.equals("jpg"))
		{
			type = "image/jpeg";
		}
		else
		{
			type = "image/" + extension;
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
