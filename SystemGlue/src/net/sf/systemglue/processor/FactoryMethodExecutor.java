package net.sf.systemglue.processor;

import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.reader.ConfigReader;
import net.sf.systemglue.utils.BeanUtils;

public class FactoryMethodExecutor {

	public static MethodExecutor createMethodExecutor(Class clazz, String methodName,
			ObjectFinder finder, String rule, boolean async) {
		MethodExecutor me = new MethodExecutor();
		me.setAsync(async);
		me.setRule(rule);
		me.setFinder(finder);
		me.setClazz(clazz);
		me.setMethodToInvoke(BeanUtils.getFirstMethodNamed(clazz, methodName));
		
		ConfigReader configReader = new ConfigReader();
		configReader.readConfig(me,me.getMethodToInvoke());
		return me;
	}
}
