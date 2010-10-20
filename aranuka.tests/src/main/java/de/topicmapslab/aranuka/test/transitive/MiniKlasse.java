package de.topicmapslab.aranuka.test.transitive;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="http://test.de/miniclass")
public class MiniKlasse extends SubKlasse {

	@Id(autogenerate=true, type=IdType.SUBJECT_IDENTIFIER)
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
