package de.topicmapslab.aranuka.binding;

public class RoleBinding extends AbstractFieldBinding {
	
	private String roleType;

	public RoleBinding(AssociationContainerBinding parent) {
		super(parent);
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
