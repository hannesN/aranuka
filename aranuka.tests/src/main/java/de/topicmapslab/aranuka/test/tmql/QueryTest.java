/**
 * 
 */
package de.topicmapslab.aranuka.test.tmql;

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
import de.topicmapslab.aranuka.test.tmql.model.Person;

/**
 * @author niederhausen
 * 
 */
public class QueryTest extends AbstractTest {
	private Session session;
	
	@Before
	public void setup() throws AranukaException {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());

		conf.setProperty(IProperties.BASE_LOCATOR,
				"http://test.aranuka.de/testcase_delete");
		conf.addPrefix("tp", "http://test.de/");
		
		conf.addClass(Person.class);

		session = conf.getSession(false);

		assertNotNull(session);
		
		session.persist(new Person("http://test.de/p/1", "Hannes"));
		session.persist(new Person("http://test.de/p/2", "Hans"));
		session.persist(new Person("http://test.de/p/3", "Peter"));
		
		assertEquals(3, session.getAll(Person.class).size());
	}
	
	@After
	public void tearDown() {
		session.clearTopicMap();
	}
	
	@Test
	public void getByType() throws AranukaException {
		
		assertEquals(3, session.getObjectsByQuery("http://test.de/person >> instances").size());
		
	}
	
	@Test(expected=AranukaException.class)
	public void getType() throws AranukaException {
		session.getObjectsByQuery("http://test.de/person");
	}
	
	@Test
	public void getByNameRegExp() throws AranukaException {
		
		assertEquals(2, session.getObjectsByQuery("SELECT http://test.de/person >> instances [ . >> characteristics tm:name =~ \"Han.*s\"] ").size());
		
	}
}
