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
import de.topicmapslab.aranuka.exception.TopicMapException;


/**
 * Configuration class for Aranuka.
 * @author christian haß
 *
 */
public class Configuration {

	/**
	 * The dafault base locator of the topic map if not overwritten by the user.
	 */
	private final static String DEFAULT_BASEL_LOCATOR = "http://www.topicmapslab.de/aranuka/";
		
	private static Logger logger = LoggerFactory.getLogger(Configuration.class);

	/**
	 * Set of annoteted classes which should be supported by the session.
	 */
	private Set<Class<?>> classes;
	/**
	 * Map of defined prefixes.
	 */
	private Map<String, String> prefixMap;
	
	/**
	 * Map of property strings.
	 */
	private Map<Property, String> propertyMap;
	
	/**
	 * The actual session.
	 */
	private Session session;
	

	/**
	 * Constructor.
	 */
	public Configuration() {
		
		// set default values
		setBaseLocator(DEFAULT_BASEL_LOCATOR);
	}
	
	/**
	 * Adds a class.
	 * @param clazz - The class object.
	 */
	public void addClass(Class<?> clazz) {
		if (classes == null) {
			classes = new HashSet<Class<?>>();
		}
		classes.add(clazz);
	}
	
	/**
	 * Returns all configured classes.
	 * @return Set of classes.
	 */
	public Set<Class<?>> getClasses(){
		return classes;
	}

	
	/**
	 * Sets the base locator which is used in the topic map.
	 * Note: Canging the base locator after a session was created has no consequence.
	 * @param baseLocator - The base locator.
	 */
	public void setBaseLocator(String baseLocator){
		
		logger.info("Set base locator to " + baseLocator);
		
		if(propertyMap == null)
			propertyMap = new HashMap<Property, String>();
		
		propertyMap.put(Property.BASE_LOCATOR, baseLocator);
		// set prefix as well
		addPrefix("base_locator", baseLocator);
	}
	
	/**
	 * Returns the currently specified base locator.
	 * @return - The base locator as string.
	 */
	public String getBaseLocator(){
		return propertyMap.get(Property.BASE_LOCATOR);
	}

	/**
	 * Adds a new prefix to the configuration.
	 * If a prefix is used in the annotation is must be added to configuration, otherwise it will not be poissible to resolve the uri.
	 * @param prefix - The predix string.
	 * @param uri - The represented uri.
	 */
	public void addPrefix(String prefix, String uri) {
		if (prefixMap == null)
			prefixMap = new HashMap<String, String>();
		
		if (!(uri.endsWith("/")) || (uri.endsWith("#")))
			uri+="/";
		
		prefixMap.put(prefix, uri);
	}
	
	/**
	 * Returns the prefix map.
	 * @return - The prefix map.
	 */
	public Map<String, String> getPrefixMap() {
		
		if (prefixMap == null)
			return Collections.emptyMap();
		
		return prefixMap;
	}
	
	/**
	 * Returns a specific property string.
	 * @param key - The key for the property.
	 * @return The property.
	 */
	public String getProperty(Property key){
		
		if(propertyMap == null)
			return null;
		
		return propertyMap.get(key);
	}

	/**
	 * Adds a new property to the configuration.
	 * @param key - The key for the property.
	 * @param value - The value of the property.
	 */
	public void addProperty(Property key, String value) {
		if(this.propertyMap == null)
			propertyMap = new HashMap<Property, String>();
		
		propertyMap.put(key, value);
		
	}
	
	/**
	 * Returns the session. If no session exist, a new one will be created.
	 * @param lasyBinding - Flag specifiing whether lazy binding should be used or not. If not, all bindings for the configured classes will be created while the session is generated.
	 * @return The sesson.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapException
	 */
	public Session getSession(boolean lasyBinding) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapException{
		
		/// TODO implement
		if(session != null)
			return session;

		/// TODO check if it is problematic to change the configuration after the session is already use since it gets a copy!
		session = new Session(this, lasyBinding);

		return session;
		
	}

	
}
