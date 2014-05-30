package net.sf.systemglue.test;

import net.sf.systemglue.annotations.MessageDestination;
import net.sf.systemglue.annotations.MessageSender;
import net.sf.systemglue.annotations.MessageType;
import net.sf.systemglue.annotations.Param;

public class Cart {	
	
	@MessageSender(target="product", 
            IPandPort="127.0.0.1:1099", 
            destination=MessageDestination.QUEUE,
            destinationName="queue/QueueSystemglue",
            messageType=MessageType.TEXT)
	public void add(@Param("product")String product) {
		
	}

}
