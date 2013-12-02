#
# Cookbook Name:: mob-platform-service
# Recipe:: default
#
# Copyright 2013, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#

cookbook_file "mob-platform-website.war" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-website.war"
	action :create
end

template "config.properties" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/config.properties"
	source "config.properties"
	action :create
end

