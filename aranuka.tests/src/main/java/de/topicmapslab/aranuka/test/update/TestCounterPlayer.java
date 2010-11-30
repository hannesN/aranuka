package de.topicmapslab.aranuka.test.update;

import org.junit.Ignore;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Ignore
@Topic(subject_identifier="test:counter_player", name="Counter Player")
public class TestCounterPlayer {

	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String subjectIdentifier;

	public String getSubjectIdentifier() {
		return subjectIdentifier;
	}

	public void setSubjectIdentifier(String subjectIdentifier) {
		this.subjectIdentifier = subjectIdentifier;
	}
	
	
	
}
