/*******************************************************************************
 * Copyright 2010-2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.aranuka.utils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.exception.TopicMapInconsistentException;

/**
 * Utils class for the work with the topic map.
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

		builder.append("class:");
		
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
	
	/**
	 * Returns a tmql query part which returns the given topic.
	 * 
	 * @param t
	 *            the topic which identifier should be used
	 * @return a string containing the identifier string
	 */
	public static String getTMQLIdentifierString(Topic t) {
		Set<Locator> siSet = t.getSubjectIdentifiers();
		if (!siSet.isEmpty())
			return "\"" + siSet.iterator().next().toExternalForm() + "\" << indicators";

		Set<Locator> slSet = t.getSubjectLocators();
		if (!slSet.isEmpty())
			return "\"" + slSet.iterator().next().toExternalForm() + "\" << locators";

		Set<Locator> iiSet = t.getItemIdentifiers();
		if (!iiSet.isEmpty())
			return "\"" + iiSet.iterator().next().toExternalForm() + "\" << item";

		throw new IllegalArgumentException("The given topic has no identifier!");
	}
	
}
