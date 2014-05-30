package net.sf.systemglue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


import net.sf.systemglue.exception.SystemGlueException;
import net.sf.systemglue.metadata.MetadataRepository;
import net.sf.systemglue.metadata.MethodMetadataContainer;
import net.sf.systemglue.processor.MethodExecutor;
import net.sf.systemglue.reader.AnnotationMetadataReader;

public class ReflectionGlueProxy implements InvocationHandler{
	
	private Object obj;
	private Class c;
	private SystemGlueExecutor sge = new SystemGlueExecutor();
	
	public ReflectionGlueProxy(Object obj, Class c) {
		this.obj = obj;
		this.c = c;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return sge.execute(method, args, obj, c);
	}

}
