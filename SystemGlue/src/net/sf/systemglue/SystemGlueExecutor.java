package net.sf.systemglue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


import net.sf.systemglue.exception.SystemGlueException;
import net.sf.systemglue.metadata.MetadataRepository;
import net.sf.systemglue.metadata.MethodMetadataContainer;
import net.sf.systemglue.processor.Executor;
//framework entry point
public class SystemGlueExecutor {
	
	public Object execute(Method method, Object[] args, Object obj, Class c) throws Throwable{
		
		MethodMetadataContainer mmc = MetadataRepository.getMetadata(c, method);
		
		Map<String,Object> invocation = new HashMap<String, Object>();
		
		if(args == null){
			args = new Object[0];
		}	
		for(int i=0; i<args.length; i++){			
			invocation.put(mmc.getParamNames().get(i), args[i]);			
		}
		
		for(Executor me : mmc.getExecuteBefore()){
			me.process(invocation);
		}
		
		for(int i=0; i<args.length; i++){
			args[i] = invocation.get(mmc.getParamNames().get(i));
		}
		
		Object returned;
		try {
			returned = method.invoke(obj, args);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}catch (Exception e) {
			throw new SystemGlueException("Problems invoking method",e);
		} 
		invocation.put(mmc.getReturnName(), returned);
		
		for(Executor me : mmc.getExecuteAfter()){
			me.process(invocation);
		}
		
		return invocation.get(mmc.getReturnName());
	}
}
