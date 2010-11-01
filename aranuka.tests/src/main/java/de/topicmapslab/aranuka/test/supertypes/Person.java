package de.topicmapslab.aranuka.test.supertypes;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="http://test.de/person")
public class Person {
	
	@Id(autogenerate=true, type=IdType.ITEM_IDENTIFIER)
	private String id;
	
	@Association(type="http://test.de/owns", played_role="http://test.de/owner", other_role="http://test.de/owned")
	private Thing thing;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Thing getThing() {
		return thing;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
	}
	
	
	

}
