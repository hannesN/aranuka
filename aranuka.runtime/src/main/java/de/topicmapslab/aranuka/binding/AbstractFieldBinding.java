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
package de.topicmapslab.aranuka.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for field bindings.
 */
public abstract class AbstractFieldBinding{

	private static Logger logger = LoggerFactory.getLogger(AbstractFieldBinding.class);

	/**
	 * Binding of the parent.
	 */
	private final AbstractClassBinding parent;
	
	/**
	 * The getter method of the field.
	 */
	private Method getter;
	/**
	 * The setter method of the field.
	 */
	private Method setter;
		
	/**
	 * Flag indicating whether the field type is an array or not.
	 */
	private boolean isArray;
	/**
	 * Flag indicating whether the field type is an collection or not.
	 */
	private boolean isCollection;
	
	/**
	 * The generic field type.
	 */
	private Type parameterisedType;
	
	/**
	 * The scope configured via the annotation.
	 */
	private Set<String> themes;
	
	/**
	 * The prefix map.
	 */
	private Map<String,String> prefixMap;
	
	/**
	 * Constructor
	 * @param prefixMap - The prefix map.
	 * @param parent - Binding of the owner of the field.
	 */
	public AbstractFieldBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		this.prefixMap = prefixMap;
		if(this.prefixMap == null)
			this.prefixMap = new HashMap<String, String>(); // create empty map to avoid exception, but should never be empty
		this.parent = parent;
	}

	/**
	 * Set the getter method
	 * @param getter - The getter method.
	 */
	public void setGetter(Method getter) {
		this.getter = getter;
	}
	
	/**
	 * @return the getter
	 */
	public Method getGetter() {
		return getter;
	}

	/**
	 * Sets the setter method
	 * @param setter - The method
	 */
	public void setSetter(Method setter) {
		this.setter = setter;
	}

	/**
	 * Returns the binding of the owner-
	 * @return The binding.
	 */
	public AbstractClassBinding getParent() {
		return parent;
	}
	
	/**
	 * Gets the value from the specific instance via the getter method.
	 * @param obj - The instance.
	 * @return - The value.
	 */
	public Object getValue(Object obj) {
		
		try {
			
			return getter.invoke(obj);
			
		} catch (Exception e) {
			
			logger.error("Could not invoke accessor", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Sets the value to a specific object via the setter method.
	 * @param value - The value.
	 * @param obj - The object.
	 */
	public void setValue(Object value, Object obj) {
		
		try {
			
			setter.invoke(obj, value);
			
		} catch (Exception e) {
			logger.error("Could not invoke mutator", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return the type of the field represented by the binding.
	 * @return The type.
	 */
	public Type getFieldType() {
		
		return getter.getGenericReturnType();
	}
	
	/**
	 * Returns whether the field type is an array or not.
	 * @return
	 */
	public boolean isArray() {
		
		return isArray;
	}
	
	/**
	 * Sets the array flag.
	 * @param isArray - The value.
	 */
	public void setArray(boolean isArray) {

		this.isArray = isArray;
	}
	
	/**
	 * Sets the collection flag.
	 * @param isCollection - The value.
	 */
	public void setCollection(boolean isCollection) {

		this.isCollection = isCollection;
	}
	
	/**
	 * Returns whether the field type is an collection or not.
	 * @return The value.
	 */
	public boolean isCollection() {
		
		return isCollection;
	}

	/**
	 * Returns the scope or an empty set if not specified.
	 * @return The set of themes representing the scope.
	 */
	public Set<String> getThemes() {
		if (themes==null)
			return Collections.emptySet();
		return themes;
	}

	/**
	 * Sets the scope.
	 * @param themes - Set of themes representing the scope.
	 */
	public void setThemes(Set<String> themes) {
		if (this.themes==null)
			this.themes = new HashSet<String>();
		this.themes = themes;
	}
	
	/**
	 * Returns the prefix map.
	 * @return The prefix map.
	 */
	public Map<String, String> getPrefixMap() {
		return prefixMap;
	}

	/**
	 * Returns the generic type, i.e. the parameter type if the field type is instance of ParameterizedType
	 * @return The type.
	 */
	public Type getParameterisedType() {
		return parameterisedType;
	}

	/**
	 * Sets the generic type.
	 * @param parameterisedType - The type.
	 */
	public void setParameterisedType(Type parameterisedType) {
		this.parameterisedType = parameterisedType;
	}

}
