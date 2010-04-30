package de.topicmapslab.aranuka.test.read;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.ontopia.memory.connectors.OntopiaMemoryConnector;
import de.topicmapslab.aranuka.test.AbstractTest;


public class ReadTest extends  AbstractTest{

	private final static String topic_SI = "http://topicmapslab.de/aranuka/test/test_topic/1";
	private final static Set<String> topic_SL = new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/test_topic/1/sl_1","http://topicmapslab.de/aranuka/test/test_topic/1/sl_2"));
	private final static String[] topic_II = {"http://topicmapslab.de/aranuka/test/test_topic/1/ii_1","http://topicmapslab.de/aranuka/test/test_topic/1/ii_2"};
	private final static String counterPlayer1_SI = "http://topicmapslab.de/aranuka/test/counter_player/1";
	private final static String counterPlayer2_SI = "http://topicmapslab.de/aranuka/test/counter_player/2";
	
	private final static String stringName = "String Name";
	private final static Set<String> setName = new HashSet<String>(Arrays.asList("Set Name 1","Set Name 2"));
	private final static String[] arrayName = {"Array Name 1","Array Name 2"};
	
	private final static String stringOccurrence = "String Occurrence";
	private final static Set<String> setStringOccurrence = new HashSet<String>(Arrays.asList("Set String Occurrence 1","Set String Occurrence 2"));
	private final static String[] arrayStringOccurrence = {"Array String Occurrence 1","Array String Occurrence 2"};
	private final static int intOccurrence = 42;
	private final static float floatOccurrence = (float)Math.PI;
	private final static double doubleOccurrence = Math.E;
	private final static boolean booleanOccurrence = true;
	private final static Date dateOccurrence = new GregorianCalendar(2002, 20, 02).getTime();
	
	private final static boolean unaryAssociation = false;
	
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
	
//	@DeleteTest
//	public void testCase02() throws Exception{
//		
//		setupDatabaseTest();
//		readTest();
//	}
	
	
	@SuppressWarnings("unchecked")
	private void readTest(){
		
		Set<Object> instances = this.session.getAll(TestTopicType.class);
		assertEquals(1,instances.size());
		
		TestTopicType test = (TestTopicType)instances.iterator().next();
		
		// identifier
		assertTrue(test.getStringSubjectIdentifier().equals(topic_SI));
		assertTrue(test.getSetSubjectLocator().equals(topic_SL));
		//assertTrue(new HashSet(Arrays.asList(test.getArrayItemIdentifier())).equals(new HashSet(Arrays.asList(topic_II)))); /// TODO tmapix adds own ids as item identifier
		
		// names
		assertTrue(test.getStringName().equals(stringName));
		assertTrue(test.getSetNames().equals(setName));
		assertTrue(new HashSet(Arrays.asList(test.getArrayNames())).equals(new HashSet(Arrays.asList(arrayName))));
		
		// occurrences
		assertTrue(test.getStringOccurrence().equals(stringOccurrence));
		assertTrue(test.getSetSringOccurrence().equals(setStringOccurrence));
		assertTrue(new HashSet(Arrays.asList(test.getArrayStringOccurrece())).equals(new HashSet(Arrays.asList(arrayStringOccurrence))));
		assertTrue(test.getIntOccurrence() == intOccurrence);
		assertTrue(test.getFloatOccurrence() == floatOccurrence);
		assertTrue(test.getDoubleOccurrence() == doubleOccurrence);
		assertTrue(test.isBooleanOccurrence() == booleanOccurrence);
		assertTrue(test.getDateOccurrence().equals(dateOccurrence));
		
		// associtions
		assertTrue(test.isUnaryAssociation() == unaryAssociation);
		
		assertTrue(test.getBinaryAssociation() != null);
		assertTrue(test.getBinaryAssociation().getSubjectIdentifier().equals(counterPlayer1_SI));
		
		assertTrue(test.getSetBinaryAssociation() != null);
		assertEquals(2, test.getSetBinaryAssociation().size());
		for(TestCounterPlayer counter:test.getSetBinaryAssociation())
			assertTrue(counter.getSubjectIdentifier().equals(counterPlayer1_SI) || counter.getSubjectIdentifier().equals(counterPlayer2_SI));
		
		assertTrue(test.getArrayBinaryAssociation() != null);
		assertEquals(2, test.getArrayBinaryAssociation().length);
		for(TestCounterPlayer counter:test.getArrayBinaryAssociation())
			assertTrue(counter.getSubjectIdentifier().equals(counterPlayer1_SI) || counter.getSubjectIdentifier().equals(counterPlayer2_SI));
		
		assertTrue(test.getNnaryAssociation() != null);
		assertTrue(test.getNnaryAssociation().getCounterPlayer() != null);
		assertTrue(test.getNnaryAssociation().getCounterPlayerSet() != null);
		assertEquals(2, test.getNnaryAssociation().getCounterPlayerSet().size());
		assertTrue(test.getNnaryAssociation().getCounterPlayer().getSubjectIdentifier().equals(counterPlayer1_SI));
		for(TestCounterPlayer counter:test.getNnaryAssociation().getCounterPlayerSet())
			assertTrue(counter.getSubjectIdentifier().equals(counterPlayer1_SI) || counter.getSubjectIdentifier().equals(counterPlayer2_SI));
	
		assertTrue(test.getSetNnaryAssociation() != null);
		assertEquals(1, test.getSetNnaryAssociation().size());
		assertTrue(test.getSetNnaryAssociation().iterator().next().getCounterPlayer() != null);
		assertTrue(test.getSetNnaryAssociation().iterator().next().getCounterPlayerSet() != null);
		assertEquals(2, test.getSetNnaryAssociation().iterator().next().getCounterPlayerSet().size());
		assertTrue(test.getSetNnaryAssociation().iterator().next().getCounterPlayer().getSubjectIdentifier().equals(counterPlayer1_SI));
		for(TestCounterPlayer counter:test.getSetNnaryAssociation().iterator().next().getCounterPlayerSet())
			assertTrue(counter.getSubjectIdentifier().equals(counterPlayer1_SI) || counter.getSubjectIdentifier().equals(counterPlayer2_SI));
		
		assertTrue(test.getArrayNnaryAssociation() != null);
		assertEquals(1, test.getArrayNnaryAssociation().length);
		assertTrue(test.getArrayNnaryAssociation()[0].getCounterPlayer() != null);
		assertTrue(test.getArrayNnaryAssociation()[0].getCounterPlayerSet() != null);
		assertEquals(2, test.getArrayNnaryAssociation()[0].getCounterPlayerSet().size());
		assertTrue(test.getArrayNnaryAssociation()[0].getCounterPlayer().getSubjectIdentifier().equals(counterPlayer1_SI));
		for(TestCounterPlayer counter:test.getArrayNnaryAssociation()[0].getCounterPlayerSet())
			assertTrue(counter.getSubjectIdentifier().equals(counterPlayer1_SI) || counter.getSubjectIdentifier().equals(counterPlayer2_SI));
	}
	
