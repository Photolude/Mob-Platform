#
# Cookbook Name:: mob-platform-service
# Recipe:: default
#
# Copyright 2013, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#

cookbook_file "mob-platform-service.war" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-service.war"
	action :create_if_missing
end

template "config.properties" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-service/WEB-INF/config.properties"
	source "config.properties"
	action :create
	notifies :restart, "service[tomcat]"
end

