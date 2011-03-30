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

package de.topicmapslab.aranuka;

public interface IPersistListener {
	
	/**
	 * Notifies that the given model was persisted by the session.
	 * 
	 * @param model the persisted model
	 */
	public void persisted(Object model);
	
	/**
	 * Notifies that the given model was removed by the session.
	 * 
	 * @param model the removed model
	 */
	public void removed(Object model);
	
	
}
