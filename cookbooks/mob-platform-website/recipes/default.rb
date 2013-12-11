#
# Cookbook Name:: mob-platform-website
# Recipe:: default
#
# Copyright 2013, Photolude, LLC
#
# The rights for this file fall under the same rights as the git repository containing it
#

#include_recipe "tomcat"

newVersionPath = node["temp_dir"] + "/version.txt"
cookbook_file "version.txt" do
	path newVersionPath
	action :create, :immediately
end

newVersion = File.read(newVersionPath)
File.delete(newVersionPath)

currentVersionPath = node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/version.txt"
currentVersion = null
if File.exists(currentVersionPath)
	currentVersion = File.read(node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/version.txt")
end

if newVersion != currentVersion

	# Deploy the new war file
	cookbook_file "mob-platform-website.war" do
		path node["tomcat"]["webapp_dir"] + "/mob-platform-website.war"
		action :create
	end

	# Start tomcat to pick up the new war and extract it in order to make the
	# configuration files available to modify
	service "Tomcat7" do
		retries 4
		retry_delay 30
		action :restart, :immediately
	end
	
	sleep(60)

	# Deploy the new version file
	cookbook_file "version.txt" do
		path node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/version.txt"
		action :create
	end
	
	# Setup the configuration
	template "config.properties" do
		path node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/config.properties"
		source "config.properties"
		action :create
	end
	
	sleep(20)
	# Restart tomcat to pick up the new configurations
	service "Tomcat7" do
		retries 4
		retry_delay 30
		action :restart, :immediately
	end
end