// @page=detailsDialogModel.js
function detailsDialogModel()
{
	this.plugin = null;
	this.dialog = null;
	this.appTitle = null;
	this.appImage = null;
	this.appTitleDiv = null;
	this.appVersion = null;
	this.role = null;
	this.accessTo = null;
	this.description = null;
	this.requiredRoles = new Array();
	
	if(requiredRoles != null && requiredRoles != "")
	{
		this.requiredRoles = eval("(" + requiredRoles + ")");
	}
	
	this.installCallback;
	this.uninstallCallback;
	
	this.installClicked = function()
	{
		var self = this;
		if(!this.plugin.installed)
		{
			callExternalServiceGet("AppManager/pluginAction/" + this.plugin.id + "/install", function(contents){
				self.installButton.val("uninstall");
				self.plugin.installed = true;
				self.appCard.updateStatus();
			});
		}
		else
		{
			var roleFound = false;
			for(var i = 0; i < this.requiredRoles.length; i++)
			{
				if(this.requiredRoles[i] == this.plugin.role)
				{
					roleFound = true;
					break;
				}
			}
			
			if(!roleFound)
			{
				callExternalServiceGet("AppManager/pluginAction/" + this.plugin.id + "/uninstall" , function(contents){
					self.installButton.val("install");
					self.plugin.installed = false;
					self.appCard.updateStatus();
				});
			}
			else
			{
				alert("You must have at least one " + this.plugin.role + ".\nTo uninstall this plugin please install another " + this.plugin.role + ", at which point this one will be uninstalled.");
			}
	
		}
	};
	
	this.show = function(appCard)
	{
		this.appCard = appCard;
		this.plugin = appCard.plugin;
		
		this.appTitle.text(this.plugin.name);
		this.appTitleDiv.text(this.plugin.name);
		this.appImage.attr("src", this.plugin.icon);
		this.appVersion.text(this.plugin.version);
		this.role.text(this.plugin.role);
		this.accessTo.text(this.plugin.externalServices);
		this.description.text(this.plugin.description);

		if(this.plugin.externalServices == "" || this.plugin.externalServices == null)
		{
			this.accessToRow.hide();
		}
		else
		{
			this.accessToRow.show();
		}
		
		this.installButton.val((this.plugin.installed)? "uninstall" : "install");
		this.dialog.css("padding-top", "50px");
		
		this.dialog.fadeIn(500);
	};
	
	this.load = function()
	{
		this.dialog = $("#appDetailsDialog");
		this.detailsDialog = $(this.dialog.find("#detailsDialog"));
		this.appTitle = $(this.dialog.find("#appTitle"));
		this.appImage = $(this.dialog.find("#appImage"));
		this.appTitleDiv = $(this.dialog.find("#appTitleDiv"));
		this.appVersion = $(this.dialog.find("#appVersion"));
		this.role = $(this.dialog.find("#role"));
		this.accessTo = $(this.dialog.find("#accessTo"));
		this.accessToRow = $(this.dialog.find("#accessToRow"));
		this.description = $(this.dialog.find("#description"));
		this.installButton = $(this.dialog.find("#installButton"));
		
		var self = this;
		this.installButton.click(function(){self.installClicked();});
		$(this.dialog.find("#closeButton")).click(function(){self.dialog.fadeOut(500);})
	};
	
	var self = this;
	$(document).ready(function(){self.load();});
}

var appDetailsDialog = new detailsDialogModel();
