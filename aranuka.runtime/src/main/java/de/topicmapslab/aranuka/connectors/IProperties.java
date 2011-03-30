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
package de.topicmapslab.aranuka.connectors;

/**
 * This interface contains the constants for common properties. 
 * A driver should use them before creating its own.
 */
public interface IProperties {

	/**
	 * filename used for memory backends. This filename will be the name of the persisted topic map file.
	 */
	public static final String FILENAME = "filename";
	
	/**
	 * The base locator of the topic map
	 */
	public static final String BASE_LOCATOR = "base_locator";
	
	/**
	 * Name of the database management system for JDBC connections. Values are:
	 * <ul>
	 * 	<li>mysql</li>
	 *  <li>postrgesql</li>
	 *  <li>h2</li> 
	 * </ul>
	 * 
	 */
	public static final String DATABASESYSTEM = "databasesystem";
	
	/**
	 * The host where the database is running or the path to the database for derby or h2
	 */
	public static final String DATABASE_HOST = "db_host";
	
	/**
	 * The port where the database is reachable
	 */
	public static final String DATABASE_PORT = "db_port";
	
	/**
	 * The name of the database or the path to the database for derby or h2
	 */
	public static final String DATABASE_NAME = "db_name";
	
	/**
	 * The login for the dbms
	 */
	public static final String DATABASE_LOGIN = "db_login";
	
	/**
	 * the password for the dbms
	 */
	public static final String DATABASE_PASSWORD = "db_password";
	
	/**
	 * Flag which indicates whether a file or a jdbc backend should be used.
	 * 
	 * Possible values are
	 * <ul>
	 * 	<li>memory</li>
	 *  <li>db</li>
	 * </ul>
	 */
	public static final String BACKEND = "backend";
	
	/**
	 * Field which specifies the qualified name of the connector class to use.
	 */
	public static final String CONNECTOR_CLASS = "connectorclass";

	/**
	 * Field to disable the history management. Currently only supported by MaJorToM
	 */
	public static final String DISABLE_HISTORY = "disable_history";
}
