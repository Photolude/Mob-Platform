#
# Cookbook Name:: mob-platform-service
# Recipe:: default
#
# Copyright 2013, Photolude, LLC
#
# The rights for this file fall under the same rights as the git repository containing it
#

# Stop tomcat as in some cases it can try to pick up the new war file before
# the file has been completely copied
service "Tomcat7" do
	retries 4
	retry_delay 30
	action :stop
end

# Deploy the new war file
cookbook_file "mob-platform-service.war" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-service.war"
	action :create_if_missing
end

# Start tomcat to pick up the new war and extract it in order to make the
# configuration files available to modify
service "Tomcat7" do
	retries 4
	retry_delay 30
	action [:start]
end

# Setup the configuration
template "config.properties" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-service/WEB-INF/config.properties"
	source "config.properties"
	action :create
end

# Restart tomcat to pick up the new configurations
service "Tomcat7" do
	retries 4
	retry_delay 30
	action [:restart]
end