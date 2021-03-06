/**
 * 
 */
package de.topicmapslab.aranuka.test.delete;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.test.AbstractTest;

/**
 * @author Hannes Niederhausen
 *
 */
public class DeleteTest extends AbstractTest {

	private Topic1 t1;
	private Topic3 t3;
	private Topic2 t2;
	private Session session;

	@Before
	public void setUp() throws Exception {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());
		
		conf.addClass(Topic1.class);
		conf.addClass(Topic2.class);
		conf.addClass(Topic3.class);
		conf.setProperty(IProperties.BASE_LOCATOR, "http://test.aranuka.de/testcase_delete");
		conf.addPrefix("tp", "http://test.de/");
		
		session = conf.getSession(false);
		
		t1 = new Topic1();
		t1.setId("http://test.de/topic1");
		session.persist(t1);
		
		t3 = new Topic3();
		t3.setId("http://test.de/topic3");
		session.persist(t3);
		
		t2 = new Topic2();
		t2.setId("http://test.de/topic2");
		t2.setTopic1(t1);
		session.persist(t2);
	}
	

	@After
	public void tearDown() {
		session.clearTopicMap();
	}
	
	@org.junit.Test
	public void testRemoveT1() {
		try {
			assertNotNull(session.getBySubjectIdentifier(t1.getId()));
			session.remove(t1);	
			assertNull(session.getBySubjectIdentifier(t1.getId()));
			Topic2 t2 = (Topic2) session.getByItemIdentifier(this.t2.getId());
			assertNull(t2.getTopic1());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	@org.junit.Test
	public void testRemoveT3() {
		try {
			assertNotNull(session.getBySubjectLocator(t3.getId()));
			session.remove(t3);			
			assertNull(session.getBySubjectLocator(t3.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@org.junit.Test
	public void testRemoveT2() {
		try {
			assertNotNull(session.getByItemIdentifier(t2.getId()));
			session.remove(t2);			
			assertNull(session.getByItemIdentifier(t2.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testClear() {
		try {
			assertNotNull(session.getByItemIdentifier(t1.getId()));
			assertNotNull(session.getByItemIdentifier(t2.getId()));
			assertNotNull(session.getByItemIdentifier(t3.getId()));
			session.clearTopicMap();			
			assertNull(session.getByItemIdentifier(t1.getId()));
			assertNull(session.getByItemIdentifier(t2.getId()));
			assertNull(session.getByItemIdentifier(t3.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
