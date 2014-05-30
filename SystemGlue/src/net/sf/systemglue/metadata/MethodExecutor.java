package net.sf.systemglue.metadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.systemglue.exception.SystemGlueException;
import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.utils.BeanUtils;
import net.sf.systemglue.utils.ELUtils;

import org.jboss.el.lang.EvaluationContext;

public class MethodExecutor implements MethodConfig{
	
	private Object obj;
	private Method methodToInvoke;
	
	private List<String> paramNames = new ArrayList<String>();
	private String returnName = "return";
	
	private String rule;
	private boolean async;
	
	private ObjectFinder finder;
	private Class clazz;
	
	public void executeMethod(Map<String,Object> invocation){
		if(!verifyRule(invocation))
			return;
		final Object[] args = new Object[paramNames.size()];
		populateParams(invocation, args);
		try {
			if(!async){
				Object returned = methodToInvoke.invoke(getObjToInvoke(), args);
				if(returned != null)
					invocation.put(returnName, returned);
			}else{
				Runnable run = new Runnable(){
					public void run() {
						try {
							methodToInvoke.invoke(getObjToInvoke(), args);
						}catch (Exception e) {
							throw new SystemGlueException("Fail on asynchoronous method invocation",e);
						}
					}
				};
				Thread t = new Thread(run);
				t.start();
			}
		} catch (Exception e) {
			throw new SystemGlueException("Fail on method invocation",e);
		}
	}
	
	private boolean verifyRule(Map<String,Object> invocation){
		if(rule == null || rule.equals("")){
			return true;
		}else{
			EvaluationContext elContext = ELUtils.buildEvaluationContext(
					new HashMap<String, Method>(), 
					invocation);
					
			return ELUtils.evaluateBooleanExpression(elContext, "#{"+rule+"}");
		}
	}

	private void populateParams(Map<String, Object> invocation, Object[] args) {
		for(int i =0; i<args.length ; i++){
			String paramName = paramNames.get(i);
			if(!paramName.contains(".")){
				args[i] = invocation.get(paramName);
			}else{
				int pointIndex = paramName.indexOf(".");
				String propName = paramName.substring(pointIndex+1); 
				paramName = paramName.substring(0,pointIndex);
				args[i] = BeanUtils.getProperty(invocation.get(paramName), propName);
			}
		}
	}

	public Object getObjToInvoke() {
		if(obj == null)
			obj = finder.findObject(clazz);
		return obj;
	}


	public Method getMethodToInvoke() {
		return methodToInvoke;
	}

	public void setMethodToInvoke(Method methodToInvoke) {
		this.methodToInvoke = methodToInvoke;
	}

	public List<String> getParamNames() {
		return paramNames;
	}

	public void setParamNames(List<String> paramNames) {
		this.paramNames = paramNames;
	}

	public String getReturnName() {
		return returnName;
	}

	public void setReturnName(String returnName) {
		this.returnName = returnName;
	}

	@Override
	public void addParamName(String name) {
		paramNames.add(name);
		
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public ObjectFinder getFinder() {
		return finder;
	}

	public void setFinder(ObjectFinder finder) {
		this.finder = finder;
	}
	
	public MethodExecutor copy(){
		MethodExecutor me = new MethodExecutor();
		me.setAsync(async);
		me.setRule(rule);
		me.setFinder(finder);
		me.setClazz(clazz);
		me.setMethodToInvoke(methodToInvoke);
		me.setParamNames(paramNames);
		me.setReturnName(returnName);
		return me;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	

	
}
