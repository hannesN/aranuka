package de.topicmapslab.aranuka.codegen.core.util;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;

/**
 * 
 * @author Sven Krosse
 *
 */
public class TypeUtility {

	public static String getJavaName(final Topic topic)
			throws POJOGenerationException {
		return getJavaName(getLocator(topic));
	}

	private static final String getJavaName(Locator locator) {
		String reference = locator.getReference();
		int index = reference.lastIndexOf("/");
		if (index != -1) {
			reference = reference.substring(index+1);
		}

		return getJavaName(reference);
	}

	public static String getJavaName(String name) {
	    StringBuilder builder = new StringBuilder();
		char lastChar = name.charAt(0);
		builder.append(new String(lastChar + "").toUpperCase());
		for (int i = 1; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c != '-' && c != '_' && c != ':') {
				if ('-' == lastChar || '_' == lastChar || ':' == lastChar) {
					builder.append(new String(c + "").toUpperCase());
				} else {
					builder.append(c);
				}
			}
			lastChar = c;
		}

		return builder.toString();
    }

	/**
	 * Creates an attribute name based on a type
	 * @param topic
	 * @return
	 * @throws POJOGenerationException
	 */
	public static String getTypeAttribute(final Topic topic)
			throws POJOGenerationException {
		String name = getTypeName(topic); 
		return Character.toLowerCase(name.charAt(0))+name.substring(1);
	}

	public static final String getTypeAttribute(Locator locator) {
		String reference = locator.getReference();
		int index = reference.lastIndexOf("/");
		if (index != -1) {
			reference = reference.substring(index+1);
		}
		if ( reference.equalsIgnoreCase("topic-name")){
			reference = "tm:name";
		}
		
		String tmp = getJavaName(reference);
		tmp = Character.toLowerCase(tmp.charAt(0))+tmp.substring(1);
		return tmp;
	}

	public static final Locator getLocator(Topic topic)
			throws POJOGenerationException {
		

		if (!topic.getSubjectIdentifiers().isEmpty()) {
			for (Locator locator : topic.getSubjectIdentifiers()) {
				if (locator.getReference().contains("tinytim")) {
					continue;
				}
				return locator;
			}
		} 
		
		if (!topic.getSubjectLocators().isEmpty()) {
			for (Locator locator : topic.getSubjectLocators()) {
				if (locator.getReference().contains("tinytim")) {
					continue;
				}
				return locator;
			}
		}
		
		if (!topic.getItemIdentifiers().isEmpty()) {
			for (Locator locator : topic.getItemIdentifiers()) {
				if (locator.getReference().contains("tinytim")) {
					continue;
				}
				return locator;
			}

		} 
		throw new POJOGenerationException("Topic with id "+topic.getId()+"has no identifier");
	}
	
	private static String getTopicName(Topic t) {
		for (Name name : t.getNames()) {
			return name.getValue();
		}
		return null;
	}

	public static String getTypeName(Topic t) {
		try {
			if (t==null)
				return null;
			
			String name = getTopicName(t);
			if (name == null)
				return TypeUtility.getJavaName(t);

			StringBuilder builder = new StringBuilder();
			char lastChar = name.charAt(0);
			builder.append(Character.toUpperCase(lastChar));
			for (int i = 1; i < name.length(); i++) {
				char c = name.charAt(i);
				if (c != '-' && c != '_' && c != ':') {
					if ('-' == lastChar || '_' == lastChar || ':' == lastChar
							|| ' ' == lastChar) {
						builder.append(Character.toUpperCase(c));
					} else if (c != ' ') {
						builder.append(c);
					}
				}
				lastChar = c;
			}
			return builder.toString();
		} catch (POJOGenerationException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final Map<String, Class<?>> xsdToJavaMappings = new HashMap<String, Class<?>>();
	static {
		xsdToJavaMappings.put("xsd:string", String.class);
		xsdToJavaMappings.put("xsd:integer", Integer.class);
		xsdToJavaMappings.put("xsd:int", Integer.class);
		xsdToJavaMappings.put("xsd:float", Float.class);
		xsdToJavaMappings.put("xsd:date", Date.class);
		xsdToJavaMappings.put("xsd:double", Double.class);
		xsdToJavaMappings.put("xsd:iri", URI.class);
		xsdToJavaMappings.put("xsd:boolean", Boolean.class);
		xsdToJavaMappings.put("xsd:decimal", Integer.class);
	}

	public static Class<?> toJavaType(final Locator datatype) {
		return toJavaType(datatype.getReference());
	}

	public static Class<?> toJavaType(final String datatype) {
		if (xsdToJavaMappings.containsKey(datatype)) {
			return xsdToJavaMappings.get(datatype);
		}
		return String.class;
	}

	public static String field2Method(String fieldName) {
		StringBuilder b = new StringBuilder();
		b.append(Character.toUpperCase(fieldName.charAt(0)));
		b.append(fieldName.substring(1));
		
		return b.toString();
	}
	
}
