package de.topicmapslab.aranuka.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractFieldBinding {

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
	
	public void setGetter(Method getter) {
		this.getter = getter;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
	}

	protected AbstractBinding getParent() {
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
		
		this.isArray = isArray;
	}
	
	public void setCollection(boolean isCollection) {
		
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

	@Override
	public String toString() {
		return "AbstractFieldBinding [getter=" + getter + ", isArray="
				+ isArray + ", isCollection=" + isCollection + ", parent="
				+ ", setter=" + setter + ", themes=" + themes + "]";
	}

}
