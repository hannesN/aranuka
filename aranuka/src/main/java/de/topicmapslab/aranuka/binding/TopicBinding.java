package de.topicmapslab.aranuka.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.topicmapslab.aranuka.utils.HashUtil;

public class TopicBinding extends AbstractClassBinding{

	private String name; // name of the topic type
	private Set<String> identifiers; // identifier of the topic type
	private TopicBinding parent; // super type

	private List<AbstractFieldBinding> fieldBindings; // topic field bindings

	public List<AbstractFieldBinding> getFieldBindings() {
		
		if (this.fieldBindings==null)
			return Collections.emptyList();
		return this.fieldBindings;
	}
	
	public void addFieldBinding(AbstractFieldBinding fieldBinding) {
		
		if (this.fieldBindings==null)
			this.fieldBindings = new ArrayList<AbstractFieldBinding>();
		
		this.fieldBindings.add(fieldBinding);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setParent(TopicBinding parent) {
		this.parent = parent;
	}
	
	public TopicBinding getParent(){
		return this.parent;
	}
	
	public void addIdentifier(String id) {
		if (identifiers==null)
			identifiers = HashUtil.createSet();
		identifiers.add(id);
	}
	
	public Set<String> getIdentifier(){
		
		if(this.identifiers == null)
			return Collections.emptySet();
		return this.identifiers;
		
	}

	@Override
	public String toString() {
		return "TopicBinding [fieldBindings=" + fieldBindings
				+ ", identifiers=" + identifiers + ", name=" + name
				+ ", parent=" + parent + "]";
	}
}
