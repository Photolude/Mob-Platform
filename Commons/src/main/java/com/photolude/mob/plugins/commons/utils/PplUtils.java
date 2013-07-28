package com.photolude.mob.plugins.commons.utils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.photolude.mob.plugins.commons.ppl.MainMenuItemType;
import com.photolude.mob.plugins.commons.ppl.MainMenuType;
import com.photolude.mob.plugins.commons.ppl.PageDefinitionType;
import com.photolude.mob.plugins.commons.ppl.PluginType;
import com.photolude.mob.plugins.commons.ppl.Ppl;


public class PplUtils {
	public static Ppl unmarshalPplFile(URL url)
	{
		Ppl retval = null;
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Ppl.class, PluginType.class, PageDefinitionType.class, MainMenuType.class, MainMenuItemType.class);
			retval = (Ppl) context.createUnmarshaller().unmarshal(url);
		} catch (JAXBException e) {
			Logger logger = Logger.getLogger(PplUtils.class);
			logger.error("Could not read file \"" + url.getFile() + "\" successfully");
			logger.error(e);
		}
		
		return retval;
	}
	
	public static boolean validate(Ppl ppl, boolean serverCriteria)
	{
		Logger logger = Logger.getLogger(PplUtils.class);
		
		if(ppl == null)
		{
			logger.error("An internal error has occured");
			return false;
		}
		
		if(ppl.getPlugin().size() == 0)
		{
			logger.error("There are no plugins as part of the <ppl> element");
			return false;
		}
		
		for(PluginType plugin : ppl.getPlugin())
		{
			Map<String, Object> pageLookup = new HashMap<String, Object>();
			
			if(plugin.getExternal() != null && plugin.getExternal().getService() != null)
			{
				for(String service : plugin.getExternal().getService())
				{
					if(!service.startsWith("http://") || service.length() < 8 || !service.endsWith("/"))
					{
						logger.error("External services must start with 'http://' and end with a '/', and must include a domain");
						return false;
					}
				}
			}
			
			if(isStringNullOrEmpty(plugin.getPluginName()))
			{
				logger.error("A plugin is missing a name");
				return false;
			}
			if(isStringNullOrEmpty(plugin.getRole()))
			{
				logger.error("Plugin \"" + plugin.getPluginName() + "\" is missing a role");
				return false;
			}
			if(isStringNullOrEmpty(plugin.getVersion()))
			{
				logger.error("Plugin \"" + plugin.getPluginName() + "\" is missing a version");
				return false;
			}
			if(!isImageValid(plugin.getIcon(), serverCriteria))
			{
				logger.error("Plugin \"" + plugin.getPluginName() + "\" is missing an icon");
				return false;
			}
			
			if(plugin.getPageDefinition() != null && plugin.getPageDefinition().size() > 0)
			{
				for(PageDefinitionType page : plugin.getPageDefinition())
				{
					if(isStringNullOrEmpty(page.getId()))
					{
						logger.error("Plugin \"" + plugin.getPluginName() + "\" has a page definition without an id");
						return false;
					}
					
					if(pageLookup.containsKey(page.getId()))
					{
						logger.error("Plugin \"" + plugin.getPluginName() + "\" has a page definition with a duplicate id \"" + page.getId() + "\"");
						return false;
					}
					
					pageLookup.put(page.getId(), new Object());
					
					for(String item : page.getHtml())
					{
						if(!doesContentExist(item, serverCriteria))
						{
							logger.error("Plugin \"" + plugin.getPluginName() + "\", definition \"" + page.getId() + "\", has a file which does not exist \"" + item + "\"");
							return false;
						}
					}
					
					for(String item : page.getScript())
					{
						if(!doesContentExist(item, serverCriteria))
						{
							logger.error("Plugin \"" + plugin.getPluginName() + "\", definition \"" + page.getId() + "\", has a file which does not exist \"" + item + "\"");
							return false;
						}
					}
				}
			}
			
			if(plugin.getMainMenu() != null && plugin.getMainMenu().getItem().size() > 0)
			{
				for(MainMenuItemType item : plugin.getMainMenu().getItem())
				{
					if(isStringNullOrEmpty(item.getDisplayName()))
					{
						logger.error("Plugin \"" + plugin.getPluginName() + "\" has a main menu item without a display name");
						return false;
					}
					if(!isImageValid(item.getImage(), serverCriteria))
					{
						logger.error("Plugin \"" + plugin.getPluginName() + "\", Main Menu Item \"" + item.getDisplayName() + "\" has an invalid file for its image \"" + item.getImage() + "\"");
						return false;
					}
					
					if(!pageLookup.containsKey(item.getTarget()))
					{
						logger.error("Plugin \"" + plugin.getPluginName() + "\", Main Menu Item \"" + item.getDisplayName() + "\" references a target which cannot be found \"" + item.getTarget() + "\"");
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private static boolean isStringNullOrEmpty(String value)
	{
		return value == null || value.length() == 0;
	}
	
	private static boolean doesContentExist(String content, boolean serverCriteria)
	{
		if(serverCriteria)
		{
			return content != null && content.length() > 0;
		}
		
		return content != null && new File(content).exists();
	}
	
	private static boolean isImageValid(String image, boolean serverCriteria)
	{
		if(image == null || image.length() == 0)
		{
			return false;
		}
		if(image.startsWith("data:"))
		{
			return true;
		}
		
		return !serverCriteria && new File(image).exists();
	}
}
