
function callExternalServiceGet(serviceCall, callback)
{
	$.get("/externalrequest/get?request=" + serviceCall,
			function(data, status)
			{
				if(callback != null)
				{
					callback(data);
				}
			});
}