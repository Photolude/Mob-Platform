package com.mob.sdk.ppl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mob.commons.plugins.ppl.Ppl;
import com.mob.commons.plugins.utils.PplUtils;
import com.mob.sdk.ppl.domain.PhotoludePluginDomain;

public class EntryPoint {
    private static final int ACTION_FAILED = 1;
	
    private static final String FLAG_DEPLOY = "deploy";
    private static final String FLAG_PUBLISH = "publish";
    private static final String FLAG_TARGET = "target";
    private static final String FLAG_TOKEN = "token";
    private static final String FLAG_VALIDATE = "validate";
    
    private static final String TARGET_DEFAULT = "development.photolude.com";
    
	public static void main(String[] args) throws IOException {
		Logger logger = Logger.getLogger(EntryPoint.class);
    	Map<String, String> arguments = new HashMap<String, String>();
    	String lastKey = null;
    	
    	if(args != null)
    	{
	    	//
	    	// Parse arguments
	    	//
	    	for(String arg : args)
	    	{
	    		if(arg.startsWith("/") || arg.startsWith("-"))
	    		{
	    			lastKey = arg.substring(1);
	    			arguments.put(lastKey, "");
	    		}
	    		else if(lastKey != null)
	    		{
	    			arguments.put(lastKey, arg);
	    		}
	    	}
    	}
    	
    	//
    	// Set defaults
    	//
    	if(!arguments.containsKey(FLAG_TARGET))
    	{
    		arguments.put(FLAG_TARGET, TARGET_DEFAULT);
    	}
    	
    	if(arguments.containsKey("help"))
    	{
    		System.out.append("ppl.exe [flags]\n");
    		System.out.append("-validate: validates the ppl.xml file contents.\n");
    		System.out.append("-deploy: deploys photolude plugin for a specific user in order to be able to debug integrations.\n");
    		System.out.append("-target [target]: The url which to deploy the plugin to.  By default this gets set to " + TARGET_DEFAULT + "\n");
    		System.out.append("-token [token]: The token for the user which is to used for debugging\n");
    		System.out.append("-publish: publishes a plugin for general consumption.\n");
    		return;
    	}
    	
    	PhotoludePluginDomain domain = new PhotoludePluginDomain();
    	
    	File pplFile = new File("ppl.xml");
    	
    	if(!pplFile.exists())
    	{
    		logger.error("Could not find the ppl.xml file in the current directory");
    	}
    	
    	Ppl pplContents = PplUtils.unmarshalPplFile(pplFile.toURI().toURL());
    	
    	if(pplContents == null)
    	{
    		logger.error("The ppl.xml file could not be found in the current directory or could not be read correctly");
    		System.exit(ACTION_FAILED);
    		return;
    	}
    	
    	if(arguments.containsKey(FLAG_VALIDATE))
    	{
    		logger.info("Validating ppl file");
    		if(domain.validate(pplContents))
    		{
    			logger.info("Ppl file appears to be valid");
    		}
    		else
    		{
    			logger.error("The ppl.xml file could not be found in the current directory or could not be read correctly");
        		System.exit(ACTION_FAILED);
        		return;
    		}
    	}
    	
    	if(arguments.containsKey(FLAG_DEPLOY))
    	{
    		if(arguments.get(FLAG_TOKEN) != null && arguments.get(FLAG_TOKEN).length() != 0)
    		{
	    		if(domain.deployForDevelopment(pplContents, arguments.get(FLAG_TARGET), arguments.get(FLAG_TOKEN)))
	    		{
	    			logger.info("Deployment succeeded");
	    		}
	    		else
	    		{
	    			logger.error("Deployment failed.");
	        		System.exit(ACTION_FAILED);
	        		return;
	    		}
    		}
    		else
    		{
    			logger.error("You must specify the -token flag in order to deploy");
    			System.exit(ACTION_FAILED);
    		}
    	}
    	
    	if(arguments.containsKey(FLAG_PUBLISH))
    	{
    		domain.publish(pplContents, arguments.get(FLAG_TARGET));
    	}
    }
}
