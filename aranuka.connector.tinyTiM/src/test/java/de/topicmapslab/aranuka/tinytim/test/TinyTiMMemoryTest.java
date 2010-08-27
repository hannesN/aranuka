/**
 * 
 */
package de.topicmapslab.aranuka.tinytim.test;

import java.util.Properties;

import org.junit.BeforeClass;

import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.test.AranukaTestSuite;

/**
 * @author Hannes Niederhausen
 *
 */
public class TinyTiMMemoryTest extends AranukaTestSuite {

	@BeforeClass
	public static void setUp() {
		Properties properties = new Properties();
		properties.setProperty(IProperties.CONNECTOR_CLASS, "de.topicmapslab.aranuka.tinytim.connectors.TinyTiMConnector");
		properties.setProperty(IProperties.BACKEND, "memory");
		setProperties(properties);
	}
	
}
