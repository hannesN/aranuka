package de.topicmapslab.aranuka.test.transitive;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.test.AbstractTest;

public class TransientTest extends AbstractTest{

	private Session session;

	@Before
	public void setup() throws Exception {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());
		
		conf.addClass(SubKlasse.class);
		conf.addClass(SuperKlasse.class);
		conf.addClass(MiniKlasse.class);
		conf.setProperty(IProperties.BASE_LOCATOR, "http://test.aranuka.de/testcase_delete");
		conf.addPrefix("tp", "http://test.de/");
		
		session = conf.getSession(false);
		
		for (int i=0; i<5; i++) {
			SubKlasse tmp = new SubKlasse("Hallo! "+i);
			tmp.setId("http://test.de/ii/"+i);
			session.persist(tmp);
		}
	}
	
	@After
	public void tearDown() {
		session.clearTopicMap();
	}
	

	@Test
	public void testNumberOfSubClasses() throws Exception {
		Set<SubKlasse> tmp = session.getAll(SubKlasse.class);
		assertEquals(5, tmp.size());
		
		Set<SuperKlasse> tmp2 = session.getAll(SuperKlasse.class);
		assertEquals(5, tmp2.size());
		
		session.persist(new SuperKlasse());
		
		tmp = session.getAll(SubKlasse.class);
		assertEquals(5, tmp.size());
		
		tmp2 = session.getAll(SuperKlasse.class);
		assertEquals(6, tmp2.size());
	}
	
	@Test
	public void testNumberOfMiniClasses() throws Exception {
		Set<SubKlasse> tmp = session.getAll(SubKlasse.class);
		assertEquals(5, tmp.size());
		
		Set<SuperKlasse> tmp2 = session.getAll(SuperKlasse.class);
		assertEquals(5, tmp.size());
		
		session.persist(new SuperKlasse());
		
		tmp = session.getAll(SubKlasse.class);
		assertEquals(5, tmp.size());
		
		tmp2 = session.getAll(SuperKlasse.class);
		assertEquals(6, tmp2.size());
		
		session.persist(new MiniKlasse());
		
		tmp = session.getAll(SubKlasse.class);
		assertEquals(6, tmp.size());
		
		tmp2 = session.getAll(SuperKlasse.class);
		assertEquals(7, tmp2.size());
		
		Set<MiniKlasse> tmp3 = session.getAll(MiniKlasse.class);
		assertEquals(1, tmp3.size());
	}
	
}
