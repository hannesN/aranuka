package de.topicmapslab.aranuka.test.count;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.test.AbstractTest;
import de.topicmapslab.aranuka.test.count.model.Address;
import de.topicmapslab.aranuka.test.count.model.Person;

public class CountTest extends AbstractTest {

	private static final String PERSON_ID = "http://psi.mustermanns.de/persons/";
	private Session session;

	@Before
	public void setup() throws AranukaException {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());

		conf.addPackage("de.topicmapslab.aranuka.test.count.model");
		conf.setProperty(IProperties.BASE_LOCATOR, "http://test.aranuka.de/testcase_count");
		conf.addPrefix("tp", "http://test.de/");

		session = conf.getSession(false);
		
		assertNotNull(session);
		
		
	}

	@After
	public void tearDown() {
		if (session!=null)
			session.clearTopicMap();
	}

	@Test
	public void testPersonCount20() throws Exception {
		assertEquals(0, session.count(Person.class));
		
		int count = 20;
		for (int i=0; i<count; i++) {
			persistPerson("Hans "+i+" Meier", 20+i);
		}
		
		assertEquals(count, session.count(Person.class));
	}
	
	@Test
	public void testPersonCount50() throws Exception {
		assertEquals(0, session.count(Person.class));
		
		int count = 20;
		for (int i=0; i<count; i++) {
			persistPerson("Hans "+i+" Meier", 20+i);
		}
		
		assertEquals(count, session.count(Person.class));
	}

	@Test
	public void testAddressCount20() throws Exception {
		assertEquals(0, session.count(Address.class));
		
		int count = 20;
		for (int i=0; i<count; i++) {
			persistAddress("Muster Street "+i);
		}
		
		assertEquals(count, session.count(Address.class));
	}
	
	@Test
	public void testAddressCount50() throws Exception {
		assertEquals(0, session.count(Address.class));
		
		int count = 50;
		for (int i=0; i<count; i++) {
			persistAddress("Muster Street "+i);
		}
		
		assertEquals(count, session.count(Address.class));
	}
	
	@Test
	public void testMixedCount20() throws Exception {
		assertEquals(0, session.count(Address.class));
		assertEquals(0, session.count(Person.class));
		
		int count = 20;
		for (int i=0; i<count; i++) {
			persistAddress("Muster Street "+i);
			persistPerson("Hans "+i+" Meier", 20+i);
		}
		
		assertEquals(count, session.count(Address.class));
		assertEquals(count, session.count(Person.class));
	}
	
	@Test
	public void testMixedCount50() throws Exception {
		assertEquals(0, session.count(Address.class));
		assertEquals(0, session.count(Person.class));
		
		int count = 50;
		for (int i=0; i<count; i++) {
			persistAddress("Muster Street "+i);
			persistPerson("Hans "+i+" Meier", 20+i);
		}
		
		assertEquals(count, session.count(Address.class));
		assertEquals(count, session.count(Person.class));
	}
	
	
	private void persistPerson(String name, int age) throws Exception {
		Person person = new Person();
		person.setAge(age);
		person.setVita("This is a vita.\nI don't know what to write.");
		person.setName(name);
		person.setId(PERSON_ID+name.toLowerCase().replaceAll(" ", "_"));
		session.persist(person);
	}

	private void persistAddress(String name) throws Exception {
		Address a = new Address(); 
		a.setStreet(name);
		session.persist(a);
	}
}
