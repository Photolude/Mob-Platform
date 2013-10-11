The Mob Platform
============
This is a web platform designed to host web pages and applications created through decentralized web development.  By decentralizing web development more elaborate and valuable websites can be created more quickly with high quality.  Larger amounts of your workforce can partisipate per webpage without the need for painful manual integration efforts.  Html & java script is integrated automatically on the page. Another value of a decentralized web development system is that a feature or portion of a webpage can be updated independently of the rest of the site.  This reduces the amount of testing and number of points of failure which can occur through typical website deployment mechanisms.

Along with the ability to provide decentralization of development, the mob platform allows multiple versions of a component to be running at the same time for different users.  An example of this is a website called www.Photolude.com, which has its version 2 software technology built on The Mob Platform.  On Photolude users can choose what and when they want to upgrade, along with the features they want to have on the website.  This ability gives more value to the users to pick and choose the experience they want to have without Photolude having to maintain overly complex webpages.

-------------------
Installation Prerequisits:
-------------------
1. Install Tomcat 7 (You should make sure to have a SSL secure endpoint for communication if you are going to have people log on)
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
	1-A. Add a company to the Company table with a key so you can deploy ppl files to it
2. Install the "PlatformService" war file
	2-A. Modify the "PlatformService" beans.xml configuration file to point at the installed Mob-Platform database
	2-B. Modify the "PlatformService" beans.xml configuration file to point at your user login service
	2-C. Make sure you're functioning correctly by hitting the plugin health endpoint which should be something like "https://${serverName}:${tomcatPort}/${ServiceName}/Plugins/Status/Health".  If any errors occur check the log file for specifics.
3. Install the "PlatformWebsite"
	3-A. Modify the "PlatformWebsite" mvc-dispatcher-servlet.xml configuration file to point at the user service provided with the "PlatformService".  This should be something like https://${serverName}:${tomcatPort}/${ServiceName}/user
	3-B. Modify the "PlatformWebsite" mvc-dispatcher-servlet.xml configuration file to point at the plugin service provided with the "PlatformService". This should be something like https://${serverName}:${tomcatPort}/${ServiceName}/Plugins
	3-C. Check the platform's health by hitting the health endpoint which should be something like "https://${serverName}:{tomcatPort}/{PlatformWebsiteName}/status/health".  if any errors occur check the log file for specifics
	3-D. Modify the anonymous configurations as part of the mvc-dispatcher-servlet.xml.  Make sure to specify the defaultIdentity, the availableIdentities and the userNames properties.
	
--------------------
Integration:
--------------------
1. Have your main website post the fields "email" and "password" to the PlatformWebsite's logon url which should look like https://${serverName}:{tomcatPort}/{PlatformWebsiteName}/logon
2. Create a module which implements ICeemAccessLayer
	2-A. Modify the "PlatformWebsite" mvc-dispatcher-servlet.xml configuration file to point at your new module in order to verify login credentials
	
--------------------
Creating a component
--------------------
Web components are called ppl, which combine to make up the Mob ;-).  PPL are comprised of a definition as to what the specific ppl definition is, defines what navigation is publicly discoverable, what pages it contributes to and what images are to be made available.  A single ppl file can contain many plugins which in turn contain the plugin definition, but does not need to contain any of the rest of the aspects which are optional.

Defining a a plugin:
The following can be found in this repository in the subpath: PlatformPlugins/AppsManager/ppl.xml
<ppl>
	<companykey>856a50bf-4735-4e1c-9e77-d05fa983c5b5</companykey>
	<plugin>
		<pluginName>Apps Manager</pluginName>
		<version>1.0.0</version>
		<role>AppManager</role>
		<description>This app allows you to manage your apps &amp; plugins</description>
		<icon>images/windows_128.png</icon>
		<tags>#appmanagement</tags>
	<plugin>
</ppl>

