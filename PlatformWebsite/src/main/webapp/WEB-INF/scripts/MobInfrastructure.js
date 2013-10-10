function MobInfrastructure()
{
	this.callbackMap = {};
	
	this.registerCallback = function(callbackName, callback)
	{
		if(this.callbackMap[callbackName] == null)
		{
			// Create new callback;
			this.callbackMap[callbackName] = new Array();
		}
		
		this.callbackMap[callbackName][this.callbackMap[callbackName].length] = callback;
	};
	
	this.fireCallback = function(callbackName, object)
	{
		var retval = null;
		if(this.callbackMap[callbackName] != null)
		{
			var callBacks = this.callbackMap[callbackName];
			
			for(var i = 0; i < callBacks.length; i++)
			{
				if(callBacks[i] != null)
				{
					var callbackValue = callBacks[i](object);
					
					if(callbackValue != null)
					{
						retval = callbackValue;
					}
				}
			}
		}
		
		return retval;
	}
	
	this.load = function()
	{
		this.fireCallback("layout-started", null);
		this.fireCallback("layout-complete", null);
		this.fireCallback("load-complete", null);
	}
	
	var self = this;
	$(document).ready(function(){ self.load(); });
}

var mob = new MobInfrastructure();