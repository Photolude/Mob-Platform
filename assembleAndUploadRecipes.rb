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
		system "echo #{filePath}"
		FileUtils.cp(filePath, dest);
	end
end

def writeVersionFile(destFolder)
	version = File.read("version.txt") 
	version << "."
	version << ENV["BUILD_NUMBER"]
	
	dest = destFolder
	dest << "/version.txt"
	File.open(dest, 'w') do |file| 
		file << version
	end
end

copyFiles("PlatformService/target/mob-platform-service-*.war", "cookbooks/mob-platform-service/files/default/mob-platform-service.war")
writeVersionFile("cookbooks/mob-platform-service/files/default")

copyFiles("PlatformWebsite/target/mob-platform-website-*.war", "cookbooks/mob-platform-website/files/default/mob-platform-website.war")
writeVersionFile("cookbooks/mob-platform-website/files/default")

copyFiles("DeploymentService/target/mob-deployment-service-*.war", "cookbooks/mob-deployment-service/files/default/mob-platform-service.war")
writeVersionFile("cookbooks/mob-deployment-service/files/default")

system "knife cookbook upload mob-platform-service"
system "knife cookbook upload mob-platform-website"
system "knife cookbook upload mob-deployment-service"