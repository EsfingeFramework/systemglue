package net.sf.systemglue.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.systemglue.reader.MessageSenderReader;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@DelegateReader(MessageSenderReader.class)
public @interface MessageSender {	
	String IPandPort();
	String target();
	MessageDestination destination() default MessageDestination.QUEUE;
	String destinationName();
	MessageType messageType();	
}
