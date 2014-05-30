package net.sf.systemglue.reader;

import net.sf.systemglue.annotations.ExecutionMoment;
import net.sf.systemglue.annotations.MessageDestination;
import net.sf.systemglue.annotations.MessageRetriever;
import net.sf.systemglue.annotations.MessageType;
import net.sf.systemglue.metadata.MetadataContainer;
import net.sf.systemglue.processor.MessageRetrieverExecutor;

public class MessageRetriverReader implements AnnotationReader<MessageRetriever> {

	@Override
	public void readAnnotation(MessageRetriever annotation,	MetadataContainer container) {
		String ipAndPort = annotation.IPandPort();
		String destinationName = annotation.destinationName();
		MessageDestination destination = annotation.destination();
		String target = annotation.target();
		MessageType messageType = annotation.messageType();
		ExecutionMoment when = annotation.when();
		long timeoutInSeconds = annotation.timeoutInSeconds();
		
		MessageRetrieverExecutor exe = new MessageRetrieverExecutor(ipAndPort,
				                                                    destinationName,
				                                                    destination,
				                                                    target,
				                                                    messageType,
				                                                    when,
				                                                    timeoutInSeconds); 
	    if(when==ExecutionMoment.BEFORE){
	    	container.addExecuteBefore(exe);
	    }else{
	    	container.addExecuteAfter(exe);
	    }
		
	}

}
