package net.sf.systemglue;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CGLIBGlueProxy implements MethodInterceptor{

	private Object obj;
	private Class c;
	private SystemGlueExecutor sge = new SystemGlueExecutor();
	
	public CGLIBGlueProxy(Object obj, Class c) {
		this.obj = obj;
		this.c = c;
	}
	
	public Object intercept(Object obj, Method method, 
				Object[] args, MethodProxy proxy) 
                  throws Throwable {
		return sge.execute(method, args, this.obj, c);
	}

}
