package net.sf.systemglue.reader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.sf.systemglue.annotations.Ignore;
import net.sf.systemglue.annotations.Param;
import net.sf.systemglue.annotations.ReturnName;
import net.sf.systemglue.metadata.MethodConfig;

public class ConfigReader {
	public ConfigReader(){}
	
	public void readConfig(MethodConfig config, Method method) {
		if(method.isAnnotationPresent(ReturnName.class)){
			ReturnName rn = method.getAnnotation(ReturnName.class);
			config.setReturnName(rn.value());
		}else{
			config.setReturnName("return");
		}
		for(int i=0; i<method.getParameterTypes().length; i++){
			Annotation[] annotations = method.getParameterAnnotations()[i];
			Param p = null;			
			for(int j=0;j<annotations.length;j++){
				//found @Param in the parameter
				if(annotations[j].annotationType() == Param.class){
					p = (Param)annotations[j];
				}
			}
			if(p != null){
				//add parameter annotated
				config.addParamName(p.value());
			}else{
				String className = method.getParameterTypes()[i].getSimpleName();
				String name = className;
				int count = 1;
				while(config.getParamNames().contains(name)){
					name = className+count;
					count++;
				}
				config.addParamName(name);
			}
		}
	}

	private boolean notAnnotatedToIgnore(Annotation[] annotations) {		
		boolean ignoreFound = false;
		for(int i = 0; i<annotations.length; i++){
			if(annotations[i].getClass().equals(Ignore.class)){
				ignoreFound = true;
				break;
			}
		}
		return ignoreFound;
	}
}
