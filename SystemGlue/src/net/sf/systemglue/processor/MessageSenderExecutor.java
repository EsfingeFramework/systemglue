package net.sf.systemglue.processor;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.naming.NamingException;

import net.sf.systemglue.annotations.MessageDestination;
import net.sf.systemglue.annotations.MessageType;
import net.sf.systemglue.metadata.MethodConfig;

public class MessageSenderExecutor implements Executor, MethodConfig {

	private String ipAndPort;
	private String destinationName;
	private MessageDestination destination;
	private String target;
	private MessageType messageType;
	
	private List<String> paramNames;
	private String returnName;
	

	public MessageSenderExecutor(String ipAndPort, String destinationName,
			MessageDestination destination, String target,
			MessageType messageType) {
		this.ipAndPort = ipAndPort;
		this.destinationName = destinationName;
		this.destination = destination;
		this.target = target;
		this.messageType = messageType;
	}

	@Override	
	public Executor copy() {
		MessageSenderExecutor exe =  new MessageSenderExecutor(this.ipAndPort,
				                                               this.destinationName,
				                                               this.destination,
				                                               this.target,
				                                               this.messageType);
		return exe;
	}

	@Override
	public void process(Map<String, Object> invocation) {		
		try {
			Object returned = invocation.get(this.target);
			MessageSenderHandler sender;
			if(destination==MessageDestination.QUEUE){
				sender = new QueueMessageSenderHandler();
			}else{
				sender = new TopicMessageSenderHandler();
			}
			sender.send(returned, this.ipAndPort, this.destinationName, this.destination, this.messageType);
		} catch (NamingException e) {
			throw new RuntimeException("Unable to send JMS message",e);
		} catch (JMSException e) {
			throw new RuntimeException("A error ocurred while trying to send JMS message",e);
		}
	}
	@Override
	public void addParamName(String name) {
		this.paramNames.add(name);

	}

	@Override
	public List<String> getParamNames() {
		return this.paramNames;
	}

	@Override
	public String getReturnName() {
		return this.returnName;
	}

	@Override
	public void setParamNames(List<String> paramNames) {
		this.paramNames = paramNames;

	}

	@Override
	public void setReturnName(String returnName) {
		this.returnName = returnName;

	}

}
