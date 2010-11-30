package de.topicmapslab.aranuka.test.transitive;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="http://test.de/supertyp")
public class SuperKlasse {

	@Id(autogenerate=true, type=IdType.ITEM_IDENTIFIER)
	private String id;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
}
