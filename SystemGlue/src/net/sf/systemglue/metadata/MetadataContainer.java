package net.sf.systemglue.metadata;

import java.util.List;

import net.sf.systemglue.processor.Executor;

public interface MetadataContainer {

	public abstract void addExecuteBefore(Executor me);
	public abstract void addExecuteAfter(Executor me);
	public abstract List<Executor> getExecuteBefore();
	public abstract void setExecuteBefore(List<Executor> executeBefore);
	public abstract List<Executor> getExecuteAfter();
	public abstract void setExecuteAfter(List<Executor> executeAfter);
}