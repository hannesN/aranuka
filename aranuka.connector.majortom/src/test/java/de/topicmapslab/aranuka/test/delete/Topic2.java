package de.topicmapslab.aranuka.test.delete;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic2")
public class Topic2 {

	@Id(type=IdType.ITEM_IDENTIFIER)
	private String id;
	
	@Association(played_role="test:role1", other_role="test:role2", type="test:assoc")
	private Topic1 topic1;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Topic1 getTopic1() {
		return topic1;
	}

	public void setTopic1(Topic1 topic1) {
		this.topic1 = topic1;
	}
	
	
}
