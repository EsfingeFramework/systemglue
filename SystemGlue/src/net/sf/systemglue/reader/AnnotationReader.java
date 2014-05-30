package net.sf.systemglue.reader;

import java.lang.annotation.Annotation;

import net.sf.systemglue.metadata.MetadataContainer;
/* *
 * Abstraction for a Reader responsible for reading specific 
 * extensible metadata annotations
 * */
public interface AnnotationReader<A extends Annotation> {
	public void readAnnotation(A annotation, MetadataContainer container);
}
