package de.topicmapslab.aranuka.test.annotations;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic_type", name="Topic Type")
public class TopicType14 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;

	
	// association container
	@AssociationContainer
	private class Container{
		
	}
	
	// associations
	@Association(played_role="test:topic_type_01_role", type="test:nnary_association")
	private Container nnaryAssociation;

	public String getStringSubjectIdentifier() {
		return stringSubjectIdentifier;
	}

	public void setStringSubjectIdentifier(String stringSubjectIdentifier) {
		this.stringSubjectIdentifier = stringSubjectIdentifier;
	}

	public Container getNnaryAssociation() {
		return nnaryAssociation;
	}

	public void setNnaryAssociation(Container nnaryAssociation) {
		this.nnaryAssociation = nnaryAssociation;
	}
	
	
	
	
}
