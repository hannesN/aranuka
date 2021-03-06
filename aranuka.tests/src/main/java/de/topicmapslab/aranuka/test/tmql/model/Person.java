package de.topicmapslab.aranuka.test.tmql.model;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;


@Topic(subject_identifier="http://test.de/person")
public class Person {

	@Id(autogenerate=false, type=IdType.ITEM_IDENTIFIER)
	private String id;
	
	public Person() {
	
	}
	
	public Person(String name) {
		this.name = name;
	}
	
	
	
	public Person(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}



	@Name
	private String name;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Person other = (Person) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
