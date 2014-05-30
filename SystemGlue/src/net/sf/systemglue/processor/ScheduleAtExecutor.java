package net.sf.systemglue.processor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.systemglue.annotations.ExecutionMoment;
import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.metadata.MethodConfig;
import net.sf.systemglue.utils.BeanUtils;


public class ScheduleAtExecutor implements Executor, MethodConfig {
	private Class<?> clazz;
	private String method;		
	private String startExecution;
	private String endExecution; 
	private int sencodsInterval;
	private ExecutionMoment when;
	private ObjectFinder finder;
	
	private List<String> paramNames;
	private String returnName;
	
	public ScheduleAtExecutor(Class<?> clazz, 
			                  String method,
			                  String startExecution, 
			                  String endExecution, 
			                  int sencodsInterval,
			                  ExecutionMoment when, 
			                  ObjectFinder finder ) {
		super();
		this.clazz = clazz;
		this.method = method;
		this.startExecution = startExecution;
		this.endExecution = endExecution;
		this.sencodsInterval = sencodsInterval;
		this.when = when;
		this.finder = finder;
	}
	
	@Override
	public Executor copy() {
		ScheduleAtExecutor exe = new ScheduleAtExecutor(this.clazz,
				                                        this.method,
				                                        this.startExecution,
				                                        this.endExecution,
				                                        this.sencodsInterval,
				                                        this.when,
				                                        this.finder);
		return exe;
	}

	@Override
	public void process(Map<String, Object> invocation) {
		SchedulerHandler handler = SchedulerHandler.getSchedulerHandler();
		/*
		Object[] args = null;
		args = setParameters(invocation, args);
		 */
		handler.scheduleAt(this.clazz, 
				           this.method, 
				           invocation, 
				           this.startExecution, 
				           this.endExecution, 
				           this.sencodsInterval);	
	}
	
	private void setParameters(Map<String, Object> invocation, Object[] args){
		for(int i =0; i<args.length ; i++){
			String paramName = this.paramNames.get(i);
			if(! paramName.contains(".")){
				args[i] = invocation.get(paramName);
			}else{
				//separates object name and its property/method
				int pointIndex = paramName.indexOf(".");
				String propName = paramName.substring(pointIndex+1); 
				paramName = paramName.substring(0,pointIndex);
				//if succeeds, returns the property vale 
				args[i] = BeanUtils.getProperty(invocation.get(paramName), propName);
			}
		}
		
		/*		
		Set entries = invocation.entrySet();
		Iterator it = entries.iterator();
	    int i=0;
	    while (it.hasNext()) {
	      Map.Entry entry = (Map.Entry) it.next();
	      args[i]= entry.getValue();
	      i++;
	    }
	    */
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
