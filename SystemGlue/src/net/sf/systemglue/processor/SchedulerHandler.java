package net.sf.systemglue.processor;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.systemglue.exception.SystemGlueException;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerHandler {
	private static SchedulerHandler instance = new SchedulerHandler();
	private Scheduler scheduler;

	public static SchedulerHandler getSchedulerHandler() {
		return instance;
	}

	public void shutdown() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	private SchedulerHandler() {
		super();
		SchedulerFactory factory = new StdSchedulerFactory();
		try {
			scheduler = factory.getScheduler();
		} catch (SchedulerException e) {
			throw new SystemGlueException("Error trying to create Scheduler", e);
		}
	}

	public void scheduleAt(Class clazz, String method,
			Map<String, Object> paramValues, String startExecution,
			String endExecution, int secondsInterval) {

		JobDetail jobDetail;
		Trigger trigger;
		Method classMethod;
		Object[] args;
		// finds the method based on parameters name
		classMethod = getClassMethod(clazz, method, paramValues);
		args = new Object[classMethod.getParameterTypes().length];
		setParameterValues(paramValues, args, classMethod);
		jobDetail = newJob(JobExecution.class).build();

		jobDetail.getJobDataMap().put("class", clazz);
		jobDetail.getJobDataMap().put("method", classMethod);
		jobDetail.getJobDataMap().put("args", args);

		Date nextExecution = convertAnnotToDate(startExecution);
		if (endExecution != null && endExecution.length() > 0) {
			endExecution = startExecution;
		}
		Date lastExecution = convertAnnotToDate(endExecution);
		trigger = newTrigger()
				.startAt(nextExecution)
				.withSchedule(
						simpleSchedule().withIntervalInSeconds(secondsInterval))
				.endAt(lastExecution).build();

		try {
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();
		} catch (SchedulerException e) {
			throw new SystemGlueException("error: could not start scheduler", e);
		}
	}

	public void scheduleFor(Class clazz, String method,
			Map<String, Object> paramValues, int intervalInHours,
			int intervalInMinutes, int intervalInSeconds, int secondsInterval) {

		JobDetail jobDetail;
		Trigger trigger;
		Method classMethod;
		Object[] args;
		// finds the method based on parameters name
		classMethod = getClassMethod(clazz, method, paramValues);
		args = new Object[classMethod.getParameterTypes().length];
		setParameterValues(paramValues, args, classMethod);
		jobDetail = newJob(JobExecution.class).build();

		jobDetail.getJobDataMap().put("class", clazz);
		jobDetail.getJobDataMap().put("method", classMethod);
		jobDetail.getJobDataMap().put("args", args);		

		long totalTime = ((intervalInHours * 3600) + (intervalInMinutes * 60) + (intervalInSeconds)) * 1000L;
		Date startTime = new Date(System.currentTimeMillis() + totalTime);

		trigger = newTrigger().startAt(startTime).build();
		try {
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();
		} catch (SchedulerException e) {
			throw new SystemGlueException(
					"error: could not execute scheduler for the specified time",
					e);
		}
	}	

	private void setParameterValues(Map<String, Object> paramValues,
			Object[] args, Method methodToExecute) {
		Class[] parameters = methodToExecute.getParameterTypes();
		Map<String, Object> tempMap = copyMap(paramValues);
		int index = 0;
		for (int i = 0; i < parameters.length; i++) {
			// parameters (name and value) from the metadata
			for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
				if (parametersOfTheSameType(entry, parameters[i])) {
					args[index] = entry.getValue();
					index++;
					tempMap.remove(entry.getKey());
					break;
				}
			}
		}
	}

	private Date convertAnnotToDate(String executionDate) {
		final int DAY = 0;
		final int MON = 1;
		final int YEAR = 2;
		final int HOUR = 3;
		final int MIN = 4;
		final int SEC = 5;

		Date date = null;
		if (executionDate != null && executionDate.length() > 0) {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yy H:m:s");

			String newDateTime = null;
			try {
				// date = (Date)formatter.parse(executionDate);
				String[] composition = executionDate.split(",");
				newDateTime = composition[DAY] + "/" + composition[MON] + "/"
						+ composition[YEAR] + " " + composition[HOUR] + ":"
						+ composition[MIN] + ":" + composition[SEC];
				date = (Date) formatter.parse(newDateTime);

			} catch (ParseException e) {
				throw new SystemGlueException("Error converting schedule date",
						e);
			}
		}
		return date;
	}

	//
	private Method getClassMethod(Class clazz, String method,
			Map<String, Object> paramValues) {
		Method methodToFind = null;
		Method[] classMethods = clazz.getMethods();
		for (int i = 0; i < classMethods.length; i++) {
			// search for a method with the parameters in the Map
			if (method.equals(classMethods[i].getName())) {
				if (methodHasAllParameters(classMethods[i], paramValues)) {
					methodToFind = classMethods[i];
					break;
				}
			}
		}
		if (methodToFind == null) {
			for (int i = 0; i < classMethods.length; i++) {
				// search for a method with the parameters in the Map
				if (method.equals(classMethods[i].getName())) {
					if (someParametersMatchMethodSignature(classMethods[i], paramValues)) {
						methodToFind = classMethods[i];
						break;
					}
				}
			}
		}
		if (methodToFind == null) {
			for (int i = 0; i < classMethods.length; i++) {
				// search for a method with the parameters in the Map
				if (method.equals(classMethods[i].getName())) {
					if (methodIsVoid(classMethods[i])) {
						methodToFind = classMethods[i];
						break;
					}
				}
			}
		}
		return methodToFind;
	}

	private boolean methodIsVoid(Method method) {
		return method.getReturnType()== Void.TYPE;
	}

	private boolean someParametersMatchMethodSignature(Method method, Map<String, Object> params) {
		// get the real parameters in the method
		Class[] parameters = method.getParameterTypes();
		boolean parameterFound = false;
		Map<String, Object> tempParamMap = copyMap(params);
		tempParamMap.putAll(params);
		// any of this parameters are in the list?
		for (int j = 0; j < parameters.length; j++) {
			parameterFound = false;
			for (Map.Entry<String, Object> entry : tempParamMap.entrySet()) {
				if (parametersOfTheSameType(entry, parameters[j])) {
					parameterFound = true;
					tempParamMap.remove(entry.getKey());
					break;
				}
			}
			if (! parameterFound) {
				break;
			}
		}
		return parameterFound;
	}

	private boolean methodHasAllParameters(Method method, Map<String, Object> params) {
		// get the real parameters in the method
		Class[] parameters = method.getParameterTypes();
		boolean parameterFound = true;
		Map<String, Object> tempMap = copyMap(params);
		// all of the parameters method are in the Map?
		for (int j = 0; j < parameters.length; j++) {
			parameterFound = false;
			for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
				if (parametersOfTheSameType(entry, parameters[j])) {
					parameterFound = true;
					tempMap.remove(entry.getKey());										
					break;
				}
			}
			if (!parameterFound) {
				break;
			}
		}
		return parameterFound && tempMap.size()==0;
	}

	private Map<String, Object> copyMap(Map<String, Object> params) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(params);
		return map;
	}

	private boolean parametersOfTheSameType(Entry entry, Class clazz) {
		if (entry.getValue() == null || clazz == null) {
			return false;
		}
		return clazz.equals(entry.getValue().getClass())|| classIsWraper(entry.getValue().getClass(), clazz.getName());
	}

	private boolean classIsWraper(Class clazz, String type) {
		HashMap<String, Class<?>> wraperTypes = new HashMap<String, Class<?>>();
		wraperTypes.put("int", Integer.class);
		wraperTypes.put("long", Long.class);
		wraperTypes.put("char", Character.class);
		wraperTypes.put("boolean", Boolean.class);
		wraperTypes.put("byte", Byte.class);
		wraperTypes.put("short", Short.class);
		wraperTypes.put("float", Float.class);
		wraperTypes.put("double", Double.class);
		
		return wraperTypes.get(type).equals(clazz);
	}

	private Class[] objectToClass(Object[] args) {
		int size = args.length;
		if (size > 0) {
			Class[] arg = new Class[size];
			for (int i = 0; i < size; i++) {
				arg[i] = args[i].getClass();
			}
			return arg;
		}
		return null;
	}
	
}
