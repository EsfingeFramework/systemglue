package net.sf.systemglue.processor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.systemglue.exception.SystemGlueException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class JobExecution implements Job {
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//gets the DataMap in the context
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		//populates with method parameters type, if exists any  
		Object[] args = populateParametersTypes(dataMap);
		//invoke the method
		Class clazz = (Class)dataMap.get("class");
		Method method = (Method)dataMap.get("method");		
		try {
			Object obj = clazz.newInstance();			
			method.invoke(obj, args);	
			//SchedulerHandler.getSchedulerHandler().shutdown();
		} catch (Exception e) {
			String errMsg = "";
			if(clazz != null){
				errMsg = clazz.getName();
			}
			String argList = "";
			if(args != null){
				for(int i=0; i<args.length;i++){
					if(args[i] != null){
						argList += args[i].getClass().getName()+",";
					}
				}
			}
			if(method !=null){
				errMsg += "." +method.getName()+"("+ argList+")";
			}
			
			
			throw new SystemGlueException("Could not execute the scheduled job:"+errMsg, e);
		}
	}

	private Object[] populateParametersTypes(JobDataMap dataMap) {		
		List params = new ArrayList();
		Iterator iterator = dataMap.keySet().iterator();
		while(iterator.hasNext()){
			//properties passed by the scheduler
			String propertyName = (String) iterator.next();	
			//includes only properties corresponding to the method signature (parameters)
			if(! propertyName.equals("class") && ! propertyName.equals("method")){
				if(dataMap.get(propertyName).getClass().isArray()){
					Object[] objs =(Object[])dataMap.get(propertyName);
					for(int i=0;i<objs.length;i++){
						params.add(objs[i]);
					}
				}else{
					Object obj = dataMap.get(propertyName);
					params.add(obj);
				}
							}
		}
		Object[] args = null;
		if(params.size()>0 && params.get(0)!=null){
			args = new Object[params.size()];
			int index = 0;	
			for(Object p: params){
				args[index] = p;
			}
		}
		return args;		
	}

}
