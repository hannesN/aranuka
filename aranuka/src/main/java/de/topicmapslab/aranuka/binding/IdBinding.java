package de.topicmapslab.aranuka.binding;

import de.topicmapslab.aranuka.annotations.IDTYPE;

public class IdBinding extends AbstractFieldBinding {

	private IDTYPE idtype;
	
	public IdBinding(TopicBinding parent) {
		super(parent);
	}
	
	public void setIdtype(IDTYPE idtype) {
		this.idtype = idtype;
	}
	
	public IDTYPE getIdtype() {
		return idtype;
	}	
}
