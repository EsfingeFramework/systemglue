package net.sf.systemglue.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.systemglue.reader.MessageRetriverReader;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@DelegateReader(MessageRetriverReader.class)
public @interface MessageRetriever {
	String IPandPort();
	String target();
	MessageDestination destination() default MessageDestination.QUEUE;
	String destinationName();
	ExecutionMoment when();
	MessageType messageType();
	long timeoutInSeconds()default 1L;
}
