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
/**
 * 
 */
package de.topicmapslab.aranuka.constants;

/**
 * This interface contains a base locator which should be used to deserialize XTM2.0 .
 * <p>
 * All id attributes will be transformed into an item identifier while reading XTM2.0 . 
 * Aranuka needs to distinguish these identififers to ignore them when setting the ids to an
 * object. To accomplish this aranuka checks if the item identifier starts with ITEM_IDENTIFIER_PREFIX.
 * </p>
 * <p>
 * It is advised to use {@link IAranukaIRIs#ITEM_IDENTIFIER_PREFIX} as base locator for the topic maps reader.
 * </p>
 * @author Hannes Niederhausen
 *
 */
public interface IAranukaIRIs {
	public final static String ITEM_IDENTIFIER_PREFIX = "http://aranujka.topicmapslab.de/itemidentifier/";
}
