package net.sf.systemglue.finder;

public interface ObjectFinder {

	public abstract <E> E findObject(Class<E> clazz);

}