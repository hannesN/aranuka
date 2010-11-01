/**
 * 
 */
package de.topicmapslab.aranuka.test.supertypes;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

/**
 * @author bosso
 *
 */
@Topic(subject_identifier="http://test.de/thing")
public class Thing {
	@Id(type=IdType.SUBJECT_IDENTIFIER)	
	private String id;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
}
