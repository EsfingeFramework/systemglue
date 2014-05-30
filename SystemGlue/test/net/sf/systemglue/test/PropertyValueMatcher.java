package net.sf.systemglue.test;

import net.sf.systemglue.utils.BeanUtils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class PropertyValueMatcher<T> extends BaseMatcher<T> {

	public PropertyValueMatcher(String property, Object value) {
		super();
		this.property = property;
		this.value = value;
	}

	private String property;
	private Object value;
	
	public static <E> Matcher<E> propertyEquals(Class<E> clazz, String property, Object value){
		return new PropertyValueMatcher<E>(property,value);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("matching property ")
			.appendValue(property)
			.appendText(" equals to ")
			.appendValue(value);
		
	}

	@Override
	public boolean matches(Object item) {
		Object returned = BeanUtils.getProperty(item, property);
		return value.equals(returned);
	}

}
