/*******************************************************************************
 * Copyright 2010-2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.aranuka.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Binding of an association container class.
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
