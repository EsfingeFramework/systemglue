package net.sf.systemglue.test;

import junit.framework.Assert;
import net.sf.systemglue.ProxyFactory;

import org.junit.Test;


public class JmsExecutionTest {
	@Test
	public void sendAndReceiveTextMessage(){
		String product = "iPad 3: wi-fi 3g, with cover"; 
		//sending message
		Cart cart = (Cart)ProxyFactory.createProxy(new Cart());
		cart.add(product);
		
		ShoppingList list = (ShoppingList)ProxyFactory.createProxy(new ShoppingList());
		String shopping = list.getProduct();
		
		Assert.assertEquals(product, shopping);
		
		
	}
}
