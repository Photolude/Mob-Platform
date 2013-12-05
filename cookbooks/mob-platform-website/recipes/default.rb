#
# Cookbook Name:: mob-platform-website
# Recipe:: default
#
# Copyright 2013, Photolude, LLC
#
# The rights for this file fall under the same rights as the git repository containing it
#

include_recipe "tomcat"

if compare_to("version.txt").compare_to(path node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/") do
	previousVersion = File.read(path node["tomcat"]["webapp_dir"] + "/version.txt")
	cookbook_file "version.txt" do 


	# Stop tomcat as in some cases it can try to pick up the new war file before
	# the file has been completely copied
	service "Tomcat7" do
		retries 4
		retry_delay 30
		action :stop
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
		action [:start]
	end

	# Deploy the new war file
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
		action [:restart]
	end
end