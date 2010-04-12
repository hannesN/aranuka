/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
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