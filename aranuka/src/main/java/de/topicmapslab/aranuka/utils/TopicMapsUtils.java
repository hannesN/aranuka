package de.topicmapslab.aranuka.utils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Role;

import de.topicmapslab.aranuka.exception.TopicMapInconsistentException;

/**
 * Utils class for the work with the topic map.
 * @author Christian Haß
 *
 */
public class TopicMapsUtils {

	/**
	 * Generates a subject identifier from a class.
	 * @param clazz - The class.
	 * @return The identifier as string.
	 */
	public static String generateSubjectIdentifier(Class<?> clazz){

		StringBuilder builder = new StringBuilder();
		String nameSuffix = clazz.getName().replaceAll("\\.", "/");

		builder.append("base_locator:");
		
		builder.append(nameSuffix);
		return builder.toString();
	}
	
	/**
	 * Generates a subject identifier for an field.
	 * @param field - The field.
	 * @return - The identifier as string.
	 */
	public static String generateSubjectIdentifier(Field field){
		
		String identifier = generateSubjectIdentifier(field.getDeclaringClass());
		
		identifier+= "/" + field.getName();
		return identifier;
	}
	
	/**
	 * Resolves an uri using an prefix.
	 * @param uri - The uri.
	 * @param prefixMap - The prefix map.
	 * @return The resolved uri as string.
	 */
	public static String resolveURI(String uri, Map<String,String> prefixMap) {
		
		int idx = uri.indexOf(":");
		
		if (idx != -1) {

			String key = uri.substring(0, idx);
			
			if (prefixMap.get(key) != null) {
				
				StringBuilder builder = new StringBuilder(prefixMap.get(key));
				builder.append(uri.substring(idx + 1));
				return builder.toString();
			}
		}
		return uri;
	}

	/**
	 * Gets the counter role of an binary association.
	 * @param association - The association.
	 * @param ownRole - The own role.
	 * @return The counter role.
	 * @throws TopicMapInconsistentException
	 */
	public static Role getCounterRole(Association association, Role ownRole) throws TopicMapInconsistentException{
		
		if(association.getRoles().size() != 2)
			throw new TopicMapInconsistentException("Binary association has " + association.getRoles().size()  + " roles.");
		
		Set<Role> roles = association.getRoles();
		
		for(Role role:roles)
			if(!role.equals(ownRole)){
				return role;
			}
				
		
		return null;
	}
	
	/**
	 * Returns the counter roles of an nnary association.
	 * @param association - The association.
	 * @param ownRole - The own role.
	 * @return Set of counter roles. 
	 */
	public static Set<Role> getCounterRoles(Association association, Role ownRole){
		
		Set<Role> result = new HashSet<Role>();
		
		Set<Role> roles = association.getRoles();
		
		for(Role role:roles)
			if(!role.equals(ownRole))
				result.add(role);

		return result;
	}
	
}
