package net.sf.systemglue.utils;

import static net.sf.systemglue.utils.NameUtils.acessorToProperty;
import static net.sf.systemglue.utils.NameUtils.propertyToGetter;
import static net.sf.systemglue.utils.NameUtils.propertyToSetter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.systemglue.exception.InvalidPropertyException;
import net.sf.systemglue.exception.SystemGlueConfigurationException;

public class BeanUtils {
	
	private static Map<String, Class> dynamicClasses = new HashMap<String, Class>();

	public static Object getProperty(Object bean, String property) {
		try {
			if (property.indexOf(".") >= 0) {
				Object subBean = getProperty(bean, property.substring(0, property.indexOf(".")));
				if (subBean == null){
					return null;
				}
				String newProperty = property
						.substring(property.indexOf(".") + 1);
				return getProperty(subBean, newProperty);
			}
			Method method = null;
			try {
				method = bean.getClass().getMethod(propertyToGetter(property),new Class[] {});
			} catch (NoSuchMethodException e) {
				method = bean.getClass().getMethod(propertyToGetter(property, true), new Class[] {});
			}
			return method.invoke(bean, new Object[] {});
		} catch (Exception e) {
			throw new InvalidPropertyException("Can't get property " + property
					+ " in the class " + bean.getClass().getName(), e);
		}
	}

	public static void setProperty(Object bean, String property, Object value) {
		try {
			if (property.indexOf(".") >= 0) {
				String firstProperty = property.substring(0, property
						.indexOf("."));
				Object subBean = getProperty(bean, firstProperty);
				if (subBean == null) {
					subBean = getPropertyClass(bean.getClass(), firstProperty)
							.newInstance();
					setProperty(bean, firstProperty, subBean);
				}
				String newProperty = property
						.substring(property.indexOf(".") + 1);
				setProperty(subBean, newProperty, value);
				return;
			}
			Method setterMethod = null;
			for (Method method : bean.getClass().getMethods()) {
				if (method.getName().equals(propertyToSetter(property))) {
					setterMethod = method;
					break;
				}
			}
			if (setterMethod != null) {
				Class type = setterMethod.getParameterTypes()[0];
				if (type == null) {
					throw new InvalidPropertyException("The setter method of "
							+ property + " does not have a parameter.");
				} else if (type.isArray()
						&& !type.getComponentType().isPrimitive()) {
					Object[] array = (Object[]) Array.newInstance(type
							.getComponentType(), ((Object[]) value).length);
					for (int i = 0; i < array.length; i++) {
						array[i] = type.getComponentType().cast(
								((Object[]) value)[i]);
					}
					setterMethod.invoke(bean, new Object[] { array });
				} else {
					if (value instanceof String
							&& (type == Integer.class || type == int.class))
						value = Integer.parseInt((String) value);
					else if (value instanceof String
							&& (type == Long.class || type == long.class))
						value = Long.parseLong((String) value);
					else if (value instanceof String
							&& (type == Byte.class || type == byte.class))
						value = Byte.parseByte((String) value);
					else if (value instanceof String
							&& (type == Short.class || type == short.class))
						value = Short.parseShort((String) value);
					else if (value instanceof String
							&& (type == Float.class || type == float.class))
						value = Float.parseFloat((String) value);
					else if (value instanceof String
							&& (type == Double.class || type == double.class))
						value = Double.parseDouble((String) value);
					else if (value instanceof String
							&& (type == Boolean.class || type == boolean.class))
						value = Boolean.parseBoolean((String) value);

					setterMethod.invoke(bean, value);
				}
			}
		} catch (Exception e) {
			throw new InvalidPropertyException("Can't set property " + property
					+ " in the class " + bean.getClass().getName());
		}
	}

	public static boolean isReadOnly(Object bean, String property) {
		for (Method method : bean.getClass().getMethods()) {
			if (method.getName().equals(propertyToSetter(property))) {
				return false;
			}
		}
		return true;
	}

