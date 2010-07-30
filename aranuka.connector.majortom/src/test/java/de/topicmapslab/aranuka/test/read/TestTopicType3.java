package de.topicmapslab.aranuka.test.read;

import java.util.Set;

import org.junit.Ignore;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Ignore
@Topic(subject_identifier="test:test_topic_type", name="Test Topic Type")
public class TestTopicType3 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;
		
	// subject locator
	@Id(type=IdType.SUBJECT_LOCATOR)
	private Set<String> setSubjectLocator;
		
	// item identifier
	@Id(type=IdType.ITEM_IDENTIFIER)
	private String[] arrayItemIdentifier;

	public String getStringSubjectIdentifier() {
		return stringSubjectIdentifier;
	}

	public void setStringSubjectIdentifier(String stringSubjectIdentifier) {
		this.stringSubjectIdentifier = stringSubjectIdentifier;
	}

	public Set<String> getSetSubjectLocator() {
		return setSubjectLocator;
	}

	public void setSetSubjectLocator(Set<String> setSubjectLocator) {
		this.setSubjectLocator = setSubjectLocator;
	}

	public String[] getArrayItemIdentifier() {
		return arrayItemIdentifier;
	}

	public void setArrayItemIdentifier(String[] arrayItemIdentifier) {
		this.arrayItemIdentifier = arrayItemIdentifier;
	}
		
	
	
}
