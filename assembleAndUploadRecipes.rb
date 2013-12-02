#
# This script assembles and uploads the chef cookbooks for the build system
#
# Copyright 2013, Photolude, LLC
#
require 'pathname'
require 'FileUtils'

def copyFiles(srcMask, dest)
	FileUtils.mkpath(dest)
	
	Dir.glob(srcMask) do |filePath|
		fileName = File.basename(filePath)
		outdata = File.read(filePath)

		File.open(dest + fileName, 'w') do |out|
			out << outdata
		end
	end
end

copyFiles("PlatformService/target/mob-platform-service-*.war", "cookbooks/mob-platform-service/files/default/")
copyFiles("PlatformWebsite/target/mob-platform-website-*.war", "cookbooks/mob-platform-website/files/default/")

system "knife cookbook upload mob-platform-service"
system "knife cookbook upload mob-platform-website"
