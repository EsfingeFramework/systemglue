package net.sf.systemglue.reader;

import net.sf.systemglue.annotations.ExecutionMoment;
import net.sf.systemglue.annotations.ScheduleFor;
import net.sf.systemglue.exception.SystemGlueException;
import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.metadata.MetadataContainer;
import net.sf.systemglue.processor.ScheduleForExecutor;

public class ScheduleForReader implements AnnotationReader<ScheduleFor> {

	private ScheduleForExecutor newScheduler(Class clazz, 
			                                 String method,
			                                 int startInHours, 
			                                 int startInMinute, 
			                                 int startInSecond,
			                                 int secondsInterval, 
			                                 ExecutionMoment when,
			                                 ObjectFinder finder, 
	                                         String rule) {

		ScheduleForExecutor sfe = new ScheduleForExecutor(clazz, 
				                                          method,
				                                          startInHours, 
				                                          startInMinute, 
				                                          startInSecond, 
				                                          secondsInterval,
				                                          finder);

		return sfe;
	}

	@Override
	public void readAnnotation(ScheduleFor annotation, MetadataContainer container) {
		ObjectFinder finder;
		try {
			finder = annotation.finder().newInstance();
			ScheduleForExecutor exec = newScheduler(annotation.clazz(),
					                                annotation.method(), 
					                                annotation.startInHours(), 
					                                annotation.startInMinutes(), 
					                                annotation.startInSeconds(),
					                                annotation.secondsInterval(),
					                                annotation.when(), 
					                                finder, 
					                                annotation.rule());
			if (annotation.when() == ExecutionMoment.AFTER) {
				container.addExecuteAfter(exec);
			} else {
				container.addExecuteBefore(exec);
			}
		} catch (InstantiationException e) {
			throw new SystemGlueException("Could not instanciate Finder", e);
		} catch (IllegalAccessException e) {
			throw new SystemGlueException("Could not access Finder", e);
		}
	}

}
