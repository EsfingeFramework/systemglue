package net.sf.systemglue.finder;

import net.sf.systemglue.exception.SystemGlueException;

public class CreateObjectFinder implements ObjectFinder {
	
	public <E> E findObject(Class<E> clazz){
		E e;
		try {
			e = clazz.newInstance();
		} catch (Exception ex) {
			throw new SystemGlueException("Can't create object "+clazz.getName(),ex);
		}
		return e;
	}

}
