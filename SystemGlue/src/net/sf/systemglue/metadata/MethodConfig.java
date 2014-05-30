package net.sf.systemglue.metadata;

import java.util.List;
//this interface abstracts a Metadata for method parameters
public interface MethodConfig {

	public abstract void addParamName(String name);

	public abstract List<String> getParamNames();

	public abstract void setParamNames(List<String> paramNames);

	public abstract String getReturnName();

	public abstract void setReturnName(String returnName);

}