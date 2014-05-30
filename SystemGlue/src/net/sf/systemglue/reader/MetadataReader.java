package net.sf.systemglue.reader;

import net.sf.systemglue.finder.ObjectFinder;
import net.sf.systemglue.processor.MethodExecutor;
import net.sf.systemglue.utils.BeanUtils;

public abstract class MetadataReader {

	public MetadataReader() {
		super();
	}	

	protected MethodExecutor createMethodExecutor(Class clazz, String methodName,
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