package de.topicmapslab.aranuka.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssociationContainerBinding extends AbstractClassBinding {

	private AssociationContainerBinding parent; // super type
	private List<RoleBinding> roles; // list of related role bindings

	// getter and setter
	
	public void setParent(AssociationContainerBinding parent) {
		this.parent = parent;
	}
	
	public AssociationContainerBinding getParent() {
		return parent;
	}

	public void addRoleBinding(RoleBinding rb) {
		if (roles==null)
			roles = new ArrayList<RoleBinding>();
		roles.add(rb);
	}
	
	public List<RoleBinding> getRoleBindings() {
		if (roles==null)
			return Collections.emptyList();
		return roles;
	}
	
}
