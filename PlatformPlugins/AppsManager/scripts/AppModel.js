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
		//
		// Need to get the full element including the outer element tags
		//
		var newParent = $("<div></div>");
		newParent.append(newObject);
		var newObjectText = newParent.html();
		
		//
		// Do find and replace for tag elements
		//
		newObjectText = replaceAll(newObjectText, "${id}", this.plugin.id);
		newObjectText = replaceAll(newObjectText, "${name}", this.plugin.name);
		newObjectText = replaceAll(newObjectText, "${company}", this.plugin.company);
		newObjectText = replaceAll(newObjectText, "${version}", this.plugin.version);
		newObjectText = replaceAll(newObjectText, "${role}", this.plugin.role);
		newObjectText = replaceAll(newObjectText, "${description}", this.plugin.description);
		newObjectText = replaceAll(newObjectText, "${rawExternalResources}", this.plugin.rawExternalResources);
		newObjectText = replaceAll(newObjectText, "${icon}", this.plugin.icon);
		var self = this;

		//
		// Get the jquery element
		//
		this.appElement = $(newObjectText);

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