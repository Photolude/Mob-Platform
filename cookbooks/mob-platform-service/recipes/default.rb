#
# Cookbook Name:: mob-platform-service
# Recipe:: default
#
# Copyright 2013, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#

require 'chef/cookbook/metadata'

metadata_file = ARGV.first || 'metadata.rb'

# read in metadata
metadata = Chef::Cookbook::Metadata.new
metadata.from_file(metadata_file)

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

