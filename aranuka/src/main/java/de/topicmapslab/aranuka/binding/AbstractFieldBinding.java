package de.topicmapslab.aranuka.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.utils.TopicMapsUtils;

public abstract class AbstractFieldBinding {

	private static Logger logger = LoggerFactory.getLogger(AbstractFieldBinding.class);

	private final AbstractBinding parent;
	
	private Method getter; // get method of the field
	private Method setter; // set method of the field
		
	private boolean isArray; 
	private boolean isCollection;
	
	private List<String> themes;
	
	public AbstractFieldBinding(AbstractBinding parent) {
		this.parent = parent;
	}
	
	// abstract persist method
	public abstract void persist(Topic topic, Object object, Map<String,String> prefixMap) throws BadAnnotationException; 
	
	public void setGetter(Method getter) {
		this.getter = getter;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
	}

	public AbstractBinding getParent() {
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

	public List<String> getThemes() {
		if (themes==null)
			return Collections.emptyList();
		return themes;
	}

	public void setThemes(List<String> themes) {
		if (this.themes==null)
			this.themes = new ArrayList<String>();
		this.themes = themes;
	}
	
	public Collection<Topic> getScope(TopicMap topicMap, Map<String, String> prefixMap){
		
		if(this.themes == null)
			return Collections.emptyList();
		
		Collection<Topic> scope = new ArrayList<Topic>();
		
		for(String theme:themes)
		{
			scope.add(topicMap.createTopicBySubjectIdentifier(topicMap.createLocator(TopicMapsUtils.resolveURI(theme, prefixMap))));
		}
		
		return scope;
	}

	@Override
	public String toString() {
		return "AbstractFieldBinding [getter=" + getter + ", isArray="
				+ isArray + ", isCollection=" + isCollection + ", parent="
				+ ", setter=" + setter + ", themes=" + themes + "]";
	}

}