The company key is leveraged by the ppl.exe in its packaging to communicate to the server who is actually doing the deployment.  This value is cross checked with company keys which the mob server knows about, and only those with valid company keys can publish to the mob system.  This feature also allows idenfitication of the creator when looking at the catalog of available plugins in the mob system.

Defining a plugin is fairly simple.  You must define a plugin name, a version, a role, a description, an icon which is to be used when browsing plugins (this is required, but only displayed if the user has access to an app which displays the mob catalog).  Tags can be supplied for the plugin, which allows any app manager system to be able to filter on specific categories.  An example of this can be found in the App Manager itself.

Defining service calls:
The following can be found in this repository in the subpath: PlatformPlugins/AppsManager/ppl.xml

<external>
	<service>
		<root>http://localhost:8080/mob-platform-service/</root>
		<alias>
			<name>pluginAction</name>
			<uri>Plugins/user/${usertoken}/plugin</uri>
		</alias>
		<alias>
			<name>catalog</name>
			<uri>Plugins/user/${usertoken}/catalog</uri>
		</alias>
		<alias>
			<name>requiredRoles</name>
			<uri>Plugins/roles/required</uri>
		</alias>
	</service>
</external>

The example above supplies access to specific url endpoints for service calls to be made either as part of the page construction or as part of ajax queries being made to the server.  The "service" element defines a scope of aliasing where a root url is provided then sub urls can be aliased.  Aliased calls can be referenced leveraging the role name and the alias as seen below in the "Defining a page" section.  All other ajax calls will be rejected by the server.  One big reason for this capability is to provide ajax access to services when the site is operating in a secure https environment, where ajax calls may not be possible to external services.

Defining a page:
The following can be found in this repository in the subpath: PlatformPlugins/AppsManager/ppl.xml

<pageDefinition>
	<pageDefinition>
		<id>AppsManager</id>
		
		<datacall>
			<method>GET</method>
			<uri>AppManager/requiredRoles</uri>
			<pageVariable>requiredRoles</pageVariable>
		</datacall>
		
		<script>scripts/AppModel.js</script>
		<script>scripts/pageLogic.js</script>
		<script>scripts/detailsDialogModel.js</script>
		
		<html>html/body.html</html>
		<html>html/detailsDialog.html</html>
		
	</pageDefinition>
</pageDefinition>

Here you can see that a ppl plugin can define a page.  A page is a simple id, which shouldn't be unique, as it defines what the web content contributes to.  If multiple ppl plugins all define a page with the same name it means that their web content will all be placed on the page when it renders.  This allows multiple ppl plugins to contribute content and functionality in a non-centralized way to the same page.  If the id for the page is "*", then the plugin will contribute the contents specified to all pages.  This can be seen in the development tools at "PlatformPlugins/DevelopmentTools", where a development dialog is specified to show on all pages if installed.

Data calls can be use to stage information from a service.  By leveraging existing aliases (see above in the "Defining service calls" section) you can stage information in a javascript variable on the page.  This will allow javascript to access that information in order to display the specified data in a meaningful way.

The script tag point to javascript files, while the html tag points to html files.  A peice which isn't here is the style tage which is used to point at css files.

Defining public navigation:

<mainMenu>
	<item>
		<displayName>Apps</displayName>
		<image>images/windows_32.png</image>
		<target>AppsManager</target>
	</item>
</mainMenu>

As part of a plugin you can define public navigation items which point to a specific page definition.  In order to define a menu item you must have a page definition with the same name.  This information will be made available to the page at download time, and plugins like "PlatformPlugins/Menu" will turn this information into a menu system.  App priority is based off of assending order.  If a priority tag is not specified the default priority for a menu item is 50. A caution if you don't specify an order is that you cannot guarentee an order if multiple menu items are present.

Writing Mob Html:
Unlike a typical webpage, the mob approach to generating a webpage is unstructured during development time, and even during download time.  Only once the data is brought down to the client
