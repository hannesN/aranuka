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

import de.topicmapslab.aranuka.enummerations.IdType;

/**
 * Identifier field binding class.
 */
public class IdBinding extends AbstractFieldBinding {

	/**
	 * The type if the identifier.
	 */
	private IdType idtype;
	
	private boolean autogenerate;
	
	/**
	 * Constructor.
	 * @param prefixMap - The prefix map.
	 * @param parent - The topic binding to which the id belongs.
	 */
	public IdBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
		autogenerate = false; // default
	}

	/**
	 * Sets the id type.
	 * @param idtype - The id type.
	 */
	public void setIdtype(IdType idtype) {
		this.idtype = idtype;
	}

	/**
	 * Returns the id type.
	 * @return The id type
	 */
	public IdType getIdtype() {
	
		return idtype;
	}

	/**
	 * Sets the autogenerate flag
	 * 
	 * @param autogenerate the new flag value
	 */
	public void setAutogenerate(boolean autogenerate) {
	
		this.autogenerate = autogenerate;
	}
	
	/**
	 * Returns the state of the autogenerate flag
	 * @return 
	 */
	public boolean isAutogenerate() {
		return autogenerate;
	}
	
}
