package de.topicmapslab.aranuka.utils;

import gnu.trove.THashSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Utils class which creates a new hash set.
 * @author ch
 *
 */
public class HashUtil {
	
	/**
	 * Creates an THashSet if applicable, otherwise an normal HashSet
	 * @param <T> - The parameter.
	 * @return The set.
	 */
	public static <T> Set<T> createSet() {
		
		Set<T> result;
		
		try {
			
			result = new THashSet<T>();
			
		}catch(Exception e){
			
			result = new HashSet<T>();
		}
		
		return result;
	}
}
