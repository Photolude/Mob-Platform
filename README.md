Mob-Platform
============
This is a web platform designed to host layered apps and plugins which provide a multi-layered approach to web development.  In theory this allows multiple developers, communities and even organizations to operate simultaneously and independently in order to deliver value to a single web page or website.

This platform was started to support the ever expanding set of features which www.photolude.com provides, and to allow the development community to also contribute both to the platform as well as create apps and plugins for the photolude site itself.  Its designed to leverage the strength of cloud computing while allowing for a back office facility which can be completely controlled outside of the cloud.

-------------------
Installation Prerequisits:
-------------------
1. Install Tomcat 7 (You should make sure to have a SSL secure endpoint for communication)
2. Install MySql
3. maven installed on your build machine


-------------------
Building the project
-------------------
1. Modify "Commons/pom.xml" to point to your instance of a maven dependency repository (I use nexus on my local machine which is how it's currently configured)
2. Build and deploy "Commons" using maven
3. Build "PlatformWebsite", "PlatformService" and "PPL" in any order
4. Add the "PPL/target" directory to your path for when you are building and deploying plugins


----------------------
To install the Mob-Platform:
----------------------
1. Install the "MobDatabase"
2. Install the "PlatformService" war file
	2-A. Modify the "PlatformService" beans.xml configuration file to point at the installed Mob-Platform database
	2-B. Modify the "PlatformService" beans.xml configuration file to point at your user login service
	2-C. Make sure you're functioning correctly by hitting the plugin health endpoint which should be something like "https://${serverName}:${tomcatPort}/${ServiceName}/Plugins/Status/Health".  If any errors occur check the log file for specifics.
3. Install the "PlatformWebsite"
	3-A. Modify the "PlatformWebsite" mvc-dispatcher-servlet.xml configuration file to point at the user service provided with the "PlatformService".  This should be something like https://${serverName}:${tomcatPort}/${ServiceName}/user
	3-B. Modify the "PlatformWebsite" mvc-dispatcher-servlet.xml configuration file to point at the plugin service provided with the "PlatformService". This should be something like https://${serverName}:${tomcatPort}/${ServiceName}/Plugins
	3-C. Check the platform's health by hitting the health endpoint which should be something like "https://${serverName}:{tomcatPort}/{PlatformWebsiteName}/status/health".  if any errors occur check the log file for specifics

	
--------------------
Integration:
--------------------
1. Have your main website post the fields "email" and "password" to the PlatformWebsite's logon url which should look like https://${serverName}:{tomcatPort}/{PlatformWebsiteName}/logon
2. Create a module which implements ICeemAccessLayer
	2-A. Modify the "PlatformWebsite" mvc-dispatcher-servlet.xml configuration file to point at your new module in order to verify login credentials