package de.topicmapslab.aranuka;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;

public class Configuration {

	private final static String DEFAULT_BASEL_LOCATOR = "http://www.topicmapslab.de/aranuka/";
	
	private static Logger logger = LoggerFactory.getLogger(Configuration.class);

	private Set<Class<?>> classes;
	private Map<String, String> prefixMap;
	
	private Map<Property, String> propertyMap;
	private Session session;
	
	// constructor

	public Configuration() {
		
		// set default values
		setBaseLocator(DEFAULT_BASEL_LOCATOR);
	}
	
	// add a class
	public void addClass(Class<?> clazz) {
		if (classes == null) {
			classes = new HashSet<Class<?>>();
		}
		classes.add(clazz);
	}
	
	/// TODO add all classes of an specific package

	public Set<Class<?>> getClasses(){
		return classes;
	}
	
	
	
	// properties
	
	public void setBaseLocator(String baseLocator){
		
		logger.info("Set base locator to " + baseLocator);
		propertyMap.put(Property.BASE_LOCATOR, baseLocator);
		// set prefix as well
		addPrefix("base_locator", baseLocator);
	}
	
	public String getBaseLocator(){
		return propertyMap.get(Property.BASE_LOCATOR);
	}

	// prefix
	
	public void addPrefix(String prefix, String uri) {
		if (prefixMap == null)
			prefixMap = new HashMap<String, String>();
		if (!(uri.endsWith("/")) || (uri.endsWith("#")))
			uri+="/";
		prefixMap.put(prefix, uri);
	}
	
	public Map<String, String> getPrefixMap() {
		if (prefixMap == null)
			return Collections.emptyMap();
		return prefixMap;
	}
	
	// session
	
	public Session getSession() throws BadAnnotationException, ClassNotSpecifiedException {
		
		/// TODO implement
		if(session != null)
			return session;

		session = new Session(this, false);

		return session;
		
	}
	
}
