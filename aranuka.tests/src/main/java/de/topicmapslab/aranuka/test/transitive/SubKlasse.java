package de.topicmapslab.aranuka.test.transitive;

import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;

@Topic(subject_identifier="http://test.de/subtyp")
public class SubKlasse extends SuperKlasse {

	@Name(type="http://test.de/name")
	private String name;
	
	public SubKlasse() {
	}
	
	public SubKlasse(String name) {
		this.name = name;
	}
	
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SubKlasse [id=" + getId() + ", name=" + name + "]";
	}
	
	
}
