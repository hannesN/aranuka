package de.topicmapslab.aranuka.test.annotations;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:counter_player_01", name="Counter Player 01")
public class CounterPlayer01 {

	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String subjectIdentifier;

	public String getSubjectIdentifier() {
		return subjectIdentifier;
	}

	public void setSubjectIdentifier(String subjectIdentifier) {
		this.subjectIdentifier = subjectIdentifier;
	}
	
	
	
}
