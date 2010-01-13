package de.topicmapslab.aranuka.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssociationContainerBinding extends AbstractBinding {

	private AssociationContainerBinding parent; // supertype
	
	private List<RoleBinding> roles;
	
	
//	public AssociationContainerBinding(String baseLocator) {
//		super(baseLocator);
//	}
	
	public void setParent(AssociationContainerBinding parent) {
		this.parent = parent;
	}
	
	// role bindings
	
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

	@Override
	public String toString() {
		return "AssociationContainerBinding [parent=" + parent + ", roles="
				+ roles + "]";
	}
	
	
}
