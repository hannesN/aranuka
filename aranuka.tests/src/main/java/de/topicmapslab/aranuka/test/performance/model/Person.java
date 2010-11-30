/**
 * 
 */
package de.topicmapslab.aranuka.test.performance.model;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

/**
 * @author Hannes Niederhausen
 *
 */
@Topic(subject_identifier="http://test.de/person")
public class Person {
	
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String subjectIdentifier;
	
	@Name(type="http://test.de/person/name")
	private String name;
	
	@Occurrence(type="http://test.de/person/age")
	private int age;

	@Association(type="http://test.de/person/lives", played_role="http://test.de/person/habitant", 
			other_role="http://test.de/person/habitat")
	private Address address;
	
	public String getSubjectIdentifier() {
		return subjectIdentifier;
	}

	public void setSubjectIdentifier(String subjectIdentifier) {
		this.subjectIdentifier = subjectIdentifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((subjectIdentifier == null) ? 0 : subjectIdentifier
						.hashCode());
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
		if (subjectIdentifier == null) {
			if (other.subjectIdentifier != null)
				return false;
		} else if (!subjectIdentifier.equals(other.subjectIdentifier))
			return false;
		return true;
	}
	
	

}
