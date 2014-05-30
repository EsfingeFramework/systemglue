package net.sf.systemglue.processor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.systemglue.exception.SystemGlueException;
import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.metadata.MethodConfig;
import net.sf.systemglue.utils.BeanUtils;
import net.sf.systemglue.utils.ELUtils;

import org.jboss.el.lang.EvaluationContext;

/* 
 *Class responsible for configuring and executing a list of methods
 *and also getting their Parameters  
 */
public class MethodExecutor implements MethodConfig, Executor{
	
	private Object obj;
	private Method methodToInvoke;
	
	private List<String> paramNames = new ArrayList<String>();
	private String returnName = "return";
	
	private String rule;
	private boolean async;
	
	private ObjectFinder finder;
	private Class clazz;
	
	
	public void process(Map<String,Object> invocation){		
		//when a rule is defined, evaluates it to decide if executeMethod() execution should go on.
		if(!verifyRule(invocation)){
			return;
		}
		final Object[] args = new Object[paramNames.size()];
		populateParams(invocation, args);
		try {
			if(!async){
				Object returned = getMethodToInvoke().invoke(getObjToInvoke(), args);
				if(returned != null){
					invocation.put(returnName, returned);
				}
			}else{
				Runnable run = new Runnable(){
					public void run() {
						try {
							getMethodToInvoke().invoke(getObjToInvoke(), args);
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
	
	//populates the "args" parameter with an object or property value, depending on the objects found in "invocation"
	private void populateParams(Map<String, Object> invocation, Object[] args) {
		for(int i =0; i<args.length ; i++){
			String paramName = this.paramNames.get(i);
			if(! paramName.contains(".")){
				args[i] = invocation.get(paramName);
			}else{
				//separates object name and its property/method
				int pointIndex = paramName.indexOf(".");
				String propName = paramName.substring(pointIndex+1); 
				paramName = paramName.substring(0,pointIndex);
				//if succeeds, returns the property vale 
				args[i] = BeanUtils.getProperty(invocation.get(paramName), propName);
			}
		}
	}

	private Object getObjToInvoke() {
		if(obj == null)
			//create a new class instance
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
	
	public Executor copy(){
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
