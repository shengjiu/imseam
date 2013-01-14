package com.imseam.cdi.chatlet.annotations;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.imseam.cdi.chatlet.spi.Constants;
import com.imseam.common.util.StringUtil;

public class AbstractChatletEventAnnotation<T extends Annotation>
implements Annotation, Serializable{

	private static final long serialVersionUID = -6715717440072025095L;
	private String chatflow;
	private String state;
	private transient Class<T> annotationType;
	private transient Method[] members;
	public final static String ALL = "*"; 

	
	protected AbstractChatletEventAnnotation(){
	}

	public void setChatflowAndState(String chatflow, String state){
		this.chatflow = chatflow;
		this.state = state;
		assert(!StringUtil.isNullOrEmptyAfterTrim(chatflow));
		assert(!StringUtil.isNullOrEmptyAfterTrim(state));
	}
	

	public String chatflow() {
		return chatflow;
	}

	public String state() {
		return state;
	}

	private Method[] getMembers() {
		if (members == null) {
			members = annotationType().getDeclaredMethods();
			assert(members.length == 2 
					&&(members[0].getName().equals("chatflow") || members[0].getName().equals("state"))
					&&(members[1].getName().equals("chatflow") || members[1].getName().equals("state")));
			
			if (members.length > 0 && !annotationType().isAssignableFrom(this.getClass())) {
				throw new RuntimeException(getClass() + " does not implement the annotation type with members " + annotationType().getName());
			}
		}
		return members;
	}

	private static Class<?> getAnnotationLiteralSubclass(Class<?> clazz) {
		Class<?> superclass = clazz.getSuperclass();
		if (superclass.equals(AbstractChatletEventAnnotation.class)) {
			return clazz;
		} else if (superclass.equals(Object.class)) {
			return null;
		} else {
			return (getAnnotationLiteralSubclass(superclass));
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> getTypeParameter(Class<?> annotationLiteralSuperclass) {
		Type type = annotationLiteralSuperclass.getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			if (parameterizedType.getActualTypeArguments().length == 1) {
				return (Class<T>) parameterizedType.getActualTypeArguments()[0];
			}
		}
		return null;
	}

	public Class<? extends Annotation> annotationType() {
		if (annotationType == null) {
			Class<?> annotationLiteralSubclass = getAnnotationLiteralSubclass(this.getClass());
			if (annotationLiteralSubclass == null) {
				throw new RuntimeException(getClass() + "is not a subclass of AnnotationLiteral");
			}
			annotationType = getTypeParameter(annotationLiteralSubclass);
			if (annotationType == null) {
				throw new RuntimeException(getClass() + " does not specify the type parameter T of AnnotationLiteral<T>");
			}
		}
		return annotationType;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append('@').append(annotationType().getName()).append('(').append(chatflow).append('-').append(state).append(')');
		return string.toString();
	}
	
	public String getTransitionName(){
		return annotationType().getSimpleName();
	}


	@Override
	public boolean equals(Object other) {
		if (other instanceof Annotation) {
			Annotation that = (Annotation) other;
			if (this.annotationType().equals(that.annotationType())) {
				for (Method member : getMembers()) {
					Object thatValue = invoke(member, that);
					if(Constants.CHATFLOW_QUALIFIERS_ALL.equals(thatValue)) continue;
					if(member.getName().equals("chatflow")){
						if(!this.chatflow.equals(thatValue)){
							return false;
						}
					}
					if(member.getName().equals("state")){
						if(this.state.equals(thatValue)){
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (Method member : getMembers()) {
			int memberNameHashCode = 127 * member.getName().hashCode();
			Object value = null;
			if(member.getName().equals("chatflow")){
				value = this.chatflow;
			}
			if(member.getName().equals("state")){
				value = this.state;
			}			
			int memberValueHashCode;
			memberValueHashCode = value.hashCode();
			hashCode += memberNameHashCode ^ memberValueHashCode;
		}
		return hashCode;
	}

	private static Object invoke(Method method, Object instance) {
		try {
			if (!method.isAccessible())
				method.setAccessible(true);
			return method.invoke(instance);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Error checking value of member method " + method.getName() + " on " + method.getDeclaringClass(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Error checking value of member method " + method.getName() + " on " + method.getDeclaringClass(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Error checking value of member method " + method.getName() + " on " + method.getDeclaringClass(), e);
		}
	}

//	private static void assertMemberValueNotNull(String member, Annotation instance, Object value) {
//		if (value == null) {
//			throw new IllegalArgumentException("Annotation member " + instance.getClass().getName() + "." + member + " must not be null");
//		}
//	}

}
