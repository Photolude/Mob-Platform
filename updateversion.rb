#
# This script assembles and uploads the chef cookbooks for the build system
#
# Copyright 2013, Photolude, LLC
#
require 'pathname'
require 'FileUtils'

version = File.read("version.txt") + "." + ENV["BUILD_NUMBER"]
Dir.glob("cookbooks/*/metadata.rb") do |filename|
	outdata = File.read(filename).gsub(/version.*'.*'/, "version \'#{version}\'")

	File.open(filename, 'w') do |out|
		out << outdata
	end
end

Dir.glob("**/pom.xml") do |filename|
	outdata = File.read(filename).gsub(/<mob.version>.*<\/mob\.version>/, "<mob.version>#{version}</mob.version>");
	
	File.open(filename, 'w') do |out|
		out << outdata
	end
end