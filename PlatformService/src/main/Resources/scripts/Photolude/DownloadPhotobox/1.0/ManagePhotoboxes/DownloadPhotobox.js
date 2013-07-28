
function Photolude_DownloadAllPhotos()
{
	this.currentPhotoList = new Array();
	this.isComplete = false;
	this.currentPage = 0;
	this.preparingDialog = null;
	
	this.AddPhotosToList = function(data)
	{
		if(data != null)
		{
			for(var i = 0; i < data.length; i++)
			{
				this.currentPhotoList.push(data[i].Id);
			}
		}
		else
		{
			this.isComplete = true;
		}
	};
	
	this.StartPhotoboxImageDownload = function(setBoxResult)
	{
		var service = new ImageService();
		
		var mindate = new Date("1/1/1900");
		var maxdate = new Date("1/1/2100");
		
		if(setBoxResult == true && !this.isComplete)
		{
			var self = this;
			service.GetPhotos(mindate, maxdate, "Oldest to Newest", this.currentPage, 50, function(data){self.AddPhotosToList(data);});
			this.currentPage++;
	
			setTimeout(function(){self.StartPhotoboxImageDownload(true);}, 500);
		}
		
		if(this.isComplete)
		{
			this.preparingDialog.hide();
			bulkDownloadDialog.DownloadImages(this.currentPhotoList);
		}
	};
	
	this.DownloadAllPhotoboxPhotos = function(boxId)
	{
		var service = new ImageService();
		this.currentPage = 0;
		this.currentPhotoList = new Array();
		this.isComplete = false;
		
		var self = this;
		
		
		this.preparingDialog.css("left", ($(window).width() - this.preparingDialog.width()) / 2 + "px");
		this.preparingDialog.css("top", ($(window).height() - this.preparingDialog.height()) / 2 + "px");
	    this.preparingDialog.show();
	    
		service.SetImageCollection(boxId, function(result){self.StartPhotoboxImageDownload(result);});
	};
	
	this.load = function()
	{
		var boxItems = GetPhotoboxes();
		
		for(var i = 0; i < boxItems.length; i++)
		{
			var item = boxItems[i];
			
			var model = $("#DownloadAllModel").clone();
			model.attr("id", "");
			model.attr("boxid", item.id);
			model.css("display", "");
			
			model.click(function(){photoludeDownloadAllPhotos.DownloadAllPhotoboxPhotos($(this).attr("boxid"));});
			
			item.tools.children(":first").after(model);
		}
		
		this.preparingDialog = $("#Photolude_Dialog_PreparingDownload");
	};
	
	var self = this;
	$(document).ready(function(){
		self.load();
	});
}

var photoludeDownloadAllPhotos = new Photolude_DownloadAllPhotos();