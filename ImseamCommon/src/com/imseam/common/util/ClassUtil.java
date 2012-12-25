package com.imseam.common.util;

import java.lang.reflect.Constructor;

/**
 * @author shengjiu wang
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ClassUtil {

	public static Class loadClass(String className) throws ClassNotFoundException {

		ClassLoader tcl = Thread.currentThread().getContextClassLoader();
		Class clazz = null;

		if (tcl != null) {
			clazz = tcl.loadClass(className);
		} else {
			clazz = Class.forName(className);
		}

		return clazz;
	}

	public static <T> T createInstance(String className) {
		try {
			return ClassUtil.<T> classForName(className).newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Cannot instantiate instance of " + className + " with no-argument constructor", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Cannot instantiate instance of " + className + " with no-argument constructor", e);
		}
	}

	public static <T> Class<T> classForName(String name) {

		try {
			if (Thread.currentThread().getContextClassLoader() != null) {
				Class<?> c = Thread.currentThread().getContextClassLoader().loadClass(name);

				@SuppressWarnings("unchecked")
				Class<T> clazz = (Class<T>) c;

				return clazz;
			} else {
				Class<?> c = Class.forName(name);

				@SuppressWarnings("unchecked")
				Class<T> clazz = (Class<T>) c;

				return clazz;
			}
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Cannot load class for " + name, e);
		} catch (NoClassDefFoundError e) {
			throw new IllegalArgumentException("Cannot load class for " + name, e);
		}
	}

	// public static Object createInstance(String className) {
	// Object instance = null;
	// try {
	//
	// Class clazz = loadClass(className);
	// if (clazz != null)
	// instance = clazz.newInstance();
	// } catch (Exception exp) {
	//
	// ExceptionUtil.wrapRuntimeException(
	// "Cannot create class instance for: " + className, exp);
	// }
	// return instance;
	// }

	public static Object createInstance(String className, Class[] constractorParamClasses, Object... params) {
		Object instance = null;
		try {

			Class clazz = loadClass(className);
			if (clazz != null) {
				Constructor constructor = clazz.getConstructor(constractorParamClasses);
				instance = constructor.newInstance(params);
			}
		} catch (Exception exp) {

			ExceptionUtil.wrapRuntimeException("Cannot create class instance for: " + className, exp);
		}
		return instance;
	}

	/**
	 * Return true if class a is either equivalent to class b, or if class a is
	 * a subclass of class b, i.e. if a either "extends" or "implements" b. Note
	 * tht either or both "Class" objects may represent interfaces.
	 */
	public static boolean isSubclass(Class a, Class b) {
		// We rely on the fact that for any given java class or
		// primtitive type there is a unqiue Class object, so
		// we can use object equivalence in the comparisons.
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		for (Class x = a; x != null; x = x.getSuperclass()) {
			if (x == b) {
				return true;
			}
			if (b.isInterface()) {
				Class interfaces[] = x.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					if (isSubclass(interfaces[i], b)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static ClassLoader getClassLoader() {
		if (Thread.currentThread().getContextClassLoader() != null) {
			return Thread.currentThread().getContextClassLoader();
		} else {
			return ClassUtil.class.getClassLoader();
		}
	}

}
