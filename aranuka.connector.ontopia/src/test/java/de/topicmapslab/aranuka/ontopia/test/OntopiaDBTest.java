package de.topicmapslab.aranuka.ontopia.test;
/**
 * 
 */


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
			
			DeleteDbFiles.execute("/tmp", "aranukatest", true);
			
//			Class.forName("org.h2.Driver");
//			Connection conn = DriverManager.getConnection("jdbc:h2:/tmp/h2/test",
//					"sa", "");
//
//			InputStream is = conn.getClass().getClassLoader().getResourceAsStream("h2.drop.sql");
//			
//			ScriptReader reader = new ScriptReader(new InputStreamReader(is));
//			Statement stm = conn.createStatement();
//			String sql = reader.readStatement();
//			while (sql!=null) {
//				stm.execute(sql);
//				sql = reader.readStatement();
//			}
//			conn.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
}
