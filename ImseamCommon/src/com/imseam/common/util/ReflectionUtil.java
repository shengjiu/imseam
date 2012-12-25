package com.imseam.common.util;

public class ReflectionUtil {
	public static boolean verifyParameterTypes(Class<?>[] requiredClasses,
			Class<?>[] realClasses) {
		
		assert(requiredClasses != null);
		assert(realClasses != null);

		if (requiredClasses.length != realClasses.length) {
			return false;
		}
		for (int i = 0; i < requiredClasses.length; i++) {
			if (!requiredClasses[i].isAssignableFrom(realClasses[i])) {
				return false;
			}
		}
		return true;
	}

}
