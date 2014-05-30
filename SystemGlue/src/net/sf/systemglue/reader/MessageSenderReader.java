package net.sf.systemglue.reader;

import net.sf.systemglue.annotations.MessageDestination;
import net.sf.systemglue.annotations.MessageSender;
import net.sf.systemglue.annotations.MessageType;
import net.sf.systemglue.metadata.MetadataContainer;
import net.sf.systemglue.processor.MessageSenderExecutor;

public class MessageSenderReader implements AnnotationReader<MessageSender> {

	@Override
	public void readAnnotation(MessageSender annotation,MetadataContainer container) {
		String ipAndPort = annotation.IPandPort();
		String destinationName = annotation.destinationName();
		MessageDestination destination = annotation.destination();
		String target = annotation.target();
		MessageType messageType = annotation.messageType();
		
		MessageSenderExecutor exe = new MessageSenderExecutor(ipAndPort,
			                                                  destinationName,
			                                                  destination,
			                                                  target,
			                                                  messageType);
	    container.addExecuteAfter(exe);
		
	}

}
