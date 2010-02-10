package de.topicmapslab.aranuka.binding;

import java.util.Map;

/**
 * Name field binding class.
 * @author Christian Haß
 *
 */
public class NameBinding extends AbstractFieldBinding {

	/**
	 * The subject identifier of the name type.
	 */
	private String nameType;
	
	/**
	 * Constructor.
	 * @param prefixMap - The prefix map.
	 * @param parent - The topic binding to which the name belongs.
	 */
	public NameBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}

	/**
	 * Returns the name type subject identifier as string.
	 * @return The identifier.
	 */
	public String getNameType() {
		return nameType;
	}

	/**
	 * Sets the name type subject identifier as string.
	 * @param nameIdentifier - The identifier.
	 */
	public void setNameType(String nameIdentifier) {
		this.nameType = nameIdentifier;
	}
	
	@Override
	public String toString() {
		return "NameBinding [nameType=" + nameType + "]";
	}
}


