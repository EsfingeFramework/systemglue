package net.sf.systemglue.processor;

import java.util.List;
import java.util.Map;
import net.sf.systemglue.annotations.ExecutionMoment;
import net.sf.systemglue.annotations.MessageDestination;
import net.sf.systemglue.annotations.MessageType;
import net.sf.systemglue.metadata.MethodConfig;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class MessageRetrieverExecutor implements Executor, MethodConfig {
	private String ipAndPort;       
	private String destinationName;
	private MessageDestination destination; 
	private String target;
	private MessageType messageType; 
	private ExecutionMoment when;
	private long timeoutInSeconds;
	
	private List<String> paramNames;
	String returnName;
    
	public MessageRetrieverExecutor(String ipAndPort,       
			                        String destinationName,
			                        MessageDestination destination, 
			                        String target,
			                        MessageType messageType, 
			                        ExecutionMoment when, 
			                        long timeoutInSeconds) {
		
		this.ipAndPort = ipAndPort;
		this.destinationName = destinationName;
		this.destination = destination;
		this.target = target;
		this.messageType = messageType;
		this.when = when;
		this.timeoutInSeconds = timeoutInSeconds;
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
	public void setParamNames(List<String> paramNames) {
		this.paramNames = paramNames;		
	}

	@Override
	public String getReturnName() {
		return this.returnName;
	}

	@Override
	public void setReturnName(String returnName) {
		this.returnName = returnName;		
	}

	@Override
	public void process(Map<String, Object> invocation) {
		MessageRetrieverHandler jmsHandler = new MessageRetrieverHandler();
		try {
			Object objMessage = jmsHandler.getJmsMessage(this.ipAndPort, this.destination, this.messageType, this.destinationName, this.timeoutInSeconds);			
			invocation.put(this.target, objMessage);
		} catch (NamingException e) {
			throw new RuntimeException("Error in resolving JMS server name",e);
		} catch (JMSException e) {
			throw new RuntimeException("Error in consuming JMS message",e);
		}		
	}
	

	@Override
	public Executor copy() {
		MessageRetrieverExecutor exe = new MessageRetrieverExecutor(this.ipAndPort, 
				                                                    this.destinationName, 
				                                                    this.destination, 
				                                                    this.target, 
				                                                    this.messageType, 
				                                                    this.when,
				                                                    this.timeoutInSeconds);
		return exe;
	}
	

}
