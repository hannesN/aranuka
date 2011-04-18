package de.topicmapslab.aranuka.test.rewrite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.test.AbstractTest;
import de.topicmapslab.aranuka.test.rewrite.model.Person;

public class RewriteTest extends AbstractTest {

	/**
	 * 
	 */
	private static final String PERSON_ID = "http://psi.mustermanns.de/persons/hans";
	private Session session;
	private Person person;

	@Before
	public void setup() throws AranukaException {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());

		conf.addPackage("de.topicmapslab.aranuka.test.rewrite.model");
		conf.setProperty(IProperties.BASE_LOCATOR, "http://test.aranuka.de/testcase_delete");
		conf.addPrefix("tp", "http://test.de/");

		session = conf.getSession(false);
		
		assertNotNull(session);
		
		person = new Person();
		person.setAge(23);
		person.setVita("This is a vita.\nI don't know what to write.");
		person.setName("Hans Mustermann");
		person.setId(PERSON_ID);
		session.persist(person);
		session.clearCache();
	}

	@After
	public void tearDown() {
		if (session!=null)
			session.clearTopicMap();
	}

	@Test
	public void testPerson() throws Exception {
		Person p = session.getBySubjectIdentifier(PERSON_ID);
		
		assertNotNull(p);
		
		// should be a proxy
		assertFalse(p.getClass()==Person.class);
		
		assertTrue(p instanceof Person);

		assertEquals(person.getAge(), p.getAge());
		assertEquals(person.getName(), p.getName());
		assertEquals(person.getId(), p.getId());
		assertEquals(person.getVita(), p.getVita());
		
		p.setAge(29);
		
		session.persist(p);
		
		session.clearCache();
		
		// test  new values
		p = session.getBySubjectIdentifier(PERSON_ID);
		
		assertNotNull(p);
		
		// should be a proxy
		assertFalse(p.getClass()==Person.class);
		
		assertTrue(p instanceof Person);

		assertNotSame(person.getAge(), p.getAge());
		assertEquals(person.getName(), p.getName());
		assertEquals(person.getId(), p.getId());
		assertEquals(person.getVita(), p.getVita());
		assertEquals(29, p.getAge());
	}

	

}
