package net.sf.systemglue.test;

import net.sf.systemglue.annotations.ScheduleAt;
import net.sf.systemglue.annotations.ScheduleFor;

public class SchedulerService {
	
	@ScheduleAt(clazz=ProgrammedExecution.class,
			    method="run",
			    startExecution="24,08,2011,23,11,30")
	public void executeAt(long id) {
		System.err.println("executing SchedulerService AT, execute()");
	}
	
	@ScheduleFor(clazz=ProgrammedExecution.class,
		         method="run",
		         startInSeconds=(int) ProgrammedExecution.INTERVAL, 
		         startInHours = 0, 
		         startInMinutes = 0)
public void executeFor(long id) {
	System.err.println("executing SchedulerService FOR, execute()");
}

}
