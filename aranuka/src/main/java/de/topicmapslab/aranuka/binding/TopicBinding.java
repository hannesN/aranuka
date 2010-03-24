package de.topicmapslab.aranuka.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Binding class for a topic type.
 * @author Christian Haß
 *
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
		this.parent = parent;
	}
	
	/**
	 * Returns the parent binding or null if not existing.
	 * @return - The binding.
	 */
	public TopicBinding getParent(){
		return this.parent;
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
}
