package net.sf.systemglue.processor;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.systemglue.annotations.MessageDestination;
import net.sf.systemglue.annotations.MessageType;

public abstract class MessageSenderHandler {
	protected Context jndiContext;	
	
	protected Context initialContext(String serverIPandPort) throws NamingException {
		Hashtable<String, String> ht=new Hashtable<String, String>();
		ht.put(InitialContext.PROVIDER_URL,"jnp://" + serverIPandPort);
		InitialContext ic=new InitialContext(ht);
		
		return ic;
	}

	public abstract void send(Object returned, String ipAndPort, String destinationName,MessageDestination destination, MessageType messageType) throws NamingException, JMSException;
	
}
