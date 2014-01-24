package com.mob.www.platform.services;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mysql.jdbc.StringUtils;

public class EhArtCache implements IArtCache {
	private Cache cache;
	
	private EhArtCache()
	{
		// Construct cache
		//CacheManager cacheManager = CacheManager.getInstance();
		//cacheManager.addCache("ArtCache");
		//this.cache = cacheManager.getCache("ArtCache");
	}
	
	@Override
	public PluginArt getArt(int pluginId, String path)
	{
		String key = generateKey(pluginId, path);
		if(key == null || this.cache == null || !this.cache.isKeyInCache(key))
		{
			return null;
		}
		
		Element element = this.cache.get(key);
		
		if(element == null)
		{
			return null;
		}
		
		return (PluginArt)element.getObjectValue();
	}

	@Override
	public boolean cacheArt(int pluginId, String path, PluginArt art)
	{
		String key = generateKey(pluginId, path);
		if(key == null || this.cache == null)
		{
			return false;
		}
		
		if(this.cache.isKeyInCache(key))
		{
			return false;
		}
		
		this.cache.put(new Element(key, art));
		
		return true;
	}
	
	public void clearCache()
	{
		this.cache.removeAll();
	}
	
	private String generateKey(int pluginId, String path)
	{
		if(StringUtils.isNullOrEmpty(path))
		{
			return null;
		}
		
		StringBuilder retval = new StringBuilder();
		retval.append(pluginId);
		retval.append("_");
		retval.append(path);
		
		return retval.toString();
	}
}
