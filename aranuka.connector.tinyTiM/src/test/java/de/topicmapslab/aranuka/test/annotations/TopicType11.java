package de.topicmapslab.aranuka.test.annotations;

import java.util.Set;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic_type", name="Topic Type")
public class TopicType11 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;

	// occurrence
	@Occurrence(type="test:test_occurrence")
	private String stringOccurrence;
	@Occurrence(type="test:test_occurrence")
	private Set<String> setSringOccurrence;
	
	
	public String getStringSubjectIdentifier() {
		return stringSubjectIdentifier;
	}
	public void setStringSubjectIdentifier(String stringSubjectIdentifier) {
		this.stringSubjectIdentifier = stringSubjectIdentifier;
	}
	public String getStringOccurrence() {
		return stringOccurrence;
	}
	public void setStringOccurrence(String stringOccurrence) {
		this.stringOccurrence = stringOccurrence;
	}
	public Set<String> getSetSringOccurrence() {
		return setSringOccurrence;
	}
	public void setSetSringOccurrence(Set<String> setSringOccurrence) {
		this.setSringOccurrence = setSringOccurrence;
	}
	
	
	
}
