package net.sf.systemglue.metadata;

import java.util.ArrayList;
import java.util.List;

import net.sf.systemglue.processor.Executor;


public class SchedulerMetadataContainer implements MetadataContainer {
	private List<Executor> executeBefore = new ArrayList<Executor>();
	private List<Executor> executeAfter = new ArrayList<Executor>();
	
	public SchedulerMetadataContainer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addExecuteBefore(Executor me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addExecuteAfter(Executor me) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Executor> getExecuteBefore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExecuteBefore(List<Executor> executeBefore) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Executor> getExecuteAfter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExecuteAfter(List<Executor> executeAfter) {
		// TODO Auto-generated method stub

	}

}
