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
import de.topicmapslab.aranuka.majortom.connector.MaJorToMEngineConnector;
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
		
		setupMemoryTest();
		readTest();
	}
	
//	@Test
//	public void testCase02() throws Exception{
//		
//		setupDatabaseTest();
//		readTest();
//	}
	
	
	@SuppressWarnings("unchecked")
	private void readTest(){
		
		Set<Object> instances = this.session.getAll(TestTopicType3.class);
		assertEquals(1,instances.size());
		
		TestTopicType3 test = (TestTopicType3)instances.iterator().next();
		
		// identifier
		assertTrue(test.getStringSubjectIdentifier().equals(topic_SI));
		assertTrue(test.getSetSubjectLocator().equals(topic_SL));
		Set isState = new HashSet(Arrays.asList(test.getArrayItemIdentifier()));
		Set shouldState = new HashSet(Arrays.asList(topic_II));
		
		isState.containsAll(shouldState);
	}
	
	// helper methods
	
	private void setupMemoryTest() throws Exception{
		
		String filename = "/tmp/read_test_map.xtm";
		
		File f = new File(filename);
		if(f.exists())
			f.delete();

		Configuration writeConfig = new Configuration();
		writeConfig.setProperty(IProperties.CONNECTOR_CLASS, MaJorToMEngineConnector.class.getName());
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
		this.config.setProperty(IProperties.CONNECTOR_CLASS, MaJorToMEngineConnector.class.getName());
		this.config.setProperty(IProperties.BASE_LOCATOR, "http://topicmapslab.de/aranuka/test/");
		this.config.setProperty(IProperties.FILENAME, filename);

		this.config.addPrefix("test", "http://topicmapslab.de/aranuka/test/");
		this.config.addPrefix("test2", "http://topicmapslab.de/aranuka/testerei/");
		
		this.config.addClass(TestTopicType3.class);
		
		this.session = this.config.getSession(false);
		
		assertNotNull(this.session);
		
		createTestMap(this.session);
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
