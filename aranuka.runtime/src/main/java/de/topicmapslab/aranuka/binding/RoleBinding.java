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

import java.util.Map;

/**
 * Role field binding class.
 */
public class RoleBinding extends AbstractFieldBinding {

	/**
	 * The subject identifier of the role type as string.
	 */
	private String roleType;
	/**
	 * The topic binding of the player.
	 */
	private TopicBinding playerBinding;

	/**
	 * Constructor. 
	 * @param prefixMap - The prefix map.
	 * @param parent - The association container binding to which this role belongs.
	 */
	public RoleBinding(Map<String,String> prefixMap, AssociationContainerBinding parent) {
		super(prefixMap, parent);
	}

	/**
	 * Returns the subject identifier of the role type.
	 * @return - The identifier as string.
	 */
	public String getRoleType() {
		return roleType;
	}

	/**
	 * Sets the subject identifier of the role type.
	 * @param roleTypeIdentifier - The identifier as string.
	 */
	public void setRoleType(String roleTypeIdentifier) {
		this.roleType = roleTypeIdentifier;
	}

	/**
	 * Returns the binding of the player.
	 * @return - The binding.
	 */
	public TopicBinding getPlayerBinding() {
		return playerBinding;
	}

	/**
	 * Sets the binding of the player.
	 * @param playerBinding - The binding.
	 */
	public void setPlayerBinding(TopicBinding playerBinding) {
		this.playerBinding = playerBinding;
	}

}
