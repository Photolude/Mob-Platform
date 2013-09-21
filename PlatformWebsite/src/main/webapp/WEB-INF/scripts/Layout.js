function LayoutManager()
{
	this.resize = function(){
		var docHeight = $(document).height();
		var winHeight = $(window).height();
		if(docHeight <= winHeight) {
			this.footerElement.addClass("pageFooterShortPage");
			this.attributions.addClass("pageFooterCreditsShortPage");
		}
		else
		{
			this.footerElement.removeClass("pageFooterShortPage");
			this.attributions.removeClass("pageFooterCreditsShortPage");
		}
		
	};
	
	this.attributionClick = function()
	{
		if(this.attributions.css("display") == "none")
		{
			this.attributions.show(500);
		}
		else
		{
			this.attributions.hide(500);
		}
	}
	
	this.layoutRoles = function(){
		$("[roleplaceholder]").each(function(index, item){
			var element = $(item);
			var roleName = element.attr("roleplaceholder");
			
			element.append($("[role=" + roleName + "]"));
		});
	};
	
	this.loadComplete = function(){
		var self = this;
		
		this.footerElement = $("#pageFooter");
		this.attributions = $("#attributions");
		
		$("#pageFooterAttributionsLink").click(function(){ self.attributionClick(); });
		$(document).bind("DOMSubtreeModified", function(){self.resize(); });
		$(window).resize(function(){ self.resize(); });
		
		this.resize();
	};
	
	var self = this;
	mob.registerCallback("layout-started", function(){ self.layoutRoles(); });
	mob.registerCallback("load-complete", function(){ self.loadComplete(); });
}

var layoutManager = new LayoutManager();