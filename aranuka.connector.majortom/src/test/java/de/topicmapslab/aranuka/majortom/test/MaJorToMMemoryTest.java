/**
 * 
 */
package de.topicmapslab.aranuka.majortom.test;

import java.util.Properties;

import org.junit.BeforeClass;

import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.majortom.connector.MaJorToMEngineConnector;
import de.topicmapslab.aranuka.test.AranukaTestSuite;

/**
 * @author Hannes Niederhausen
 *
 */
public class MaJorToMMemoryTest extends AranukaTestSuite {

	@BeforeClass
	public static void setUp() {
		Properties properties = new Properties();
		properties.setProperty(IProperties.DISABLE_HISTORY, "true");
		properties.setProperty(IProperties.CONNECTOR_CLASS, MaJorToMEngineConnector.class.getName());
		properties.setProperty(IProperties.BACKEND, "memory");
		setProperties(properties);
	}
}
