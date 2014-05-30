package net.sf.systemglue.processor;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import net.sf.systemglue.annotations.MessageDestination;
import net.sf.systemglue.annotations.MessageType;

public class QueueMessageSenderHandler extends MessageSenderHandler {	
	private QueueConnectionFactory connectionFactory;
	private Queue queue;
	private QueueSender sender;
	private QueueConnection connection; 
	private QueueSession session;
		
	protected void prepareQueue(String ipAndPort, String destinationName)throws NamingException, JMSException {		
		this.jndiContext = initialContext(ipAndPort);		
		this.connectionFactory = (QueueConnectionFactory) this.jndiContext.lookup("ConnectionFactory");
		this.queue = (Queue) this.jndiContext.lookup(destinationName);
		this.connection = this.connectionFactory.createQueueConnection();
		this.session = this.connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);		
		this.sender = this.session.createSender(this.queue);
	}
	
	public void send(Object returned, 
			         String ipAndPort, 
			         String destinationName,	
			         MessageDestination destination, 
			         MessageType messageType)	throws NamingException, JMSException {
		if (returned != null) {
			if (messageType == MessageType.TEXT) {
				sendMessageAsText(returned, ipAndPort, destinationName,	destination);
			} else {
				if (returned instanceof Serializable) {
					sendMessageAsObject((Serializable) returned, ipAndPort,	destinationName, destination);
				} else {
					throw new IllegalArgumentException("Error sending JMS message> object must be serializable");
				}
			}
		}
	}

	private void sendMessageAsText(Object returned, 
			                       String ipAndPort,
			                       String destinationName, 
			                       MessageDestination destination)
			throws NamingException, JMSException {
		
		prepareQueue(ipAndPort, destinationName);

		TextMessage message = this.session.createTextMessage();
		message.setText((String) returned);
		this.sender.send(message);

		cleanUp();
	}

	private void sendMessageAsObject(Serializable objReturned,
			                         String ipAndPort, 
			                         String destinationName,
			                         MessageDestination destination) throws NamingException, JMSException {
		prepareQueue(ipAndPort, destinationName);

		ObjectMessage message = this.session.createObjectMessage();
		message.setObject(objReturned);
		this.sender.send(message);

		cleanUp();
	}
	
	private void cleanUp() throws JMSException {
		this.sender.close();
		this.session.close();
		this.connection.stop();
		this.connection.close();
	}
}
