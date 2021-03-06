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
package de.topicmapslab.aranuka.persist;

import java.text.MessageFormat;

/**
 * Interface which contains constants for the tmql qeuries used.
 * 
 * @author Hannes Niederhausen
 * 
 */
public interface ITMQLQueries {

	/**
	 * String using params for {@link MessageFormat}.
	 * 
	 * number of params: 4
	 * <ol>
	 * <li>identifier of player topic</li>
	 * <li>identifier of the association type</li>
	 * <li>identifier of the role type</li>
	 * <li>filter for scope or empty string. Should have the form: @http://scope.de/1 AND @http://scope.de/2</li>
	 * </ol>
	 */
	public final String GET_UNARY_ASSOCIATION = "for $t in {0} RETURN '{' for $a IN {1} ( {2} : $t ) RETURN $a {3}  '}'";

	/** String using params for {@link MessageFormat}.
	 * 
	 * number of params: 3
	 * <ol>
	 * <li>identifier of the association type without brackets</li>
	 * <li>identifier of the role type without brackets</li>
	 * <li>identifier of player topic, which should have the CTM form &lt;iri&gt; </li>
	 * <li>filter for scope or empty string. Should have the form: @&lt;http://scope.de/1&gt; , &lt;http://scope.de/2&gt; </li>
	 * </ol>
	 */
	public final String INSERT_UNARY_ASSOCIATION = "INSERT '''''' <{0}> ( <{1}> : {2} ) {3} '''''' ";
	
	/**
	 * Deletes a topic of the given type
	 * params: 1
	 * <ol>
	 * <li>identifier of the topic</li>
	 * </ol>
	 */
	public final String DELETE_TOPIC = "DELETE CASCADE {0}";
	
	/**
	 * String using params for {@link MessageFormat}.
	 * 
	 * number of params: 6
	 * <ol>
	 * <li>identifier of player topic</li>
	 * <li>identifier of the other player topic type</li>
	 * <li>identifier of the association type</li>
	 * <li>identifier of the role type</li>
	 * <li>identifier of the other role type</li>
	 * <li>filter for scope or empty string. Should have the form: [ @http://scope.de/1 AND @http://scope.de/2 ]</li>
	 * </ol>
	 */
	public final String GET_ID_OF_BINARY_ASSOCIATION = "for $t1 in {0} for $t2 in {1} RETURN '{' for $a IN {2} ( {3} : $t1 , {4} : $t2 ) WHERE $a {5} RETURN $a >> id  '}'";

	/** String using params for {@link MessageFormat}.
	 * 
	 * number of params: 7
	 * <ol>
	 * <li>identifier of the association type without brackets</li>
	 * <li>identifier of the role type without brackets</li>
	 * <li>identifier of player topic, which should have the CTM form &lt;iri&gt; </li>
	 * <li>identifier of the other role type without brackets</li>
	 * <li>identifier of other player topic, which should have the CTM form &lt;iri&gt; </li>
	 * <li>filter for scope or empty string. Should have the form: @&lt;http://scope.de/1&gt; , &lt;http://scope.de/2&gt; </li>
	 * <li>the type of the other player topic </li>
	 * </ol>
	 */
	public final String INSERT_BINARY_ASSOCIATION = "INSERT '''''' <{0}> ( <{1}> : {2} , <{3}> : {4} ) {5} \n {4} isa <{6}> . '''''' ";

	
	/**
	 * String using params for {@link MessageFormat}.
	 * 
	 * number of params: 2
	 * <ol>
	 * <li>identifier of the association type without brackets</li>
	 * <li>values of non to remove associations in the form: MINUS "id" << id UNION "id2" << id </li>
	 * <li>identifier of the player type to persist</li>
	 * </ol> 
	 * 
	 */
	public final String DELETE_BINARY_ASSOCIATION = "DELETE {0} >> typed [. >> players == {2} ] {1}";
	
	/** String using params for {@link MessageFormat}.
	 * 
	 * number of params: 5
	 * <ol>
	 * <li>identifier of the association type without brackets</li>
	 * <li>identifier of the role type without brackets</li>
	 * <li>identifier of player topic, which should have the CTM form &lt;iri&gt; </li>
	 * <li>identifier of the other role type without brackets</li>
	 * <li>filter for scope or empty string. Should have the form: @&lt;http://scope.de/1&gt; , &lt;http://scope.de/2&gt; </li>
	 * </ol>
	 */
	public final String GET_ALL_BINARY_ASSOCIATIONS = "SELECT $t WHERE {0} ( {1} : {2} , {3} : $t ) {4}";
}
