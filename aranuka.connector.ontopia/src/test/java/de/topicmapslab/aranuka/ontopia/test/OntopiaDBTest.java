/**
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
 */
package de.topicmapslab.aranuka.ontopia.test;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.h2.tools.DeleteDbFiles;
import org.h2.util.ScriptReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.ontopia.connectors.OntopiaConnector;
import de.topicmapslab.aranuka.test.AranukaTestSuite;

/**
 * @author Hannes Niederhausen
 *
 */
public class OntopiaDBTest extends AranukaTestSuite {

	@BeforeClass
	public static void setUp() throws IOException {
		Properties properties = new Properties();
		
		// try to load properties
		ClassLoader cl = OntopiaDBTest.class.getClassLoader();
		InputStream is = cl.getResourceAsStream("db.properties");
		if (is==null)
			throw new IOException("Missing \"db.properties\"");
		
		properties.load(is);
		properties.setProperty(IProperties.CONNECTOR_CLASS, OntopiaConnector.class.getName());
		properties.setProperty(IProperties.BACKEND, "db");
		setProperties(properties);
		
		// Initializing the database for the h2 test
		String jdbcString = "jdbc:h2:"+properties.getProperty(IProperties.DATABASE_HOST)+";MVCC=true";
		
		
		try {
			Class.forName("org.h2.Driver");
			
			Connection conn = DriverManager.getConnection(jdbcString, "sa", "");

			is = conn.getClass().getClassLoader().getResourceAsStream("h2.create.sql");
			
			ScriptReader reader = new ScriptReader(new InputStreamReader(is));
			Statement stm = conn.createStatement();
			String sql = reader.readStatement();
			while (sql!=null) {
				stm.execute(sql);
				sql = reader.readStatement();
			}
			
			conn.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@AfterClass
	public static void dropDB() {
		try {
			// Initializing the database for the h2 test
			String db = getProperties().getProperty(IProperties.DATABASE_HOST);
			
			String dbDir = db.substring(0, db.lastIndexOf('/'));
			String dbname = db.substring(db.lastIndexOf('/')+1);
			
			DeleteDbFiles.execute(dbDir, dbname, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
}
