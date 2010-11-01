/**
 * 
 */
package de.topicmapslab.aranuka.test.supertypes;

import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;

/**
 * @author bosso
 *
 */
@Topic(subject_identifier="http://test.de/lamp")
public class Lamp extends Thing {

	@Name
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
