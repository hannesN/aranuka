package de.topicmapslab.aranuka.binding;

import java.util.Map;


public class NameBinding extends AbstractFieldBinding {

	private String nameType;
	
	// --[ public methods ]--------------------------------------------------------------------------------
	
	public NameBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}

	public String getNameType() {
		return nameType;
	}

	public void setNameType(String nameIdentifier) {
		this.nameType = nameIdentifier;
	}
	
	@Override
	public String toString() {
		return "NameBinding [nameType=" + nameType + "]";
	}
}


