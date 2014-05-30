package net.sf.systemglue.test;

import java.util.HashMap;
import java.util.Map;

import net.sf.systemglue.finder.ObjectFinder;

public class MockFinder implements ObjectFinder{
	
	private static Map<Class,Object> mockMap = new HashMap<Class,Object>();
	
	public static void addMock(Class clazz, Object obj){
		mockMap.put(clazz, obj);
	}
	
	public static void clear(){
		mockMap.clear();
	}

	@Override
	public <E> E findObject(Class<E> clazz) {
		return (E)mockMap.get(clazz);
	}

}
