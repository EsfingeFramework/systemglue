package net.sf.systemglue.processor;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.NamingException;

import net.sf.systemglue.annotations.MessageDestination;
import net.sf.systemglue.annotations.MessageType;

public class TopicMessageSenderHandler extends MessageSenderHandler {	
	private TopicConnection connection;
	private TopicConnectionFactory connectionFactory;
	private TopicSession session;
	private Topic topic;
	private TopicPublisher publisher;
	
	@Override
	public void send(Object returned, String ipAndPort, String destinationName,
			MessageDestination destination, MessageType messageType)
			throws NamingException, JMSException {
		if(messageType==MessageType.TEXT){
			sendMessageAsText(returned, 
                              ipAndPort,
                              destinationName, 
                              destination);
		}else{
			if (returned instanceof Serializable) {
				sendMessageAsObject((Serializable) returned, ipAndPort,	destinationName, destination);
			} else {
				throw new IllegalArgumentException("Error sending JMS message> object must be serializable");
			}
		}

	}
	
	private void sendMessageAsObject(Serializable returned, String ipAndPort,
			String destinationName, MessageDestination destination) throws NamingException, JMSException {
		prepareTopic(ipAndPort, destinationName);
		
		ObjectMessage message = this.session.createObjectMessage();
		message.setObject(returned);
		this.publisher.publish(message);
		
		cleanUp();
		
	}

	private void sendMessageAsText(Object returned, String ipAndPort,
			String destinationName, MessageDestination destination) throws JMSException, NamingException {
		
		prepareTopic(ipAndPort, destinationName);
		
		TextMessage message = this.session.createTextMessage();
		message.setText((String)returned);
		this.publisher.publish(message);
		
		cleanUp();
	}

	private void cleanUp() throws JMSException {
		this.session.close();
		this.connection.close();		
	}

	protected void prepareTopic(String ipAndPort, String destinationName)
	throws NamingException, JMSException {
		this.jndiContext = initialContext(ipAndPort);		
		this.connectionFactory = (TopicConnectionFactory) this.jndiContext.lookup("ConnectionFactory");
		this.connection = connectionFactory.createTopicConnection();
		this.session = connection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		this.topic = this.session.createTopic(destinationName);
		this.publisher = this.session.createPublisher(this.topic);
		this.connection.start();		
	}

}
