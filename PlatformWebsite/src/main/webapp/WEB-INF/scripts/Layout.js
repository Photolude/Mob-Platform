function LayoutManager()
{
	$("[camp]").each(function(index, item){
		var element = $(item);
		var roleName = element.attr("camp");
		var itemsToAdd = $("[supporter=" + roleName + "]");
		element.append(itemsToAdd);
	});
}

mob.registerCallback("layout-started", function(){ LayoutManager(); });