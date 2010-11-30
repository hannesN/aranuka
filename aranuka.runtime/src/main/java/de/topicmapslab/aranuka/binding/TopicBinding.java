/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Binding class for a topic type.
 */
public class TopicBinding extends AbstractClassBinding{

	/**
	 * The optional name of the topic type, used for display means.
	 */
	private String name;
	/**
	 * Set if subject identifiers which identify this topic type.
	 */
	private Set<String> identifiers;
	/**
	 * Binding of the parent class.
	 */
	private TopicBinding parent;
	
	/**
	 * Bindings of children.
	 */
	private List<TopicBinding> children;

	/**
	 * List of field bindings which belong to this type class.
	 */
	private List<AbstractFieldBinding> fieldBindings;

	/**
	 * Returns the list of field bindings.
	 * @return - List of field bindings.
	 */
	public List<AbstractFieldBinding> getFieldBindings() {
		
		if (this.fieldBindings==null)
			return Collections.emptyList();
		return this.fieldBindings;
	}
	
	/**
	 * Adds a field binding to the topic type class.
	 * @param fieldBinding - The field binding.
	 */
	public void addFieldBinding(AbstractFieldBinding fieldBinding) {
		
		if (this.fieldBindings==null)
			this.fieldBindings = new ArrayList<AbstractFieldBinding>();
		
		this.fieldBindings.add(fieldBinding);
	}
	
	/**
	 * Sets the name of the topic type.
	 * @param name - The name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of the topic type.
	 * @return - The name.
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Sets the parent binding.
	 * @param parent - The binding.
	 */
	public void setParent(TopicBinding parent) {
		if (this.parent!=null)
			this.parent.removeChild(this);
		
		this.parent = parent;
		
		if (this.parent!=null)
			this.parent.addChild(this);
	}
	
	/**
	 * Returns the parent binding or null if not existing.
	 * @return - The binding.
	 */
	public TopicBinding getParent(){
		return this.parent;
	}
	
	/**
	 * Returns the children bindigns, which means subclasses/subtypes.
	 * @return
	 */
	public List<TopicBinding> getChildren() {
		if (children==null)
			return Collections.emptyList();
		return children;
	}
	
	/**
	 * Adds a child to the binding.
	 * @param binding
	 */
	public void addChild(TopicBinding binding) {
		if(children==null)
			children=new ArrayList<TopicBinding>();
		children.add(binding);
	}
	
	/**
	 * Removes a child of the binding.
	 * 
	 * @param binding
	 */
	public void removeChild(TopicBinding binding) {
		if(children!=null)
			children.remove(binding);
	}
	
	/**
	 * Adds an subject identifier for the topic type.
	 * @param id - The identifier as string.
	 */
	public void addIdentifier(String id) {
		if (identifiers==null)
			identifiers = new HashSet<String>();
		identifiers.add(id);
	}
	
	/**
	 * Returns the set of subject identifiers as a set of strings.
	 * @return - The set.
	 */
	public Set<String> getIdentifier(){
		
		if(this.identifiers == null)
			return Collections.emptySet();
		return this.identifiers;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicBinding other = (TopicBinding) obj;
		if (identifiers == null) {
			if (other.identifiers != null)
				return false;
		} else if (!identifiers.equals(other.identifiers))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}
	
	
}
