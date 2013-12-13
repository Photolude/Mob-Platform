#
# Cookbook Name:: mob-platform-website
# Recipe:: default
#
# Copyright 2013, Photolude, LLC
#
# The rights for this file fall under the same rights as the git repository containing it
#

#include_recipe "tomcat"

FileUtils.mkdir_p(node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF")

if(File::exists?(node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/version.txt")) do
	# Deploy the new version file
	cookbook_file "version.txt" do
		path node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/version_new.txt"
		action :create
	end

	newVersion = File.read(node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/version_new.txt")
	oldVersion = File.read(node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/version.txt")

	if( newVersion == oldVersion) return;
end

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
	action :restart
end

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

# Restart tomcat to pick up the new configurations
service "Tomcat7" do
	retries 4
	retry_delay 30
	action :restart
end
