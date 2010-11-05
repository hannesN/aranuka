package de.topicmapslab.aranuka.test.read;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.test.AbstractTest;


public class PrefixReadTest extends  AbstractTest{

	private final static String topic_SI = "test:test_topic_1";
	
	private final static Set<String> topic_SL = new HashSet<String>(Arrays.asList("test:test_topic_1_sl_1","test2:test_topic_1_sl_2"));

	private final static String[] topic_II = {"test:test_topic_1-ii_1","test2:test_topic_1-ii_2"};
	
	Configuration config;
	Session session;
	
	@Before
	public void setUp() throws Exception {

	}
	
	@Test
	public void testCase01() throws Exception{
		if ("memory".equals(getProperties().getProperty(IProperties.BACKEND))) {
			setupMemoryTest();
			readTest();
		}
	}
	
//	@Test
//	public void testCase02() throws Exception{
//		
//		setupDatabaseTest();
//		readTest();
//	}
	
	
	private void readTest(){
		
		Set<TestTopicType3> instances = this.session.getAll(TestTopicType3.class);
		assertEquals(1,instances.size());
		
		TestTopicType3 test = (TestTopicType3)instances.iterator().next();
		
		// identifier
		assertTrue(test.getStringSubjectIdentifier().equals(topic_SI));
		assertTrue(test.getSetSubjectLocator().equals(topic_SL));
		Set<String> isState = new HashSet<String>(Arrays.asList(test.getArrayItemIdentifier()));
		Set<String> shouldState = new HashSet<String>(Arrays.asList(topic_II));
		
		isState.containsAll(shouldState);
	}
	
	// helper methods
	
	private void setupMemoryTest() throws Exception{
		
		String filename = "/tmp/read_test_map.xtm";
		
		File f = new File(filename);
		if(f.exists())
			f.delete();

		Configuration writeConfig = new Configuration();
		writeConfig.setProperties(getProperties());
		writeConfig.setProperty(IProperties.BASE_LOCATOR, "http://topicmapslab.de/aranuka/test/");
		writeConfig.setProperty(IProperties.FILENAME, filename);

		writeConfig.addPrefix("test", "http://topicmapslab.de/aranuka/test/");
		
		writeConfig.addClass(TestTopicType3.class);
		
		Session writeSession = writeConfig.getSession(false);
		
		assertNotNull(writeSession);
		createTestMap(writeSession);
		
		assertTrue(f.exists());
		
		// create the test session

		this.config = new Configuration();
		this.config.setProperties(getProperties());
		this.config.setProperty(IProperties.BASE_LOCATOR, "http://topicmapslab.de/aranuka/test/");
		this.config.setProperty(IProperties.FILENAME, filename);

		this.config.addPrefix("test", "http://topicmapslab.de/aranuka/test/");
		this.config.addPrefix("test2", "http://topicmapslab.de/aranuka/testerei/");
		
		this.config.addClass(TestTopicType3.class);
		
		this.session = this.config.getSession(false);
		
		assertNotNull(this.session);
	}
	
	
	private void createTestMap(Session session) throws Exception{
		
		TestTopicType3 test = new TestTopicType3();
		
		// identifier
		test.setStringSubjectIdentifier(topic_SI);
		test.setSetSubjectLocator(topic_SL);
		test.setArrayItemIdentifier(topic_II);
		
		
		session.persist(test);
		session.flushTopicMap();
		
	}
	

	
	
	
	
}
