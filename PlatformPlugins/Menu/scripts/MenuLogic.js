// @page=MenuLogic.js

function MenuLogic()
{
	var menuItems = navigationData;
	for(var i = 0; i < menuItems.length; i++)
	{
		var item = $("#menuItemTemplate").clone();
		
		item.css("display", "");
		item.find("a").attr("href", "/apps/" + menuItems[i].target);
		item.find("#menuItemName").text(menuItems[i].displayName);
		
		var imageElement = item.find("#menuItemIcon");
		imageElement.attr("src", menuItems[i].iconData);
		
		$("#menu").append(item);
	}
}

var menuLogic = null;
mob.registerCallback("layout-complete",
		function(){
	menuLogic = new MenuLogic();
})