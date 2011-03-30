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
package de.topicmapslab.aranuka.exception;

/**
 * 
 * The exception to encapsulate other exceptions
 * 
 * @author Hannes Niederhausen
 *
 */
public class AranukaException extends Exception {
	private static final long serialVersionUID = -885912261015606243L;

	public AranukaException() {
		super();
	}

	public AranukaException(String message, Throwable cause) {
		super(message, cause);
	}

	public AranukaException(String message) {
		super(message);
	}

	public AranukaException(Throwable cause) {
		super(cause);
	}

	
	
}
