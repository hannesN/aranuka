package de.topicmapslab.aranuka.binding;

import java.util.Map;

import org.tmapi.core.Topic;


public class RoleBinding extends AbstractFieldBinding {
	
	private String roleType;
	
	public RoleBinding(Map<String,String> prefixMap, AssociationContainerBinding parent) {
		super(prefixMap, parent);
	}
	
	public void persist(Topic topic, Object topicObject){
		
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
