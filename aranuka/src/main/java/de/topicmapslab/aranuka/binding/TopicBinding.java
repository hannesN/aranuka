package de.topicmapslab.aranuka.binding;

import java.util.List;
import java.util.Set;

import de.topicmapslab.aranuka.utils.HashUtil;

public class TopicBinding {

	private String name;
	private Set<String> identifiers;
	private TopicBinding parent; // supertype
	
//	private List<IdBinding> ids;
//	private List<NameBinding> names;
//	private List<OccurrenceBinding> occurrences;
//	private List<AssociationBinding> associations;
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setParent(TopicBinding parent) {
		this.parent = parent;
	}
	
	public void addIdentifier(String id) {
		if (identifiers==null)
			identifiers = HashUtil.createSet();
		identifiers.add(id);
	}
}
