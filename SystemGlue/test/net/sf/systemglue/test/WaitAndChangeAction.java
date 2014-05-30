package net.sf.systemglue.test;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

public class WaitAndChangeAction implements Action{
	
	public static int value = 0;
	
	public static Action waitAndChange(){
		return new WaitAndChangeAction();
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("waiting and executing");
	}

	@Override
	public Object invoke(Invocation invocation) throws Throwable {
		Thread.sleep(2000);
		value = 1;
		return null;
	}

}
