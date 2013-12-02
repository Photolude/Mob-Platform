#
# This script assembles and uploads the chef cookbooks for the build system
#
# Copyright 2013, Photolude, LLC
#
require 'pathname'
require 'FileUtils'

def copyFiles(srcMask, dest)
	dir = File.dirname(dest)

	unless File.directory?(dir)
		FileUtils.mkdir_p(dir)
	end

	Dir.glob(srcMask) do |filePath|
		outdata = File.read(filePath)

		File.open(dest, 'w') do |out|
			out << outdata
		end
	end
end

copyFiles("PlatformService/target/mob-platform-service-*.war", "cookbooks/mob-platform-service/files/default/mob-platform-service.war")
copyFiles("PlatformWebsite/target/mob-platform-website-*.war", "cookbooks/mob-platform-website/files/default/mob-platform-website.war")

system "knife cookbook upload mob-platform-service"
system "knife cookbook upload mob-platform-website"
