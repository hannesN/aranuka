package de.topicmapslab.aranuka.binding;

import java.util.Map;

import de.topicmapslab.aranuka.enummerations.IdType;

public class IdBinding extends AbstractFieldBinding {

	private IdType idtype;
	
	// --[ public methods ]--------------------------------------------------------------------------------
	
	public IdBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}

	// getter and setter
	
	public void setIdtype(IdType idtype) {
		this.idtype = idtype;
	}
	
	public IdType getIdtype() {
		return idtype;
	}
	
	@Override
	public String toString() {
		return "IdBinding [idtype=" + idtype + "]";
	}
}
