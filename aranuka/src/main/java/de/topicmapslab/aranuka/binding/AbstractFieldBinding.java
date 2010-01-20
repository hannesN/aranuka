package de.topicmapslab.aranuka.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.utils.TopicMapsUtils;

public abstract class AbstractFieldBinding{

	private static Logger logger = LoggerFactory.getLogger(AbstractFieldBinding.class);

	private final AbstractClassBinding parent;
	
	private Method getter; // get method of the field
	private Method setter; // set method of the field
		
	private boolean isArray; 
	private boolean isCollection;
	
	private Set<String> themes; // scope
	
	private Map<String,String> prefixMap;
	
	public AbstractFieldBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		this.prefixMap = prefixMap;
		if(this.prefixMap == null)
			this.prefixMap = new HashMap<String, String>(); // create empty map to avoid exception, but should never be empty
		this.parent = parent;
	}

	public void setGetter(Method getter) {
		this.getter = getter;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
	}

	public AbstractClassBinding getParent() {
		return parent;
	}
	
	public Object getValue(Object obj) {
		
		try {
			
			return getter.invoke(obj);
			
		} catch (Exception e) {
			
			logger.error("Could not invoke accessor", e);
			throw new RuntimeException(e);
		}
	}
	
	public void setValue(Object value, Object obj) {
		
		try {
			
			setter.invoke(obj, value);
			
		} catch (Exception e) {
			
			logger.error("Could not invoke mutator", e);
			throw new RuntimeException(e);
		}
	}
	
	public Type getFieldType() {
		
		return getter.getGenericReturnType();
	}
	
	public boolean isArray() {
		
		return isArray;
	}
	
	public void setArray(boolean isArray) {
		
		//logger.info(this.getClass() + " set array to " + isArray);
		
		this.isArray = isArray;
	}
	
	public void setCollection(boolean isCollection) {
		
		//logger.info(this.getClass() + " set collection to " + isCollection);
		this.isCollection = isCollection;
	}
	
	public boolean isCollection() {
		
		return isCollection;
	}

	public Set<String> getThemes() {
		if (themes==null)
			return Collections.emptySet();
		return themes;
	}

	public void setThemes(Set<String> themes) {
		if (this.themes==null)
			this.themes = new HashSet<String>();
		this.themes = themes;
	}
	
	public Set<Topic> getScope(TopicMap topicMap){
		
		if(this.themes == null)
			return Collections.emptySet();
		
		Set<Topic> scope = new HashSet<Topic>();
		
		for(String theme:themes)
		{
			scope.add(topicMap.createTopicBySubjectIdentifier(topicMap.createLocator(TopicMapsUtils.resolveURI(theme, this.prefixMap))));
		}
		
		return scope;
	}

	public Map<String, String> getPrefixMap() {
		return prefixMap;
	}

	@Override
	public String toString() {
		return "AbstractFieldBinding [getter=" + getter + ", isArray="
				+ isArray + ", isCollection=" + isCollection + ", parent="
				+ ", setter=" + setter + ", themes=" + themes + "]";
	}

}
