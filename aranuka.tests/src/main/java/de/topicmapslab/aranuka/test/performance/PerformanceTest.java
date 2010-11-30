/**
 * 
 */
package de.topicmapslab.aranuka.test.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.test.AbstractTest;
import de.topicmapslab.aranuka.test.performance.model.Address;
import de.topicmapslab.aranuka.test.performance.model.Person;

/**
 * @author Hannes Niederhausen
 * 
 */
@Ignore
public class PerformanceTest extends AbstractTest {

	private Session session;
	
	private List<Address> addresses = new ArrayList<Address>(20);

	/**
	 * Setup for test
	 * 
	 * @throws AranukaException
	 */
	@Before
	public void setup() throws AranukaException {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());

		conf.addPackage("de.topicmapslab.aranuka.test.performance.model");
		conf.setProperty(IProperties.BASE_LOCATOR,
				"http://test.aranuka.de/test_performance");
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

	@Test
	public void create1000Persons() throws Exception {
		for (Person p : createModel()) {
			session.persist(p);
		}

		session.clearCache();

		long start = System.currentTimeMillis();
		Set<Person> results = session.getAll(Person.class);

		System.out.println(System.currentTimeMillis() - start);

		assertEquals(1000, results.size());

	}
	@Test
	public void create1000PersonsWithAddress() throws Exception {
		for (int i=0; i<20; i++) {
			Address a = new Address();
			a.setItemIdentifier("http://test.d/a/"+i);
			a.setContent("Sesame Street 0"+i);
			addresses.add(a);
			session.persist(a);
		}
		
		for (Person p : createModelWithAddress()) {
			session.persist(p);
		}

		session.clearCache();

		long start = System.currentTimeMillis();
		Set<Person> results = session.getAll(Person.class);

		System.out.println(System.currentTimeMillis() - start);

		assertEquals(1000, results.size());

	}
	private List<Person> createModel() {
		List<Person> list = new ArrayList<Person>();
		for (int i = 0; i < 1000; i++) {
			list.add(createPerson(i));
		}
		return list;
	}

	private List<Person> createModelWithAddress() {
		List<Person> list = new ArrayList<Person>();
		for (int i = 0; i < 1000; i++) {
			Person p = createPerson(i);
			p.setAddress(addresses.get((int) (System.currentTimeMillis()%20)));
			list.add(p);
		}
		return list;
	}

	
	private Person createPerson(int i) {
		Person p = new Person();
		p.setSubjectIdentifier("http://test.de/persons/" + i);
		p.setName("Hans Meyer " + i);
		p.setAge(i * 30 % 5);

		return p;
	}
}
