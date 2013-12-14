#
# Cookbook Name:: mob-platform-website
# Recipe:: default
#
# Copyright 2013, Photolude, LLC
#
# The rights for this file fall under the same rights as the git repository containing it
#

service "tomcat7" do
	supports :restart => true, :status => true
end

# Deploy the new war file
cookbook_file "mob-platform-website.war" do
	path 
	action :create
	notifies :restart, "service[tomcat7]", :immediately
end
sleep(20)

# Deploy the new version file
cookbook_file "version.txt" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/version.txt"
	action :create
	notifies :restart, "service[tomcat7]", :immediately
end

# Setup the configuration
template "config.properties" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/config.properties"
	source "config.properties"
	action :create
	notifies :restart, "service[tomcat7]", :immediately
end
