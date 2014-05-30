package net.sf.systemglue.metadata;

import java.util.ArrayList;
import java.util.List;

import net.sf.systemglue.processor.Executor;


public class MethodMetadataContainer implements MethodConfig,MetadataContainer {
	
	private List<Executor> executeBefore = new ArrayList<Executor>();
	private List<Executor> executeAfter = new ArrayList<Executor>();
	
	private List<String> paramNames = new ArrayList<String>();
	private String returnName = "return";
	
	private List<Class> annotations = new ArrayList<Class>(); 
	
	public void addExecuteBefore(Executor me){
		executeBefore.add(me);
	}
	public void addExecuteAfter(Executor me){
		executeAfter.add(me);
	}
	public void addParamName(String name){
		paramNames.add(name);
	}
	public List<Executor> getExecuteBefore() {
		return executeBefore;
	}
	public void setExecuteBefore(List<Executor> executeBefore) {
		this.executeBefore = executeBefore;
	}
	public List<Executor> getExecuteAfter() {
		return executeAfter;
	}
	public void setExecuteAfter(List<Executor> executeAfter) {
		this.executeAfter = executeAfter;
	}
	public List<String> getParamNames() {
		return paramNames;
	}
	public void setParamNames(List<String> paramNames) {
		this.paramNames = paramNames;
	}
	public String getReturnName() {
		return returnName;
	}
	public void setReturnName(String returnName) {
		this.returnName = returnName;
	}
	
	public void addAnnotation(Class anot){
		annotations.add(anot);
	}
	
	public void readAnnotationContainer(AnnotationMetadataContainer amc){
		for(Executor me : amc.getExecuteBefore()){
			addExecuteBefore(me.copy());
		}
		for(Executor me : amc.getExecuteAfter()){
			addExecuteAfter(me.copy());
		}
	}
	
	public void fireAnnotationConfigured(Class c, AnnotationMetadataContainer amc){
		if(annotations.contains(c)){
			readAnnotationContainer(amc);
		}
	}

}
