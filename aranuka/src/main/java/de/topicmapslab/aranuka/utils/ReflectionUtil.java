/**
 * 
 */
package de.topicmapslab.aranuka.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class ReflectionUtil {

	public static boolean isCollection(Field field) {
		
		Type type = field.getGenericType();
		Class<?> classType = null;
		
		if (type instanceof ParameterizedType) {
			
			classType = (Class<?>) ((ParameterizedType)type).getRawType();
			
		}else{
			
			classType = (Class<?>) type;
		}
		
		return instanceOf(classType, Collection.class);
	}
	
	public static boolean instanceOf(Class<?>test, Class<?> clazz) {
		
		if (test.equals(clazz))
			return true;
		
		for (Class<?> i : test.getInterfaces()) {
			
			if (i.equals(clazz))
				return true;
		}	
		
		return false;
	}

	public static Class<?> getGenericType(Field field) {
		
		Type returnType = field.getGenericType();
		
		if (returnType instanceof ParameterizedType) {
			
			ParameterizedType pt = (ParameterizedType) returnType;
			Type[] args = pt.getActualTypeArguments();
			
			for (Type typeArg : args) {
				
				Class<?> typeArgClass = (Class<?>) typeArg;
				
				return typeArgClass;
			}
		}

		return (Class<?>) returnType;
	}

}
