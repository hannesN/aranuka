/**
 * 
 */
package de.topicmapslab.aranuka.test.performance.model;

import de.topicmapslab.aranuka.annotations.Topic;

/**
 * @author Hannes Niederhausen
 *
 */
@Topic(subject_identifier="http://test.de/person")
public class Person {
	
	private String subjectIdentifier;
	
	private String name;
	
	private int age;

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
