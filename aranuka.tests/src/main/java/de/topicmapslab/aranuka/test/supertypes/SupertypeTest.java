package de.topicmapslab.aranuka.test.supertypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.test.AbstractTest;
import de.topicmapslab.aranuka.test.supertypes.model.Lamp;
import de.topicmapslab.aranuka.test.supertypes.model.Person;
import de.topicmapslab.aranuka.test.supertypes.model.Place;
import de.topicmapslab.aranuka.test.supertypes.model.Place.AssocContainer;

public class SupertypeTest extends AbstractTest {

	private static final String SUPERTYPE = "http://psi.topicmaps.org/iso13250/model/supertype";
	private static final String SUBTYPE = "http://psi.topicmaps.org/iso13250/model/subtype";
	private static final String SUPERTYPE_SUBTYPE = "http://psi.topicmaps.org/iso13250/model/supertype-subtype";
	private Session session;
	private Locator baseLocator;
	private TopicMap topicMap;
	private Topic lampType;
	private Lamp lamp;

	@Before
	public void setup() throws AranukaException {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());

		conf.addPackage("de.topicmapslab.aranuka.test.supertypes.model");
		conf.setProperty(IProperties.BASE_LOCATOR,
				"http://test.aranuka.de/testcase_delete");
		conf.addPrefix("tp", "http://test.de/");

		session = conf.getSession(false);
		
		assertNotNull(session);
		
		lamp = new Lamp();
		lamp.setName("Luxor");
		lamp.setId("http://test.de/lamp/luxor");
		session.persist(lamp);

		topicMap = session.getTopicMap();

		baseLocator = topicMap.createLocator("http://test.de/");
		
		
	}

	@After
	public void tearDown() {
		if (session!=null)
			session.clearTopicMap();
	}

	@Test
	public void testType() throws Exception {
		
		lampType = topicMap.getTopicBySubjectIdentifier(baseLocator.resolve("lamp"));

		assertNotNull(lampType);

		// type should only play role in supertype subtype assoc
		Topic subTypeTopic = getSubTypeTopic();
		assertNotNull(subTypeTopic);
		
		Topic superTypeSubTypeTopic = getSuperTypeSubTypeTopic();
		assertNotNull(superTypeSubTypeTopic);
		
		Topic superTypeTopic = getSuperTypeTopic();
		assertNotNull(superTypeTopic);
		
		Set<Role> roleSet = lampType.getRolesPlayed(subTypeTopic, superTypeSubTypeTopic);
		assertEquals(1, roleSet.size());

		Role r = roleSet.iterator().next();
		// check if we have the right association
		Association assoc = r.getParent();
		assertEquals(superTypeSubTypeTopic, assoc.getType());
		Set<Role> otherRoles = assoc.getRoles(superTypeTopic);
		assertEquals(1, otherRoles.size());
		
		Role otherRole = otherRoles.iterator().next();
		assertEquals(baseLocator.resolve("thing"), otherRole.getPlayer()
				.getSubjectIdentifiers().iterator().next());
	

	}


	@Test
	public void instance() throws Exception {
		
		Locator l = topicMap.createLocator("http://test.de/lamp/luxor");
		Topic t = topicMap.getTopicBySubjectIdentifier(l);
		
		assertNotNull(t);
		
		Set<Name> names = t.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		assertEquals(lamp.getName(), name.getValue());
		
		// check if name has a supertype
		Topic subTypeTopic = getSubTypeTopic();
		assertNotNull(subTypeTopic);
		
		Topic superTypeSubTypeTopic = getSuperTypeSubTypeTopic();
		assertNotNull(superTypeSubTypeTopic);

		Topic superTypeTopic = getSuperTypeTopic();
		assertNotNull(superTypeTopic);
		
		Set<Role> rolesPlayed = t.getRolesPlayed(subTypeTopic, superTypeSubTypeTopic);
		assertEquals(true, rolesPlayed.isEmpty());
		
		// check if name has a subtype
		rolesPlayed = t.getRolesPlayed(superTypeTopic, superTypeSubTypeTopic);
		assertEquals(true, rolesPlayed.isEmpty());
		
		// has to have only one type: lamp
		assertEquals(1, t.getTypes().size());
		
		Topic tType = t.getTypes().iterator().next();
		Set<Locator> subjectIdentifiers = tType.getSubjectIdentifiers();
		assertEquals(1, subjectIdentifiers.size());
		
		assertEquals(baseLocator.resolve("lamp"), subjectIdentifiers.iterator().next());
		
	}
	
	@Test
	public void testAssociation() throws Exception {
		Person p = getPerson();
		
		session.persist(lamp);
		session.persist(p);
		instance();
		
		Person p2 = (Person) session.getByItemIdentifier(p.getId());
		
		assertEquals(p.getId(), p2.getId());
		assertEquals(p.getName(), p2.getName());
		assertEquals(p.getThing(), p2.getThing());
		
		
		
	}

	@Test
	public void nNaray() throws Exception {
		Person p = getPerson();
		
		Place place = new Place();
		place.setName("Place");
		
		AssocContainer assocContainer = new AssocContainer();
		place.setOwners(assocContainer);
		assocContainer.setOwner(p);
		assocContainer.setThing(lamp);
		
		session.persist(place);
		instance();
		
		Place pl2 = (Place) session.getBySubjectIdentifier(place.getSi());
		
		assertEquals(assocContainer, pl2.getOwners());
		
		assertEquals(place, pl2);
	}

	private Topic getSuperTypeSubTypeTopic() {
		return topicMap.getTopicBySubjectIdentifier(
				topicMap.createLocator(SUPERTYPE_SUBTYPE));
	}

	private Topic getSubTypeTopic() {
		return topicMap.getTopicBySubjectIdentifier(
				topicMap.createLocator(SUBTYPE));
	}

	private Topic getSuperTypeTopic() {
		return topicMap.getTopicBySubjectIdentifier(
				topicMap.createLocator(SUPERTYPE));
	}

	private Person getPerson() {
		Person p = new Person();
		p.setThing(lamp);
		p.setName("Heinz");
		return p;
	}
	
	
}
