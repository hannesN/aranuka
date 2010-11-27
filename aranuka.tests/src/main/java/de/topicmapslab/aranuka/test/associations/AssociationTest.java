/**
 * 
 */
package de.topicmapslab.aranuka.test.associations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.test.AbstractTest;
import de.topicmapslab.aranuka.test.associations.model.Address;
import de.topicmapslab.aranuka.test.associations.model.Date;
import de.topicmapslab.aranuka.test.associations.model.Person;
import de.topicmapslab.aranuka.test.associations.model.Person.Lives;

/**
 * 
 * Test for nnary associations
 * 
 * @author Hannes Niederhausen
 *
 */
public class AssociationTest extends AbstractTest {
	
	private Session session;
	
	/**
	 * Setup for test
	 * @throws AranukaException
	 */
	@Before
	public void setup() throws AranukaException {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());

		conf.addPackage("de.topicmapslab.aranuka.test.associations.model");
		conf.setProperty(IProperties.BASE_LOCATOR,
				"http://test.aranuka.de/testcase_delete");
		conf.addPrefix("tp", "http://test.de/");

		session = conf.getSession(false);
		
		assertNotNull(session);
	}

	/**
	 * Cleaning the topic map aftet testing
	 */
	@After
	public void tearDown() {
		session.clearTopicMap();
	}
	
	@Test
	public void modification() throws AranukaException, IOException {
		// creating the topics
		Date d1 = new Date();
		d1.setContent("now");
		d1.setItemIdentifier("http://test.de/d/1");
		session.persist(d1);
		
		Date d2 = new Date();
		d2.setContent("later");
		d2.setItemIdentifier("http://test.de/d/2");
		session.persist(d2);
		
		Address a = new Address();
		a.setItemIdentifier("http://test.de/a/1");
		a.setContent("Semsame Stree 3");
		session.persist(a);
		
		Person p = new Person();
		p.setSubjectIdentifier("http://test.de/p/1");
		p.setName("Hans Meyer");
		
		Lives  l = new Lives();
		l.setAdress(a);
		l.setDate(d1);
		p.setLives(l);
		
		session.persist(p);
		
		
		Person p2 = (Person) session.getBySubjectIdentifier(p.getSubjectIdentifier());
		serializeTopicMap("/tmp/test.ctm", session.getTopicMap());
		checkNewPerson(p, p2);
		
		p.getLives().setDate(d2);
		session.persist(p);

		serializeTopicMap("/tmp/test2.ctm", session.getTopicMap());
		
		p2 = (Person) session.getBySubjectIdentifier(p.getSubjectIdentifier());
		checkNewPerson(p, p2);
		
		
		
	}
	
	private void checkNewPerson(Person expected, Person actual) {
		assertEquals(expected.getSubjectIdentifier(), actual.getSubjectIdentifier());
		assertEquals(expected.getName(), actual.getName());
		
		// checking container has instances
		assertNotNull(actual.getLives());
		assertNotNull(actual.getLives().getAdress());
		assertNotNull(actual.getLives().getDate());
		
		// check if we get what we expect
			// date
		Date eDate = expected.getLives().getDate();
		Date aDate = actual.getLives().getDate();
		
		assertEquals(eDate.getItemIdentifier(), aDate.getItemIdentifier());
		assertEquals(eDate.getContent(), aDate.getContent());
		
			// addresses
		Address eAddress = expected.getLives().getAdress();
		Address aAddress = actual.getLives().getAdress();
		
		assertEquals(eAddress.getContent(), aAddress.getContent());
		assertEquals(eAddress.getItemIdentifier(), aAddress.getItemIdentifier());
	}
}
