package de.topicmapslab.aranuka.test.transitive;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="http://test.de/subtyp")
public class SubKlasse extends SuperKlasse {

	@Id(type=IdType.ITEM_IDENTIFIER)
	private String id;
	
	@Name(type="http://test.de/name")
	private String name;
	
	public SubKlasse() {
	}
	
	public SubKlasse(String name) {
		this.name = name;
	}
	
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SubKlasse [id=" + id + ", name=" + name + "]";
	}
	
	
}
