/**
 * 
 */
package de.topicmapslab.aranuka.test.preusker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmapi.core.Locator;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.test.AbstractTest;
import de.topicmapslab.aranuka.test.preusker.model.Brief;
import de.topicmapslab.aranuka.test.preusker.model.Brief.Adressat;
import de.topicmapslab.aranuka.test.preusker.model.Brief.Verfasser;
import de.topicmapslab.aranuka.test.preusker.model.Funktion;
import de.topicmapslab.aranuka.test.preusker.model.Person;
import de.topicmapslab.aranuka.test.preusker.model.Schlagwort;

/**
 * Test to debug error in the Preusker Genny App
 * 
 * @author Hannes Niederhausen
 *
 */
public class PreuskerAssociationTest extends AbstractTest {

	private Session session;
	private Brief letter;

	@Before
	public void setup() throws AranukaException {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());

		conf.setProperty(IProperties.BASE_LOCATOR,
				"http://test.aranuka.de/preuskertest");
		conf.addPrefix("tp", "http://test.de/");
		
		conf.addPackage("de.topicmapslab.aranuka.test.preusker.model");

		session = conf.getSession(false);

		assertNotNull(session);
		
		init();
	}
	
	/**
	 * Creates a letter and the needed attributes
	 * @throws Exception 
	 */
	private void init() throws AranukaException {
		
		Schlagwort word = new Schlagwort();
		word.setName("Uff!");
		session.persist(word);
		
		HashSet<Schlagwort> wordSet = new HashSet<Schlagwort>();
		wordSet.add(word);
		
		
		Funktion funk = new Funktion();
		funk.setName("None");
		session.persist(funk);
		
		Person p = new Person();
		p.setPersonenname("Hans Meyer");
		session.persist(p);
		
		Person p2 = new Person();
		p2.setPersonenname("Wiener Wurst");
		session.persist(p2);
		
		letter = new Brief();
		letter.setId("100");
		letter.setSchlagwortSet(wordSet);
		
		
		Brief.Verfasser author = new Verfasser();
		author.setEntity(p);
		author.setFunktion(funk);
		
		Brief.Adressat adressee = new Adressat();
		adressee.setEntity(p2);
		adressee.setFunktion(funk);
		
		letter.setAdressat(adressee);
		letter.setVerfasser(author);
		
		session.persist(letter);
	}

	@After
	public void tearDown() {
		session.clearTopicMap();
	}

	@Test
	public void testAssociationAfterChange() throws AranukaException {
		Person p2 = new Person();
		p2.setPersonenname("Hans Wurst");
		session.persist(p2);
		
		letter.getAdressat().setEntity(p2);
		session.persist(letter);
		
		testAssociation();
	}
	
	@Test
	public void testAssociation() {
		Topic letterTopic =  getTopic("http://psi.archaeologie.sachsen.de/preusker/Brief/100");
		assertNotNull(letterTopic);
		
		Set<Role> letterRoles = letterTopic.getRolesPlayed();
		assertEquals("Roles Check for Letter", 3, letterRoles.size());
		
		Topic roleTypeTopic =  getTopic("http://psi.archaeologie.sachsen.de/preusker/empfangen_von");
		assertNotNull(roleTypeTopic);
		
		letterRoles = letterTopic.getRolesPlayed(roleTypeTopic);
		assertEquals("Roles Check for Letter", 1, letterRoles.size());
	}
	
	
	private Topic getTopic(String si) {
		TopicMap tm = session.getTopicMap();
		Locator letterSI = tm.createLocator(si);
		return tm.getTopicBySubjectIdentifier(letterSI);
	}
}
