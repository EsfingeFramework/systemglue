package net.sf.systemglue.test;

import net.sf.systemglue.annotations.ExecutionMoment;
import net.sf.systemglue.annotations.MessageDestination;
import net.sf.systemglue.annotations.MessageRetriever;
import net.sf.systemglue.annotations.MessageType;
import net.sf.systemglue.annotations.ReturnName;

public class ShoppingList {
	
	@ReturnName("shopping")
	@MessageRetriever(target="shopping", 
	                  IPandPort="127.0.0.1:1099", 
	                  destination=MessageDestination.QUEUE,
	                  destinationName="queue/QueueSystemglue",
	                  messageType=MessageType.TEXT,
	                  when=ExecutionMoment.AFTER,
	                  timeoutInSeconds = 2)
	public String getProduct() {
		// TODO Auto-generated method stub
		return null;
	}

}
