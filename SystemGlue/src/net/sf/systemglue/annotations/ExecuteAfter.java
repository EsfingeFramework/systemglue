package net.sf.systemglue.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.systemglue.finder.CreateObjectFinder;
import net.sf.systemglue.finder.ObjectFinder;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
public @interface ExecuteAfter {
	Class<?> clazz();
	String method();
	Class<? extends ObjectFinder> finder() default CreateObjectFinder.class;
	String rule() default "";
	boolean async() default false;
}
