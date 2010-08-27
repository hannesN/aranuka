package de.topicmapslab.aranuka.ontopia.test;
/**
 * 
 */


import java.util.Properties;

import org.junit.BeforeClass;

import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.ontopia.connectors.OntopiaConnector;
import de.topicmapslab.aranuka.test.AranukaTestSuite;

/**
 * @author Hannes Niederhausen
 *
 */
public class OntopiaMemoryTest extends AranukaTestSuite {

	@BeforeClass
	public static void setUp() {
		Properties properties = new Properties();
		properties.setProperty(IProperties.CONNECTOR_CLASS, OntopiaConnector.class.getName());
		properties.setProperty(IProperties.BACKEND, "memory");
		setProperties(properties);
	}
}
