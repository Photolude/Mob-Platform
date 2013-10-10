// @page=pageLogic.js

function ManagePluginDomain()
{
	this.catalog = null;
	this.filteredCatalog = null;
	this.appRows = new Array();
	this.tabArea = new Array();
	this.onPage = 1;
	
	this.serviceCallback = function(contents)
	{
		if(contents != null && contents != "")
		{
			this.catalog = contents;
			
			this.tabClicked($(this.tabArea.find(".tabBarHighlighted").children()));
		}
	}
	
	this.tabClicked = function(link)
	{
		this.tabArea.children().removeClass("tabBarHighlighted");
		link.parent().addClass("tabBarHighlighted");
		var tag = link.attr("filter");
		if(tag == "")
		{
			this.filteredCatalog = this.catalog.plugins;
		}
		else if(tag == "#installed")
		{
			this.filteredCatalog = new Array();
			for(var i = 0; i < this.catalog.plugins.length; i++)
			{
				var item = this.catalog.plugins[i];
				if(item.installed)
				{
					this.filteredCatalog[this.filteredCatalog.length] = item;
				}
			}
		}
		else
		{
			
			this.filteredCatalog = new Array();
			for(var i = 0; i < this.catalog.plugins.length; i++)
			{
				var item = this.catalog.plugins[i];
				if(item.tags != null && item.tags.indexOf(tag) >= 0)
				{
					this.filteredCatalog[this.filteredCatalog.length] = item;
				}
			}
		}
		
		this.render(1);
	}
	
	this.render = function(page)
	{
		if(this.filteredCatalog != null)
		{
			var contentObject = $("#pluginCatalog");
			contentObject.empty();
			for(i = 0; i < this.filteredCatalog.length; i++)
			{
				var plugin = this.filteredCatalog[i];
				
				var app = new AppModel(plugin);
				
				contentObject.append(app.getElement());
				this.appRows[i] = app;
			}
		}
		
		this.onPage = page;
	}
	
	this.newAppInstalled = function(plugin)
	{
		for(var i = 0; i < this.catalog.plugins.length; i++)
		{
			var catalogPlugin = this.catalog.plugins[i];
			if(catalogPlugin.installed && plugin.role == catalogPlugin.role)
			{
				catalogPlugin.installed = false;
			}
		}
		
		
		for(var i = 0; i < this.appRows.length; i++)
		{
			this.appRows[i].updateStatus();
		}
	};
	
	this.load = function()
	{
		var self = this;
		callExternalServiceGet("AppManager/catalog", function(contents){self.serviceCallback(contents);});
		
		this.tabArea = $("#appsManagerTabBar");
		this.tabArea.find("a").click(function(){self.tabClicked($(this));});
		
		mob.registerCallback("new-app-installed", function(object){ self.newAppInstalled(object); });
	};
	
	var self = this;
	$(document).ready(function(){self.load();});
};

var managePluginDomain = new ManagePluginDomain();