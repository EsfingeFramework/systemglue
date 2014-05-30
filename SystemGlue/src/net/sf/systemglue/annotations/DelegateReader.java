package net.sf.systemglue.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.systemglue.reader.AnnotationReader;
/* *
 * Annotation that identifies new metadata that will be read by the 
 * implementation of AnnotationReader
 * */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DelegateReader {
	Class<? extends AnnotationReader> value();
}