	public static List<String> getPropertyList(Class beanClass) {
		ArrayList<String> propertys = new ArrayList<String>();
		for (Method method : beanClass.getDeclaredMethods()) {
			if (method.getName().startsWith("get")
					|| method.getName().startsWith("is")) {
				propertys.add(acessorToProperty(method.getName()));
			}
		}
		return propertys;
	}

	public static Class getPropertyClass(Class beanClass, String property) {
		try {
			Class returnType = null;

			if (property.indexOf(".") >= 0) {
				Class subClass = getPropertyClass(beanClass, property
						.substring(0, property.indexOf(".")));
				String newProperty = property
						.substring(property.indexOf(".") + 1);
				return getPropertyClass(subClass, newProperty);
			}

			try {
				returnType = beanClass.getMethod(
						NameUtils.propertyToGetter(property), new Class[] {})
						.getReturnType();
			} catch (NoSuchMethodException e) {
				returnType = beanClass.getMethod(
						NameUtils.propertyToGetter(property, true),
						new Class[] {}).getReturnType();
			}

			if (returnType == null)
				throw new InvalidPropertyException("Can't find property "
						+ property + " in the class " + beanClass);

			return returnType;
		} catch (Exception e) {
			throw new InvalidPropertyException("Can't find property "
					+ property + " in the class " + beanClass);
		}
	}

	public static void printBean(Object bean, PrintWriter pw) {
		pw.println(NameUtils.toDefaultName(bean.getClass().getName() + ": "
				+ bean.toString()));
		for (Method method : bean.getClass().getMethods()) {
			try {
				if (method.getName().startsWith("get")
						&& method.getParameterTypes().length == 0) {
					String property = NameUtils.acessorToProperty(method
							.getName());
					pw.println(" -" + NameUtils.toDefaultName(property) + ": "
							+ method.invoke(bean, new Object[] {}).toString());
				}
			} catch (Exception e) {
				String property = NameUtils.acessorToProperty(method.getName());
				pw.println(NameUtils.toDefaultName(property) + ": "
						+ e.getMessage());
			}
		}
	}

	public static Serializable copyBySerialization(Serializable obj)
			throws Exception {
		ObjectOutputStream output = null;
		ObjectInputStream input = null;
		try {
			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			output = new ObjectOutputStream(byteOutput);
			output.writeObject(obj);
			output.flush();
			byte[] byteArray = byteOutput.toByteArray();
			ByteArrayInputStream byteInput = new ByteArrayInputStream(byteArray);
			input = new ObjectInputStream(byteInput);
			return (Serializable) input.readObject();
		} catch (Exception e) {
			throw e;
		} finally {
			if (output != null)
				output.close();
			if (input != null)
				input.close();
		}
	}

	public static Class getClass(String clazz) {
		if(dynamicClasses.containsKey(clazz)){
			return dynamicClasses.get(clazz);
		}
		try {
			return Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			throw new SystemGlueConfigurationException("Can't find class: "
					+ clazz, e);
		}
	}
	
	public static void addDynamicClass(String className, Class clazz) {
		dynamicClasses.put(className, clazz);
	}
	
	public static <E> E createClassInstance(String clazz, Class<E> type) {
		Class c = getClass(clazz);
		if(!type.isAssignableFrom(c))
			throw new SystemGlueConfigurationException("The class "+ clazz +" is not assignable to "
					+ type.getName());
		try {
			return (E)c.newInstance();
		} catch (Exception e) {
			throw new SystemGlueConfigurationException("Can't instantiate class: "
					+ clazz, e);
		}
	}
	
	public static Method getFirstMethodNamed(Class clazz, String methodName){
		for(Method m : clazz.getMethods()){
			if(m.getName().equals(methodName)){
				return m;
			}
		}
		throw new SystemGlueConfigurationException("Can't find a method named"
				+ methodName + " in class "+clazz.getName());
	}

}
