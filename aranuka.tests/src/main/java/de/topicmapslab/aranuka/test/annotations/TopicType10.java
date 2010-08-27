package de.topicmapslab.aranuka.test.annotations;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic_type", name="Topic Type")
public class TopicType10 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;
		
	// name
	@Name(type="test:test_name")
	private String testName1;
	
	@Name(type="test:test_name")
	private String testName2;

	public String getStringSubjectIdentifier() {
		return stringSubjectIdentifier;
	}

	public void setStringSubjectIdentifier(String stringSubjectIdentifier) {
		this.stringSubjectIdentifier = stringSubjectIdentifier;
	}

	public String getTestName1() {
		return testName1;
	}

	public void setTestName1(String testName1) {
		this.testName1 = testName1;
	}

	public String getTestName2() {
		return testName2;
	}

	public void setTestName2(String testName2) {
		this.testName2 = testName2;
	}
	
	
	
}
