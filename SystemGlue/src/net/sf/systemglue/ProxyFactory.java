package net.sf.systemglue;

import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.systemglue.exception.SystemGlueException;

public class ProxyFactory {
	
	public final static String REFLECTION = "REFLECTION";
	public final static String CGLIB = "CGLIB";
	
	private static String proxyImplementation = CGLIB;
	
	public static Object createProxy(Object obj){
		return createProxy(obj,obj.getClass());
	}

	public static Object createProxy(Object obj, Class c){
		if(proxyImplementation.equals(CGLIB))
			return createCGLIBProxy(obj,c);
		if(proxyImplementation.equals(REFLECTION))
			return createReflectionProxy(obj,c);
		throw new SystemGlueException("Unrecognized proxy implementation configured: "+proxyImplementation);
	}

	private static Object createReflectionProxy(Object obj, Class c) {
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(),
									  obj.getClass().getInterfaces(),
									  new ReflectionGlueProxy(obj,c));
	}
	
	private static Object createCGLIBProxy(Object obj, Class c) {
		try {
			CGLIBGlueProxy proxy = new CGLIBGlueProxy(obj,c);
			Enhancer e = new Enhancer();
			e.setSuperclass(obj.getClass());
			e.setCallback(proxy);
			return e.create();			
		} catch (Throwable e) {
			throw new SystemGlueException("Fail to create proxy",e);
		}
	}
	
	public static void setProxyImplementation(String impl){
		proxyImplementation = impl;
	}
	

}
