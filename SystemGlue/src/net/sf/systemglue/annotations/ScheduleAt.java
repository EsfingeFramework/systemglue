package net.sf.systemglue.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.systemglue.finder.CreateObjectFinder;
import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.reader.ScheduleAtReader;
/**
 * Configure a method invocation that must be scheduled.
 * To start, it's you have to configure the "nextExecution"
 * Attributes and meanings:
 * clazz = the class in which is found the method to be invoked;
 * method = a string naming the method to be invoked (without parameters); 
 * nextExecution = a date and time string, in the format dd,MM,yy,hh24,mi,ss  in wich
 * the method will be called;
 * millisInterval = when the method should be invoked more than once, this interval is
 *                  necessary. Represented in Milliseconds
 * ExecutionMoment = specifies if the scheduling happens before or after then main 
 * method execution 
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@DelegateReader(ScheduleAtReader.class)
public @interface ScheduleAt {
	Class<?> clazz();
	String method();
	
	String startExecution(); //format: "dd,MM,yyyy,hh24,mi,ss"
	String endExecution() default ""; 
	int secondsInterval() default 1;
	ExecutionMoment when() default ExecutionMoment.AFTER;
	Class<? extends ObjectFinder> finder() default CreateObjectFinder.class;
	String rule() default "";
	
}
