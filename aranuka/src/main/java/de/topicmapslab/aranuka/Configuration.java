package de.topicmapslab.aranuka;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.topicmapslab.aranuka.engine.IEngineDriver;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapException;
import de.topicmapslab.aranuka.utils.TopicMapsUtils;

/**
 * Configuration class for Aranuka.
 * @author Christian haß
 *
 */
public class Configuration {

	/**
	 * Set of annoteted classes which should be supported by the session.
	 */
	private Set<Class<?>> classes;
	
	/**
	 * Map of defined prefixes.
	 */
	private Map<String, String> prefixMap;
	
	/**
	 * Map of topic names. Key is the identifier while the value is the name.
	 */
	private Map<String,String> nameMap;
	
	/**
	 * The actual session.
	 */
	private Session session;

	private IEngineDriver driver;
	
	/**
	 * Constructor.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Configuration(Class<?> driverClass){
		
		if(driverClass == null)
			throw new RuntimeException("Driver specification must not be null.");
				
		Object obj = null;
		
		try{
		
			obj = driverClass.getConstructor().newInstance();

		}catch(Exception e){
			throw new RuntimeException("Can't instanciate driver.", e);
		}
		
		if(!(obj instanceof IEngineDriver))
			throw new RuntimeException("");
		
		this.driver = (IEngineDriver)obj;

	}
	
	public void setProperty(String key, String value){
		
		this.driver.setProperty(key, value);
		
	}
	
	/// TODO find a way to avoid public
 	IEngineDriver getDriver(){
		
		return this.driver;
		
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

	public void addName(String identifier, String name){
		
		if(this.nameMap == null)
			this.nameMap = new HashMap<String, String>();
		
		this.nameMap.put(identifier, name);
	}
	
	public String getName(String identifier){
		
		if(this.nameMap == null)
			return null;
		
		String name = this.nameMap.get(identifier);
		
		if(name != null)
			return name;
		
		if(this.getPrefixMap() == null)
			return null;
		
		// try to find the name in unresolved identifier
		
		for(Map.Entry<String,String> entry:this.nameMap.entrySet()){
			
			if(TopicMapsUtils.resolveURI(entry.getKey(),this.getPrefixMap()).equals(identifier)){
				return entry.getValue();
			}
		}
		
		return null;
		
	}
	
	public void setNameMap(Map<String, String> nameMap) {
	
		this.nameMap = nameMap;
	}
	
	
	
}
