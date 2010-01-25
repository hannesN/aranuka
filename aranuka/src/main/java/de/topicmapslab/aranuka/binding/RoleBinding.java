package de.topicmapslab.aranuka.binding;

import java.util.Map;


public class RoleBinding extends AbstractFieldBinding {

	private String roleType;
	private TopicBinding playerBinding;

	public RoleBinding(Map<String,String> prefixMap, AssociationContainerBinding parent) {
		super(prefixMap, parent);
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleTypeIdentifier) {
		this.roleType = roleTypeIdentifier;
	}

	public TopicBinding getPlayerBinding() {
		return playerBinding;
	}

	public void setPlayerBinding(TopicBinding playerBinding) {
		this.playerBinding = playerBinding;
	}
	
	@Override
	public String toString() {
		return "RoleBinding [playerBinding=" + playerBinding + ", roleType="
				+ roleType + "]";
	}
}
