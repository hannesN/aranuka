/**
 * 
 */
package de.topicmapslab.aranuka.test.write;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

/**
 * Testclass to test id generation
 * 
 * @author Hannes
 *
 */
@Topic(subject_identifier="http://test.de/person")
public class Person {

	@Id(type=IdType.ITEM_IDENTIFIER, autogenerate=true)
	private String ii;
	
	@Id(type=IdType.SUBJECT_LOCATOR, autogenerate=true)
	private String sl;
	
	@Id(type=IdType.SUBJECT_IDENTIFIER, autogenerate=true)
	private String si;
	
	@Name
	private String name;

	public String getIi() {
		return ii;
	}

	public void setIi(String ii) {
		this.ii = ii;
	}

	public String getSl() {
		return sl;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public String getSi() {
		return si;
	}

	public void setSi(String si) {
		this.si = si;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
