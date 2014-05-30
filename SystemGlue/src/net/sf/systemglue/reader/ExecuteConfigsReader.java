package net.sf.systemglue.reader;

import net.sf.systemglue.annotations.Execute;
import net.sf.systemglue.annotations.Executions;
import net.sf.systemglue.metadata.MetadataContainer;

public class ExecuteConfigsReader implements AnnotationReader<Executions> {
	//process the Array of @Execute
	@Override
	public void readAnnotation(Executions annotation,
			MetadataContainer container) {
		ExecuteReader exeRdr = new ExecuteReader();
		for(Execute exe: annotation.value()){			
			exeRdr.readAnnotation(exe,container);
		}
	}

}
