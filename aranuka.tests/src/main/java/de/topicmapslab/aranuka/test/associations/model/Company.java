package de.topicmapslab.aranuka.test.associations.model;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

/**
 * Test Company with an address
 * 
 * @author Hannes Niederhausen
 * 
 */
@Topic(subject_identifier = "http://test.de/company")
public class Company {

	@Id(type = IdType.SUBJECT_IDENTIFIER)
	private String subjectIdentifier;

	@Association(persistOnCascade = true, type = "http://test.de/has_address", played_role = "http://test.de/place", other_role = "http://test.de/address")
	private Address address;

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param subjectIdentifier
	 *            the subjectIdentifier to set
	 */
	public void setSubjectIdentifier(String subjectIdentifier) {
		this.subjectIdentifier = subjectIdentifier;
	}

	/**
	 * @return the subjectIdentifier
	 */
	public String getSubjectIdentifier() {
		return subjectIdentifier;
	}

}
