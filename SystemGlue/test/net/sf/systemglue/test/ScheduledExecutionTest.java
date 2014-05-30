package net.sf.systemglue.test;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import net.sf.systemglue.ProxyFactory;


public class ScheduledExecutionTest {	
	private static final int FACTOR = 1000; 
	private SchedulerService schSrvc = null;
	private long createTimestampId(){
		return System.currentTimeMillis(); 
	}
	
	@Before
	public void init(){
		schSrvc =(SchedulerService) ProxyFactory.createProxy(new SchedulerService());
	}
	
	@Test
	public void scheduleAt(){
	  
		//SchedulerService schSrvc =(SchedulerService) ProxyFactory.createProxy(new SchedulerService());		
		//creates an id based on System.currentTimeMillis
		long id = createTimestampId();
		//this execution starts the scheduler. The execution id will be useful to get timestamp 
		schSrvc.executeAt(id);
		
		Calendar expected = Calendar.getInstance();
		expected.clear();
		//the same date/time as the annotated Method
		expected.set(Calendar.YEAR, 2011);
		expected.set(Calendar.MONTH, Calendar.AUGUST);
		expected.set(Calendar.DAY_OF_MONTH, 24);
		expected.set(Calendar.HOUR_OF_DAY, 23);
		expected.set(Calendar.MINUTE, 11);
		expected.set(Calendar.SECOND, 30);
		
		try {
			Thread.sleep(20000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Assert.assertEquals((ProgrammedExecution.timestamp(id).getId()), (expected.getTimeInMillis()), FACTOR);
				
	}	
	
    @Test
	public void scheduleFor(){				
		//creates an id based on System.currentTimeMillis
		long id = createTimestampId();
		//this execution starts the scheduler. The execution id will be useful to get timestamp 
		schSrvc.executeFor(id);
		//id + Interval = expected time for execution
		long expected = (ProgrammedExecution.INTERVAL * 1000) + id;
		try {
			Thread.sleep(20000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(ProgrammedExecution.timestamp(id).getId(), expected, FACTOR);
	}
    
}
