package de.topicmapslab.aranuka.utils;

import java.lang.reflect.Field;
import java.util.Map;

public class TopicMapsUtils {

	public static String generateSubjectIdentifier(Class<?> clazz){

		StringBuilder builder = new StringBuilder();
		String nameSuffix = clazz.getName().replaceAll("\\.", "/");

		builder.append("base_locator:");
		
		builder.append(nameSuffix);
		return builder.toString();
	}
	
	public static String generateSubjectIdentifier(Field field){
		
		String identifier = generateSubjectIdentifier(field.getDeclaringClass());
		
		identifier+= "/" + field.getName();
		return identifier;
	}
	
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
	
}
