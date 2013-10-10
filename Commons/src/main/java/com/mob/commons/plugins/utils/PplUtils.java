package com.mob.commons.plugins.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.mob.commons.plugins.ppl.Alias;
import com.mob.commons.plugins.ppl.ArtType;
import com.mob.commons.plugins.ppl.DataCallType;
import com.mob.commons.plugins.ppl.ImageType;
import com.mob.commons.plugins.ppl.MainMenuItemType;
import com.mob.commons.plugins.ppl.MainMenuType;
import com.mob.commons.plugins.ppl.PageDefinitionType;
import com.mob.commons.plugins.ppl.PluginType;
import com.mob.commons.plugins.ppl.Ppl;
import com.mob.commons.plugins.ppl.Service;
import com.mysql.jdbc.StringUtils;


public class PplUtils {
	public static Ppl unmarshalPplFile(URL url)
	{
		Ppl retval = null;
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Ppl.class, PluginType.class, PageDefinitionType.class, MainMenuType.class, MainMenuItemType.class, DataCallType.class);
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
				for(Service service : plugin.getExternal().getService())
				{
					if(service.getRoot().length() < 8 || !service.getRoot().endsWith("/"))
					{
						logger.error("External services must end with a '/', and must include a domain");
						return false;
					}
					
					try
					{
						new URL(service.getRoot());
					}
					catch(MalformedURLException e)
					{
						logger.error("The root provided is malformed (" + service.getRoot() + ")");
						logger.error(e);
						return false;
					}
					
					for(Alias alias : service.getAlias())
					{
						try
						{
							new URL(service.getRoot() + alias.getUri());
						}
						catch(MalformedURLException e)
						{
							logger.error("The alias provided is malformed (root: " + service.getRoot() + " alias: " + alias.getUri());
							logger.error(e);
							return false;
						}
						
						if(alias.getName() == null || alias.getName().length() == 0)
						{
							logger.error("An alias provided was missing a name");
						}
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
					
					for(Object obj : page.getDatacall())
					{
						if(obj != null)
						{
							if(!DataCallType.class.isInstance(obj))
							{
								logger.error("The data call provided was not a recognized type");
								return false;
							}
							
							DataCallType datacall = (DataCallType)obj;
							
							if(StringUtils.isNullOrEmpty(datacall.getMethod()))
							{
								logger.error("Data calls must have a method specified");
								return false;
							}
							if(StringUtils.isNullOrEmpty(datacall.getUri()))
							{
								logger.error("Data calls must have a uri specified");
								return false;
							}
							if(StringUtils.isNullOrEmpty(datacall.getPageVariable()))
							{
								logger.error("Data calls must have a page variable specified");
								return false;
							}
							if(datacall.getMethod().toLowerCase().equals("post"))
							{
								if(StringUtils.isNullOrEmpty(datacall.getContent()))
								{
									logger.error("A post data call must specify contents, which cannot be null or empty");
									return false;
								}
								if(StringUtils.isNullOrEmpty(datacall.getContentType()))
								{
									logger.error("A post data call must specify contentType, which cannot be null or empty");
									return false;
								}
							}
						}
					}
					
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
					
					for(String item : page.getStyle())
					{
						if(!doesContentExist(item, serverCriteria))
						{
							logger.error("Plugin \"" + plugin.getPluginName() + "\", definition \"" + page.getId() + "\", has a file which does not exist \"" + item + "\"");
							return false;
						}
					}
				}
			}
			
			if(plugin.getArt() != null)
			{
				ArtType art = plugin.getArt();
				
				if(art.getImage() != null && art.getImage().size() > 0)
				{
					if(StringUtils.isNullOrEmpty(art.getImageRoot()))
					{
						logger.warn("Plugin \"" + plugin.getPluginName() + "\" has an art section without an image root.  This can cause redundency in paths if the files are located in an image directory.");
					}
					
					String root = (art.getImageRoot() == null)? "" : art.getImageRoot();
					
					if(!root.endsWith("/") && !root.endsWith("\\"))
					{
						root += "/";
					}
					
					for(ImageType image : art.getImage())
					{
						if(image.getPath() == null || StringUtils.isNullOrEmpty(image.getPath()))
						{
							logger.error("Plugin \"" + plugin.getPluginName() + "\" has an image without a path specified");
							return false;
						}
						
						if(!doesContentExist(root + image.getPath(), serverCriteria))
						{
							logger.error("Plugin \"" + plugin.getPluginName() + "\" has an image specified which doesn not exist. " + root + image.getPath());
							return false;
						}
						
						if(serverCriteria && !isImageValid(image.getData(), serverCriteria))
						{
							logger.error("Plugin \"" + plugin.getPluginName() + "\" has image data which is invalid. " + image.getPath());
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
