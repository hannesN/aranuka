package de.topicmapslab.aranuka.binding;

public class NameBinding extends AbstractFieldBinding {

	private String nameType;
	
	public NameBinding(TopicBinding parent) {
		super(parent);
	}
	
	public String getNameType() {
		return nameType;
	}

	public void setNameType(String nameIdentifier) {
		this.nameType = nameIdentifier;
	}

	@Override
	public String toString() {
		return "NameBinding [nameType=" + nameType + "] " + super.toString();
	}
	
}


