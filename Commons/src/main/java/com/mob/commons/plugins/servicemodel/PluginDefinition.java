package com.mob.commons.plugins.servicemodel;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.mob.commons.plugins.utils.BlobUtils;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PluginDefinition {
	private int id;
	public int getId(){ return this.id; }
	public PluginDefinition setId(int value)
	{
		this.id = value;
		return this;
	}
	
	private String name;
	public String getName() { return this.name; }
	public PluginDefinition setName(String value) 
	{ 
		this.name = value;
		return this;
	}
	
	private String company;
	public String getCompany() { return this.company; }
	public PluginDefinition setCompany(String value)
	{
		this.company = value;
		return this;
	}
	
	private String description;
	public String getDescription(){ return this.description; }
	public PluginDefinition setDescription(String value)
	{
		this.description = value;
		return this;
	}
	
	private String version;
	public String getVersion() { return this.version; }
	public PluginDefinition setVersion(String value)
	{
		this.version = value;
		return this;
	}
	
	private String role;
	public String getRole(){return this.role; }
	public PluginDefinition setRole(String value)
	{
		this.role = value;
		return this;
	}
	
	private String icon;
	public String getIcon(){ return this.icon; }
	public PluginDefinition setIcon(String value)
	{
		this.icon = value;
		return this;
	}
	
	private ServiceAlias[] serviceAliases;
	public ServiceAlias[] getServiceAliases(){ return this.serviceAliases; }
	public PluginDefinition setServiceAliases(ServiceAlias[] value)
	{
		this.serviceAliases = value;
		return this;
	}
	
	private boolean installed;
	public boolean getInstalled(){ return this.installed; }
	public PluginDefinition setInstalled(boolean value)
	{
		this.installed = value;
		return this;
	}
	
	private String tags;
	public String getTags(){ return this.tags; }
	public PluginDefinition setTags(String value)
	{
		this.tags = value;
		return this;
	}
	
	private int priority;
	public int getPriority(){ return this.priority; }
	public PluginDefinition setPriority(int value)
	{
		this.priority = value;
		return this;
	}
	
	private ExternalAttribution[] attributions;
	public ExternalAttribution[] getAttributions(){ return this.attributions; }
	public PluginDefinition setAttributions(ExternalAttribution[] value)
	{
		this.attributions = value;
		return this;
	}
	
	private String externalServices;
	public String getExternalServices(){ return this.externalServices; }
	public PluginDefinition setExternalServices(String value)
	{
		this.externalServices = value;
		return this;
	}
	
	/**
	 * Reads a blob and puts it into the service aliases
	 * @param blob the blob to be de-serialized
	 */
	public void setServiceCalls(String blob)
	{
		this.setServiceAliases(BlobUtils.readBlob(blob, ServiceAlias[].class));
	}
	
	/**
	 * Reads a blob and puts it into the attributions
	 * @param blob the blob to be de-serialized
	 */
	public void setAttributeBlob(String blob)
	{
		this.setAttributions(BlobUtils.readBlob(blob, ExternalAttribution[].class));
	}
	
	@Override
	public boolean equals(Object otherObject)
	{
		if(otherObject == null || !PluginDefinition.class.isAssignableFrom(otherObject.getClass()))
		{
			return false;
		}
		
		PluginDefinition other = (PluginDefinition)otherObject;
			
		boolean retval = this.getId() == other.getId() &&
				((this.name == null && other.name == null) || (this.name != null && this.getName().equals(other.getName()))) &&
				((this.company == null && other.company == null) || (this.company != null && this.getCompany().equals(other.getCompany()))) &&
				((this.version == null && other.version == null) || (this.version != null && this.getVersion().equals(other.getVersion())));
		
		return retval;
	}
	
	@Override
	public int hashCode()
	{
		return (this.getId() + "," + this.getName() + "," + this.getCompany() + "," + this.getVersion()).hashCode();
	}
	
	@Override
	public String toString()
	{
		return "Name:" + this.name + ", company:" + this.company + ", version:" + this.version;
	}
}
