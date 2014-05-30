package net.sf.systemglue.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.metadata.MethodConfig;
import net.sf.systemglue.utils.BeanUtils;

public class ScheduleForExecutor implements Executor, MethodConfig {

	private Class clazz;
	private String method;
	private int startInHours;
	private int startInMinutes;
	private int startInSeconds;
	private ObjectFinder finder;
	//private String rule;
	private int secondsInterval;
	private List<String> paramNames;
	private String returnName;

	public ScheduleForExecutor(Class clazz, 
			                   String method, 
			                   int startInHours,
			                   int startInMinutes, 
			                   int startInSeconds, 
			                   int secondsInterval,
			                   ObjectFinder finder) {
		
		this.clazz = clazz;
		this.method = method;
		this.startInHours = startInHours;
		this.startInMinutes = startInMinutes;
		this.startInSeconds = startInSeconds;
		this.secondsInterval = secondsInterval;		
		this.finder = finder;
		//this.rule = rule;
		this.paramNames = new ArrayList<String>();
		
	}
	
	@Override
	public Executor copy() {
		ScheduleForExecutor sfe = new ScheduleForExecutor(this.clazz,
				                                          this.method,
				                                          this.startInHours,
				                                          this.startInMinutes,
				                                          this.startInSeconds,
				                                          this.secondsInterval,
				                                          this.finder);
		
		return sfe;
	}

	@Override
	public void process(Map<String, Object> invocation) {
		SchedulerHandler handler = SchedulerHandler.getSchedulerHandler();
		/*
		Object[] args = null;
		args = setParameters(invocation, args);
		 */
		
		handler.scheduleFor(this.clazz, 
				            this.method, 
				            invocation , 
				            this.startInHours, 
				            this.startInMinutes, 
				            this.startInSeconds, 
				            this.secondsInterval);	


	}

	private Object[] setParameters(Map<String, Object> invocation, Object[] args) {		
		Set entries = invocation.entrySet();
		args = new Object[entries.size()];
	    Iterator it = entries.iterator();
	    int i=0;
	    while (it.hasNext()) {
	      Map.Entry entry = (Map.Entry) it.next();
	      args[i]= entry.getValue();
	      i++;
	    }
		return args;
	}	

	@Override
	public void addParamName(String name) {
		this.paramNames.add(name);		
	}

	@Override
	public List<String> getParamNames() {
		return this.paramNames;
	}

	@Override
	public String getReturnName() {
		return this.returnName;
	}

	@Override
	public void setParamNames(List<String> paramNames) {
		this.paramNames = paramNames;		
	}

	@Override
	public void setReturnName(String returnName) {
		this.returnName = returnName;		
	}
}