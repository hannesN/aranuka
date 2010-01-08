package de.topicmapslab.aranuka.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.topicmapslab.aranuka.utils.HashUtil;

public class TopicBinding extends AbstractBinding{

	private String name;
	private Set<String> identifiers;
	private TopicBinding parent; // supertype
	
	private List<IdBinding> ids;
	private List<NameBinding> names;
	private List<OccurrenceBinding> occurrences;
	private List<AssociationBinding> associations;
	
	
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
	
	// id bindings
	
	public void addIdBinding(IdBinding ib) {
		if (ids==null)
			ids = new ArrayList<IdBinding>();
		ids.add(ib);
	}
	
	public List<IdBinding> getIdBindings() {
		if (ids==null)
			return Collections.emptyList();
		return ids;
	}
	
	// name bindings
	
	public void addNameBinding(NameBinding nb) {
		if (names==null)
			names = new ArrayList<NameBinding>();
		names.add(nb);
	}
	
	public List<NameBinding> getNameBindings() {
		if (names==null)
			return Collections.emptyList();
		return names;
	}
	
	// occurrence bindings
	
	public List<OccurrenceBinding> getOccurrencesBindings() {
		if (occurrences==null)
			return Collections.emptyList();
		return occurrences;
	}
	
	public void addOccurrenceBinding(OccurrenceBinding ob) {
		if (occurrences==null)
			occurrences = new ArrayList<OccurrenceBinding>();
		occurrences.add(ob);
	}
	
	// association bindings
	
	public List<AssociationBinding> getAssociationBindings() {
		if (associations==null)
			return Collections.emptyList();
		return associations;
	}
	
	public void addAssociationBinding(AssociationBinding ab) {
		if (associations==null)
			associations = new ArrayList<AssociationBinding>();
		associations.add(ab);
	}

	@Override
	public String toString() {
		return "TopicBinding [associations=" + associations + ", identifiers="
				+ identifiers + ", ids=" + ids + ", name=" + name + ", names="
				+ names + ", occurrences=" + occurrences + ", parent=" + parent
				+ "]";
	}
	
	
	
}
