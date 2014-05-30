package net.sf.systemglue.metadata;

import java.util.ArrayList;
import java.util.List;

import net.sf.systemglue.processor.Executor;


public class AnnotationMetadataContainer implements MetadataContainer {
	
	private List<Executor> executeBefore = new ArrayList<Executor>();
	private List<Executor> executeAfter = new ArrayList<Executor>();
	@Override
	public void addExecuteBefore(Executor me){
		executeBefore.add(me);
	}
	@Override
	public void addExecuteAfter(Executor me){
		executeAfter.add(me);
	}
	@Override
	public List<Executor> getExecuteBefore() {
		return executeBefore;
	}
	@Override
	public void setExecuteBefore(List<Executor> executeBefore) {
		this.executeBefore = executeBefore;
	}
	@Override
	public List<Executor> getExecuteAfter() {
		return executeAfter;
	}
	@Override
	public void setExecuteAfter(List<Executor> executeAfter) {
		this.executeAfter = executeAfter;
	}
}
