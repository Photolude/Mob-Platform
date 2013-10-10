// @page=AppModel.js

function replaceAll(original, find, replace)
{
	return original.split(find).join(replace);
}

function AppModel(plugin)
{
	this.appElement = null;
	this.appDetailsButton = null;
	this.details = null;
	this.installed = null;
	this.plugin = plugin;
	
	this.detailsClicked = function()
	{
		appDetailsDialog.show(this);
	};
	
	this.load = function()
	{
		//
		// Generate a copy of the template
		//
		var newObject = $("#appTemplate").clone();
		newObject.css("display", "");
		newObject.attr("id", "");
		
		newObject.find("#appName").text(this.plugin.name);
		newObject.find("#appVersion").text(this.plugin.version);
		
		var self = this;

		//
		// Get the jquery element
		//
		this.appElement = newObject;

		this.details = $(this.appElement.find("#appDetails_" + this.plugin.id));
		this.installed = $(this.appElement.find("#installedBanner"));
		$(this.appElement.find("#appImage")).attr("src", this.plugin.icon);
		this.appElement.click(function(){self.detailsClicked();});
		
		this.updateStatus();
	};
	
	this.updateStatus = function()
	{
		this.installed.css("display", (this.plugin.installed)? "inherit" : "none");
	}
	
	this.getElement = function()
	{
		return this.appElement;
	};
	
	this.load();
}