	// helper methods
	
	private void setupMemoryTest() throws Exception{
		
		String filename = "/tmp/read_test_map.xtm";
		
		File f = new File(filename);
		if(f.exists())
			f.delete();

		Configuration writeConfig = new Configuration(OntopiaMemoryConnector.class);
		writeConfig.setProperty(IProperties.BASE_LOCATOR, "http://topicmapslab.de/aranuka/test/");
		writeConfig.setProperty(IProperties.FILENAME, filename);

		writeConfig.addPrefix("test", "http://topicmapslab.de/aranuka/test/");
		
		writeConfig.addClass(TestTopicType.class);
		writeConfig.addClass(TestCounterPlayer.class);
		
		Session writeSession = writeConfig.getSession(false);
		
		assertNotNull(writeSession);
		createTestMap(writeSession);
		
		assertTrue(f.exists());
		
		// create the test session

		this.config = new Configuration(OntopiaMemoryConnector.class);
		this.config.setProperty(IProperties.BASE_LOCATOR, "http://topicmapslab.de/aranuka/test/");
		this.config.setProperty(IProperties.FILENAME, filename);

		this.config.addPrefix("test", "http://topicmapslab.de/aranuka/test/");
		
		this.config.addClass(TestTopicType.class);
		this.config.addClass(TestCounterPlayer.class);
		
		this.session = this.config.getSession(false);
		
		assertNotNull(this.session);
	}
	
	private void createTestMap(Session session) throws Exception{
		
		// create counter player
		TestCounterPlayer counterPlayer1 = new TestCounterPlayer();
		counterPlayer1.setSubjectIdentifier(counterPlayer1_SI);
		
		TestCounterPlayer counterPlayer2 = new TestCounterPlayer();
		counterPlayer2.setSubjectIdentifier(counterPlayer2_SI);
		
		// create test topic
		TestTopicType test = new TestTopicType();
		
		// identifier
		test.setStringSubjectIdentifier(topic_SI);
		test.setSetSubjectLocator(topic_SL);
		test.setArrayItemIdentifier(topic_II);
		// names
		test.setStringName(stringName);
		test.setSetNames(setName);
		test.setArrayNames(arrayName);
		// occurrences
		test.setStringOccurrence(stringOccurrence);
		test.setSetSringOccurrence(setStringOccurrence);
		test.setArrayStringOccurrece(arrayStringOccurrence);
		test.setIntOccurrence(intOccurrence);
		test.setFloatOccurrence(floatOccurrence);
		test.setDoubleOccurrence(doubleOccurrence);
		test.setBooleanOccurrence(booleanOccurrence);
		test.setDateOccurrence(dateOccurrence);
		// associations
		test.setUnaryAssociation(unaryAssociation);
		
		
		test.setBinaryAssociation(counterPlayer1);
		test.setSetBinaryAssociation(new HashSet<TestCounterPlayer>(Arrays.asList(counterPlayer1,counterPlayer2)));
		test.setArrayBinaryAssociation(new TestCounterPlayer[]{counterPlayer1,counterPlayer2});
		
		TestAssociationContainer container1 = new TestAssociationContainer();
		container1.setCounterPlayer(counterPlayer1);
		container1.setCounterPlayerSet(new HashSet<TestCounterPlayer>(Arrays.asList(counterPlayer1,counterPlayer2)));
		
		test.setNnaryAssociation(container1);
		test.setSetNnaryAssociation(new HashSet<TestAssociationContainer>(Arrays.asList(container1)));
		test.setArrayNnaryAssociation(new TestAssociationContainer[]{container1});
		
		session.persist(test);
		session.flushTopicMap();
		
	}
	

	
	
	
	
}
