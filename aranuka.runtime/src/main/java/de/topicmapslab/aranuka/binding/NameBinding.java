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
 * Name field binding class.
 */
public class NameBinding extends AbstractFieldBinding {

	/**
	 * The subject identifier of the name type.
	 */
	private String nameType;
	
	/**
	 * Constructor.
	 * @param prefixMap - The prefix map.
	 * @param parent - The topic binding to which the name belongs.
	 */
	public NameBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}

	/**
	 * Returns the name type subject identifier as string.
	 * @return The identifier.
	 */
	public String getNameType() {
		return nameType;
	}

	/**
	 * Sets the name type subject identifier as string.
	 * @param nameIdentifier - The identifier.
	 */
	public void setNameType(String nameIdentifier) {
		this.nameType = nameIdentifier;
	}

}


