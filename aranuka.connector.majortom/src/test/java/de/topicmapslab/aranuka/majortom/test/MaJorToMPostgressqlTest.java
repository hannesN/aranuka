/**
 * 
 */
package de.topicmapslab.aranuka.majortom.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.BeforeClass;

import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.majortom.connector.MaJorToMEngineConnector;
import de.topicmapslab.aranuka.test.AranukaTestSuite;

/**
 * @author Hannes Niederhausen
 *
 */
public class MaJorToMPostgressqlTest extends AranukaTestSuite {

	@BeforeClass
	public static void setUp() throws IOException {
		Properties properties = new Properties();
		
		// try to load properties
		ClassLoader cl = MaJorToMPostgressqlTest.class.getClassLoader();
		InputStream is = cl.getResourceAsStream("db.properties");
		if (is==null)
			throw new IOException("Missing \"db.properties\"");
		
		properties.load(is);
		properties.setProperty(IProperties.CONNECTOR_CLASS, MaJorToMEngineConnector.class.getName());
		properties.setProperty(IProperties.BACKEND, "db");
		setProperties(properties);
	}
}
