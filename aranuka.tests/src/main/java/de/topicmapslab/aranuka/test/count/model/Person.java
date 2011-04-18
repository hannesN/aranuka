package de.topicmapslab.aranuka.test.count.model;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Scope;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="http://test.de/person")
public class Person {
	
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String id;
	
	@Name
	private String name;

	@Occurrence
	private int age;
	
	@Occurrence
	@Scope(themes="http://de.wikipedia.de/deutsch")
	private String vita;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
		
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getVita() {
		return vita;
	}

	public void setVita(String vita) {
		this.vita = vita;
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
		if (!getClass().isAssignableFrom(obj.getClass()))
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
