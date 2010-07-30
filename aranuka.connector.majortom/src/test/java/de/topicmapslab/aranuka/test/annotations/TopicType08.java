package de.topicmapslab.aranuka.test.annotations;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic_type", name="Topic Type")
public class TopicType08 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;

	public String getStringSubjectIdentifier() {
		return stringSubjectIdentifier;
	}
	
}
