package de.topicmapslab.aranuka.binding;

import java.util.Map;

import org.tmapi.core.Topic;


public class RoleBinding extends AbstractFieldBinding {
	
	private String roleType;
	
	public RoleBinding(AssociationContainerBinding parent) {
		super(parent);
	}
	
	public void persist(Topic topic, Object topicObject, Map<String,String> prefixMap){
		
	}
	
	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleTypeIdentifier) {
		this.roleType = roleTypeIdentifier;
	}

	@Override
	public String toString() {
		return "RoleBinding [roleType=" + roleType + "]";
	}

	
}
