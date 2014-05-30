package net.sf.systemglue.reader;

import net.sf.systemglue.annotations.Execute;
import net.sf.systemglue.annotations.ExecutionMoment;
import net.sf.systemglue.exception.SystemGlueException;
import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.metadata.MetadataContainer;
import net.sf.systemglue.processor.FactoryMethodExecutor;
import net.sf.systemglue.processor.MethodExecutor;

public class ExecuteReader implements AnnotationReader<Execute> {

	@Override
	public void readAnnotation(Execute annotation, MetadataContainer container) {		
		try {
			ObjectFinder finder;
			finder = annotation.finder().newInstance();
			MethodExecutor me = FactoryMethodExecutor.createMethodExecutor(annotation.clazz(), 
					                                 annotation.method(), 
					                                 finder, 
					                                 annotation.rule(), 
					                                 annotation.async());
			if(annotation.when()==ExecutionMoment.AFTER){
				container.addExecuteAfter(me);
			}else{
				container.addExecuteBefore(me);
			}
		} catch (Exception e) {
			throw new SystemGlueException("Problem creating finder "+annotation.finder().getName(),e);
		}
		
	}

}