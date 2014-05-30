package net.sf.systemglue.reader;

import net.sf.systemglue.annotations.ExecutionMoment;
import net.sf.systemglue.annotations.ScheduleAt;
import net.sf.systemglue.exception.SystemGlueException;
import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.metadata.MetadataContainer;
import net.sf.systemglue.processor.ScheduleAtExecutor;

public class ScheduleAtReader implements AnnotationReader<ScheduleAt> {	
	private ScheduleAtExecutor newScheduler(Class clazz,
										   String method,	
										   String startExecution,
										   String endExecution, 
										   int secondsInterval,
										   ExecutionMoment when,
										   ObjectFinder finder){
		
		ScheduleAtExecutor sae = new ScheduleAtExecutor(clazz, 
				                                        method,
				                                        startExecution, 
				                                        endExecution, 
				                                        secondsInterval, 
				                                        when,
				                                        finder);
		
		return sae;
	}
	@Override
	public void readAnnotation(ScheduleAt annotation, MetadataContainer container) {
		ObjectFinder finder;
		try {
			finder = annotation.finder().newInstance();
			ScheduleAtExecutor exec = newScheduler(annotation.clazz(),
		                                           annotation.method(),
		                                           annotation.startExecution(),
		                                           annotation.endExecution(),
		                                           annotation.secondsInterval(),
		                                           annotation.when(),
		                                           finder);
			if(annotation.when()==ExecutionMoment.AFTER){
				container.addExecuteAfter(exec);
			}else{
				container.addExecuteBefore(exec);
			}
		} catch (InstantiationException e) {
			throw new SystemGlueException("Could not instanciate Finder",e);
		} catch (IllegalAccessException e) {
			throw new SystemGlueException("Could not access Finder",e);
		}		
	}

}
