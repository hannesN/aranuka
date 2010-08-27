package de.topicmapslab.aranuka.test.annotations;

import java.util.Set;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Role;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic_type", name="Topic Type")
public class TopicType15 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;

	
	// association container
	@AssociationContainer
	private class Container{
		
		@Role(type="test:counter_player_1_role")
		private Set<CounterPlayer01> counter1;
		
		private CounterPlayer02 counter2;

		@SuppressWarnings("unused")
		public Set<CounterPlayer01> getCounter1() {
			return counter1;
		}

		@SuppressWarnings("unused")
		public void setCounter1(Set<CounterPlayer01> counter1) {
			this.counter1 = counter1;
		}

		@SuppressWarnings("unused")
		public CounterPlayer02 getCounter2() {
			return counter2;
		}

		@SuppressWarnings("unused")
		public void setCounter2(CounterPlayer02 counter2) {
			this.counter2 = counter2;
		}

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
