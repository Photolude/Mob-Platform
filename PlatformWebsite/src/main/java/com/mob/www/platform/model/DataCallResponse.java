package com.mob.www.platform.model;

import java.util.concurrent.Future;

import org.apache.http.HttpResponse;

import com.mob.commons.plugins.servicemodel.PluginDataCall;

public class DataCallResponse {
	private PluginDataCall dataCallInfo;
	public PluginDataCall getDataCallInfo(){ return this.dataCallInfo; }
	public DataCallResponse setDataCallInfo(PluginDataCall value)
	{
		this.dataCallInfo = value;
		return this;
	}
	
	private String data;
	public String getData()
	{
		if(this.data != null)
		{
			return this.data.replace("\"", "\\\"");
		}
		return this.data; 
	}
	public DataCallResponse setData(String value)
	{
		this.data = value;
		return this;
	}
	
	private Future<HttpResponse> response;
	public Future<HttpResponse> getResponse(){ return this.response; }
	public DataCallResponse setResponse(Future<HttpResponse> value)
	{
		this.response = value;
		return this;
	}
	
	private String script;
	public String getScript()
	{
		StringBuilder retval = new StringBuilder();
		retval.append("<script type=\"text/javascript\">");
		retval.append("var ");
		retval.append(this.dataCallInfo.getPageVariable());
		retval.append(" = \"");
		retval.append(this.getData());
		retval.append("\";</script>");
		
		this.script = retval.toString();
		return this.script;
	}
	public DataCallResponse setScript(String value){this.script = value; return this;}
}
