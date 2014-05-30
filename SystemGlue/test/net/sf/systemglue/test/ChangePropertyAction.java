package net.sf.systemglue.test;

import net.sf.systemglue.utils.BeanUtils;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

public class ChangePropertyAction implements Action{
	
	public ChangePropertyAction(String property, Object value) {
		super();
		this.property = property;
		this.value = value;
	}
	
	private String property;
	private Object value;
		
	public static Action changeProperty(String property, Object value){
		return new ChangePropertyAction(property, value);
	}
	
	@Override
	public void describeTo(Description description) {
		description.appendText("changing property")
		.appendValue(property)
		.appendText(" to ")
		.appendValue(value);
		
	}
	@Override
	public Object invoke(Invocation invocation) throws Throwable {
		BeanUtils.setProperty(invocation.getParameter(0), property, value);
		return null;
	}
	
	

}
