function getExternalServiceCallPath(serviceCall)
{
	return "/externalrequest/get/" + serviceCall;
}

function callExternalServiceGet(serviceCall, callback)
{
	$.get(getExternalServiceCallPath(serviceCall),
			function(data, status)
			{
				if(callback != null)
				{
					callback(data);
				}
			});
}

function callExternalServicePost(serviceCall, body, dataType, callbackSuccess, callbackError)
{
	$.ajax({
		type: "POST",
		url: "/externalrequest/post",
		data: { data: JSON.stringify(body), request: serviceCall, requestDataType: dataType },
		success: function(data){
			if(callbackSuccess != null)
			{
				if(dataType == "application/json" && data != "")
				{
					data = eval("(" + data + ")");
				}
				callbackSuccess(data);
			}
		},
		fail: callbackError,
		dataType: "application/json"
	});
}

function callExternalServicePut(serviceCall, body, dataType, callbackSuccess, callbackError)
{
	$.ajax({
		type: "POST",
		url: "/externalrequest/put",
		data : { data: JSON.stringify(body), request: serviceCall, requestDataType: dataType },
		dataType: "application/json",
		success: function(data){
			if(callbackSuccess != null)
			{
				if(dataType == "application/json" && data != "")
				{
					data = eval("(" + data + ")");
				}
				callbackSuccess(data);
			}
		},
		error: callbackError
	});
}

function callExternalServiceDelete(serviceCall, body, dataType, callbackSuccess, callbackError)
{
	$.ajax({
		type: "POST",
		url: "/externalrequest/delete",
		data : { data: JSON.stringify(body), request: serviceCall, requestDataType: dataType },
		dataType: "application/json",
		success: function(data){
			if(callbackSuccess != null)
			{
				if(dataType == "application/json" && data != "")
				{
					data = eval("(" + data + ")");
				}
				callbackSuccess(data);
			}
		},
		error: callbackError
	});
}
