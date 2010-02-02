/**
 * 
 */
package de.topicmapslab.aranuka.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Utils class for the work with reflections.
 * @author Christian Haß
 *
 */
public class ReflectionUtil {

	/**
	 * Chacks if a field is a collection.
	 * @param field - The field.
	 * @return True or false.
	 */
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
	
	/**
	 * Checks if a class is instance of an other class.
	 * @param test - The test class.
	 * @param clazz - The other class.
	 * @return True or false.
	 */
	public static boolean instanceOf(Class<?>test, Class<?> clazz) {
		
		if (test.equals(clazz))
			return true;
		
		for (Class<?> i : test.getInterfaces()) {
			
			if (i.equals(clazz))
				return true;
		}	
		
		return false;
	}

	/**
	 * Returns the generic type of an field.
	 * @param field - The field.
	 * @return The class type.
	 */
	public static Class<?> getGenericType(Field field) {
		
		Type type = field.getGenericType();
		
		return getGenericType(type);
	}
	
	/**
	 * Returns the generic type of an type.
	 * @param type - The type.
	 * @return The class type.
	 */
	public static Class<?> getGenericType(Type type){
		
		if (type instanceof ParameterizedType) {
			
			ParameterizedType pt = (ParameterizedType) type;
			Type[] args = pt.getActualTypeArguments();
			
			for (Type typeArg : args) {
				
				Class<?> typeArgClass = (Class<?>) typeArg;
				
				return typeArgClass;
			}
		}
		
		return (Class<?>)type;
		
	}

}
