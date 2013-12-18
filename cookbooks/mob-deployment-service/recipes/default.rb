#
# Cookbook Name:: mob-deployment-service
# Recipe:: default
#
# Copyright 2013, Photolude, LLC
#
# The rights for this file fall under the same rights as the git repository containing it
#

service "tomcat7" do
	supports :restart => true, :status => true
	retries 3
	retry_delay 300
end

# Deploy the new war file
cookbook_file "mob-deployment-service.war" do
	path node["tomcat"]["webapp_dir"] + "/mob-deployment-service.war"
	action :create
	notifies :restart, "service[tomcat7]", :immediately
end

# Deploy the new version file
cookbook_file "version.txt" do
	path node["tomcat"]["webapp_dir"] + "/mob-deployment-service/WEB-INF/version.txt"
	action :create
end

# Setup the configuration
template "config.properties" do
	path node["tomcat"]["webapp_dir"] + "/mob-deployment-service/WEB-INF/config.properties"
	source "config.properties"
	action :create
	notifies :restart, "service[tomcat7]", :immediately
end

