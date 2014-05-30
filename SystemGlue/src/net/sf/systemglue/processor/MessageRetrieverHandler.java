package net.sf.systemglue.processor;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.systemglue.annotations.MessageDestination;
import net.sf.systemglue.annotations.MessageType;

public class MessageRetrieverHandler {
	private static final long SECOND_TO_MILLIS = 1000L; 
	
	public Object getJmsMessage(String ipAndPort,
			                     MessageDestination destination, 
			                     MessageType messageType, 
			                     String destinationName, 
			                     long timeoutInSeconds) throws NamingException, JMSException {
		
		Object messageObj = null;
		Message message = null;
		if(destination==MessageDestination.QUEUE){			
			message = consumeFromQueue(ipAndPort, destinationName, messageType, timeoutInSeconds);			
		}else{ //TOPIC
			message = consumeFromTopic(ipAndPort, destinationName, messageType, timeoutInSeconds);
		}
		
		if(message != null){
			if(messageType == MessageType.TEXT){
				messageObj = ((TextMessage)message).getText();
			}else{
				messageObj = ((ObjectMessage)message).getObject();
			}
		}
		
		return messageObj;
	}

	private Message consumeFromTopic(String ipAndPort,String destinationName, MessageType messageType, long timeoutInSeconds) throws NamingException, JMSException {
		Context jndiContext = initialContext(ipAndPort);
		
		Message message = null;		
		TopicConnectionFactory connectionFactory = (TopicConnectionFactory) jndiContext.lookup("ConnectionFactory");		
		Topic topic = (Topic) jndiContext.lookup(destinationName);		
		TopicConnection connection = connectionFactory.createTopicConnection();
		TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);		
		TopicSubscriber subscriber = session.createSubscriber(topic);	
		connection.start();		
		int attempts = 0;
		while(message==null&& attempts <5){
			message = subscriber.receive(timeoutInSeconds * SECOND_TO_MILLIS);
			attempts++;
		}
		
		connection.close();
		session.close();
		
		return message;
	}

	private Message consumeFromQueue(String ipAndPort,String destinationName, MessageType messageType, long timeoutInSeconds) throws NamingException, JMSException {		
		Context jndiContext = initialContext(ipAndPort);
		
		Message message = null;		
		QueueConnectionFactory connectionFactory = (QueueConnectionFactory) jndiContext.lookup("ConnectionFactory");		
		Queue queue = (Queue) jndiContext.lookup(destinationName);		
		QueueConnection connection = connectionFactory.createQueueConnection();
		QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);		
		QueueReceiver receiver = session.createReceiver(queue);	
		connection.start();		
		int tentativas = 0;
		while(message==null&&tentativas<5){
			message = receiver.receive(timeoutInSeconds * SECOND_TO_MILLIS);
			tentativas++;
		}
		
		connection.stop();
		session.close();
		
		return message;
	}

	private Context initialContext(String serverIPandPort) throws NamingException {
		Hashtable<String, String> ht=new Hashtable<String, String>();
		ht.put(InitialContext.PROVIDER_URL,"jnp://" + serverIPandPort);
		InitialContext ic=new InitialContext(ht);
		
		return ic;
	}
}
