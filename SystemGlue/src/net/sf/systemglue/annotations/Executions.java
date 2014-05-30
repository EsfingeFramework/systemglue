package net.sf.systemglue.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
//@DelegateReader(ExecuteConfigsReader.class)
@VectorialAnnotation
public @interface Executions{
	Execute[] value();
}
