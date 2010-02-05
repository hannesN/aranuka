package de.topicmapslab.aranuka.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Binding of an association container class.
 * @author christian haß
 *
 */
public class AssociationContainerBinding extends AbstractClassBinding {

	/**
	 * Association container binding of the parent class
	 */
	private AssociationContainerBinding parent;
	/**
	 * List of role bindings for the fields
	 */
	private List<RoleBinding> roles;

	/**
	 * Sets the parent binding.
	 * @param parent - The parent binding.
	 */
	public void setParent(AssociationContainerBinding parent) {
		this.parent = parent;
	}
	
	/**
	 * Returns the parent binding or null if not existing.
	 * @return - The parent binding.
	 */
	public AssociationContainerBinding getParent() {
		return parent;
	}

	/**
	 * Adds a new role binding to the list.
	 * @param rb - The role binding.
	 */
	public void addRoleBinding(RoleBinding rb) {
		if (roles==null)
			roles = new ArrayList<RoleBinding>();
		roles.add(rb);
	}
	
	/**
	 * Returns the list of role bindings or an empty list if non exist.
	 * @return - The list of role bindings.
	 */
	public List<RoleBinding> getRoleBindings() {
		if (roles==null)
			return Collections.emptyList();
		return roles;
	}
	
}
