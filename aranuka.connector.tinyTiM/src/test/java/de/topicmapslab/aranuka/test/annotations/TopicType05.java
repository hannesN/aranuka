package de.topicmapslab.aranuka.test.annotations;

import java.util.Set;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic_type", name="Topic Type")
public class TopicType05 {

	// subject locator
	@Id(type=IdType.SUBJECT_LOCATOR)
	private Set<String> setSubjectLocator;
	@Id(type=IdType.SUBJECT_LOCATOR)
	private String stringSubjectLocator;
		


	public Set<String> getSetSubjectLocator() {
		return setSubjectLocator;
	}
	public void setSetSubjectLocator(Set<String> setSubjectLocator) {
		this.setSubjectLocator = setSubjectLocator;
	}
	
	public String getStringSubjectLocator() {
		return stringSubjectLocator;
	}
	public void setStringSubjectLocator(String stringSubjectLocator) {
		this.stringSubjectLocator = stringSubjectLocator;
	}
	
	
}
