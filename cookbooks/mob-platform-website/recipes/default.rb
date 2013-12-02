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

service "tomcat7" do
	case node["platform"]
	when "centos","redhat","fedora","amazon"
	service_name "tomcat#{node["tomcat"]["base_version"]}"
	supports :restart => true, :status => true
	when "debian","ubuntu"
	service_name "tomcat#{node["tomcat"]["base_version"]}"
	supports :restart => true, :reload => false, :status => true
	when "windows"
	service_name "tomcat#{node["tomcat"]["base_version"]}"
	supports :restart => true, :reload => false, :status => true
	when "smartos"
	service_name "tomcat"
	supports :restart => true, :reload => false, :status => true
	else
	service_name "tomcat#{node["tomcat"]["base_version"]}"
	end
	retries 4
	retry_delay 30
	action :restart
end

template "config.properties" do
	path node["tomcat"]["webapp_dir"] + "/mob-platform-website/WEB-INF/config.properties"
	source "config.properties"
	action :create
end

