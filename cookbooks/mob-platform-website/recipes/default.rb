#
# Cookbook Name:: mob-platform-website
# Recipe:: default
#
# Copyright 2013, Photolude, LLC
#
# The rights for this file fall under the same rights as the git repository containing it
#

# Deploy the new war file
cookbook_file "mob-platform-website.war" do
	path 
	action :create
	notify :restart, "service[tomcat7]"
end

# Deploy the new version file
cookbook_file "version.txt" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/version.txt"
	action :create
	notify :restart, "service[tomcat7]"
end

# Setup the configuration
template "config.properties" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/config.properties"
	source "config.properties"
	action :create
	notify :restart, "service[tomcat7]"
end
