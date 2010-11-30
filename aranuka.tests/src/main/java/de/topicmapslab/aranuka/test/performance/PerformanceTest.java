/**
 * 
 */
package de.topicmapslab.aranuka.test.performance;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.test.AbstractTest;
import de.topicmapslab.aranuka.test.performance.model.Person;

/**
 * @author Hannes Niederhausen
 *
 */
public class PerformanceTest extends AbstractTest {

private Session session;
	
	/**
	 * Setup for test
	 * @throws AranukaException
	 */
	@Before
	public void setup() throws AranukaException {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());

		conf.addPackage("de.topicmapslab.aranuka.test.performance.model");
		conf.setProperty(IProperties.BASE_LOCATOR,
				"http://test.aranuka.de/testcase_delete");
		conf.addPrefix("tp", "http://test.de/");

		session = conf.getSession(false);
		
		assertNotNull(session);
	}

	/**
	 * Cleaning the topic map after testing
	 */
	@After
	public void tearDown() {
		session.clearTopicMap();
	}
	
	
	
	private List<Person> createModel() {
		List<Person> list = new ArrayList<Person>();
		for (int i=0; i<1000; i++) {
			list.add(createPerson(i));
		}
		return list;
	}

	private Person createPerson(int i) {
		Person p = new Person();
		p.setSubjectIdentifier("http://test.de/persons/"+i);
		p.setName("Hans Meyer "+i);
		p.setAge(i*30%5);
		
		return p;
	}
}
