package net.sf.systemglue.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.sf.systemglue.annotations.Execute;
import net.sf.systemglue.annotations.Executions;
import net.sf.systemglue.annotations.ExecutionMoment;

@Retention(RetentionPolicy.RUNTIME)
@Executions({
@Execute(clazz=OtherClass.class,method="otherExecution",finder=MockFinder.class, when=ExecutionMoment.BEFORE),
@Execute(clazz=OtherClass.class,method="toBeExecuted",finder=MockFinder.class, when=ExecutionMoment.AFTER)})
public @interface DomainAnnotation {
}
