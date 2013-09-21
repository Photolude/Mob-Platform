package com.photolude.mob.www.platform.model;

import com.photolude.mob.commons.plugins.servicemodel.PluginDataCall;

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
