/**
 * 
 */
package de.topicmapslab.aranuka.test.associations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import de.topicmapslab.aranuka.test.associations.model.Gun;
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
	
	/**
	 * Checking if deletion of associtaion only effects the wanted objects
	 * @throws AranukaException 
	 */
	@Test
	public void spyTest() throws AranukaException {
		Person spy1 = new Person();
		spy1.setName("James Bond");
		spy1.setSubjectIdentifier("http://test.de/spy/james_bond");
		spy1.setSpy(true);
		session.persist(spy1);

		// checking mr bond
		Person p = (Person) session.getBySubjectIdentifier(spy1.getSubjectIdentifier());
		assertEquals("James Bond", p.getName());
		assertTrue(p.isSpy());
		
		Person spy2 = new Person();
		spy2.setName("Maxwell Smart");
		spy2.setSubjectIdentifier("http://test.de/spy/maxwell_smart");
		spy2.setSpy(true);
		session.persist(spy2);
		
		p = (Person) session.getBySubjectIdentifier(spy2.getSubjectIdentifier());
		assertEquals(spy2.getName(), p.getName());
		assertTrue(p.isSpy());
		
		p.setSpy(false);
		session.persist(p);
		
		p = (Person) session.getBySubjectIdentifier(spy2.getSubjectIdentifier());
		assertEquals(spy2.getName(), p.getName());
		assertFalse(p.isSpy());

		
		p = (Person) session.getBySubjectIdentifier(spy1.getSubjectIdentifier());
		assertEquals("James Bond", p.getName());
		assertTrue(p.isSpy());
	}
	
	@Test
	public void weaponTest() throws Exception {
		Gun walther = new Gun();
		walther.setName("Walther PPK");
		walther.setSubjectIdentifier("http://en.wikipedia.org/wiki/Walther_ppk");
		
		Gun desertEagle = new Gun();
		desertEagle.setName("Desert Eagle");
		desertEagle.setSubjectIdentifier("http://en.wikipedia.org/wiki/IMI_Desert_Eagle");
		
		Person bond = new Person();
		bond.setName("James Bond");
		bond.setSpy(true);
		bond.setSubjectIdentifier("http://en.wikipedia.org/wiki/James_Bond");
		bond.setGun(walther);
		session.persist(bond);
		
		serializeTopicMap("/tmp/bond.ctm", session.getTopicMap());
		
		// checking bond
		Person p = (Person) session.getBySubjectIdentifier(bond.getSubjectIdentifier());
		assertEquals(bond.getName(), p.getName());
		assertEquals(bond.getGun(), p.getGun());
		assertEquals(bond.isSpy(), p.isSpy());
		
		Person tony = new Person();
		tony.setName("Bullet Tooth Tony");
		tony.setSubjectIdentifier("http://en.wikipedia.org/wiki/Bullet_tooth_tony");
		tony.setGun(desertEagle);
		tony.setSpy(false);
		session.persist(tony);
			
		// checking tony
		p = (Person) session.getBySubjectIdentifier(tony.getSubjectIdentifier());
		assertEquals(tony.getName(), p.getName());
		assertEquals(tony.getGun(), p.getGun());
		assertEquals(tony.isSpy(), p.isSpy());
		
		// rechecking bond
		p = (Person) session.getBySubjectIdentifier(bond.getSubjectIdentifier());
		assertEquals(bond.getName(), p.getName());
		assertEquals(bond.getGun(), p.getGun());
		assertEquals(bond.isSpy(), p.isSpy());
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
