/**
 * 
 */
package de.topicmapslab.aranuka.test.associations.model;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

/**
 * @author Hannes Niederhausen
 *
 */
@Topic(subject_identifier="http://test.de/gun")
public class Gun {

	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String subjectIdentifier;
	
	@Name(type="http://test.de/gun/name")
	private String name;

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
		Gun other = (Gun) obj;
		if (subjectIdentifier == null) {
			if (other.subjectIdentifier != null)
				return false;
		} else if (!subjectIdentifier.equals(other.subjectIdentifier))
			return false;
		return true;
	}
	
	
	
	
}
