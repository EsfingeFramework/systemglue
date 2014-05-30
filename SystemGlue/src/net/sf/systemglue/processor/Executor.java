package net.sf.systemglue.processor;

import java.util.Map;

public interface Executor {

	public void process(Map<String, Object> invocation);
	
	public Executor copy();
}
