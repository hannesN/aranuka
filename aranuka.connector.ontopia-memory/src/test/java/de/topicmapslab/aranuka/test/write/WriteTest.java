package de.topicmapslab.aranuka.test.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.TopicMapIOException;
import de.topicmapslab.aranuka.ontopia.memory.connectors.OntopiaMemoryConnector;
import de.topicmapslab.aranuka.test.AbstractTest;

public class WriteTest extends AbstractTest {

	Configuration config;
	Session session;

	@Before
	public void setUp() throws Exception {

		String filename = "/tmp/read_test_map.xtm";
		
		File f = new File(filename);
		if(f.exists())
			f.delete();
		
		this.config = new Configuration(OntopiaMemoryConnector.class);
		this.config.setProperty(IProperties.BASE_LOCATOR, "http://topicmapslab.de/aranuka/test/");
		this.config.setProperty(IProperties.FILENAME, filename);

		this.config.addPrefix("test", "http://topicmapslab.de/aranuka/test/");
		
		this.config.addClass(TestTopicType.class);
		this.config.addClass(TestCounterPlayer.class);
		
		this.config.addName("test:named_occurrence", "Named Occurrence");
		this.config.addName("http://topicmapslab.de/aranuka/test/resolved_named_occurrence", "Resolved Named Occurrence");
				
		this.session = this.config.getSession(false);
		
		
		assertNotNull(this.session);

	}

	@Test
	public void testCase01() throws Exception {

		// persist a simple topic
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));

	}

	@Test(expected=TopicMapIOException.class)
	public void testCase02() throws Exception {

		// persist a simple topic without identifier
		
		TestTopicType test = new TestTopicType();
				
		this.session.persist(test);
		
	}
	
	@Test
	public void testCase03() throws Exception {

		// persist a simple topic with names
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		test.setStringName("Name1");
		
		Set<String> nameSet = new HashSet<String>();
		nameSet.add("Name2");
		nameSet.add("Name3");
		String[] nameArray = {"Name4","Name5","Name6"};
		
		test.setSetNames(nameSet);
		test.setArrayNames(nameArray);
		
		this.session.persist(test);
				
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_name"));
		assertEquals(2,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name"));
		assertEquals(3,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name"));

	}
	
	@Test
	public void testCase04() throws Exception {

		// check name values
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
				
		Set<String> nameSet = new HashSet<String>();
		nameSet.add("Name1");
		nameSet.add("Name2");
				
		test.setSetNames(nameSet);
				
		this.session.persist(test);
				
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name"));
		
		Set<String> names = getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name");
		
		assertTrue(names.contains("Name1"));
		assertTrue(names.contains("Name2"));
		
	}
	
	@Test
	public void testCase05() throws Exception {

		// names check name scope
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		test.setStringName("Name1");
		
		
		this.session.persist(test);
				
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_name"));
		String[] themes = {"http://topicmapslab.de/aranuka/test/name_theme1", "http://topicmapslab.de/aranuka/test/name_theme2"};
		assertTrue(verifyNameScope(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_name", themes));

	}
	
	@Test
	public void testCase06() throws Exception {
		
		// test multiple identifier
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		Set<String> subjectLocators = new HashSet<String>();
		subjectLocators.add("http://topicmapslab.de/aranuka/test/test_topic/sl/1");
		subjectLocators.add("http://topicmapslab.de/aranuka/test/test_topic/sl/2");
		String[] itemIdentifiers = {"test:test1","test:test2","test:test3"};
		
		test.setSetSubjectLocator(subjectLocators);
		test.setArrayItemIdentifier(itemIdentifiers);
		
		this.session.persist(test);
				
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfSubjectIdentifiers(this.session.getTopicMap(), si));
		assertEquals(2,numberOfSubjectLocators(this.session.getTopicMap(), si));
		assertEquals(3,numberOfItemIdentifiers(this.session.getTopicMap(), si));
		
		
	}
	
	@Test
	public void testCase07() throws Exception {
		
		// add occurrences
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		test.setBooleanOccurrence(true);
		Date date = new GregorianCalendar(2010, 02, 04).getTime();
		test.setDateOccurrence(date);
		test.setDoubleOccurrence(3.14);
		test.setFloatOccurrence(3.14f);
		test.setIntOccurrence(1);
		test.setStringOccurrence("Test1");
		Set<String> stringOccurrences = new HashSet<String>();
		stringOccurrences.add("Test2");
		stringOccurrences.add("Test3");
		test.setSetSringOccurrence(stringOccurrences);
		String[] arrayOccurrence = {"Test4","Test5","Test6"};
		test.setArrayStringOccurrece(arrayOccurrence);
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/boolean_occurrence", true));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/date_occurrence", date));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/double_occurrence", 3.14));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/float_occurrence", 3.14f));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/int_occurrence", 1));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_occurrence", "Test1"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence", stringOccurrences));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence", arrayOccurrence));
		
	}
	
	@Test
	public void testCase08() throws Exception {
		
		// occurrence scope
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		test.setStringOccurrence("Occurrnence1");
		
		
		this.session.persist(test);
				
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		
		String[] themes = {"http://topicmapslab.de/aranuka/test/occurrence_theme1"};
		assertTrue(verifyOccurrenceScope(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_occurrence", themes));
	}
		
	@Test
	public void testCase09() throws Exception {
		
		// unary association
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		test.setUnaryAssociation(true);
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/unnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", new HashMap<String,Set<String>>()));
		
	}
	
	@Test
	public void testCase10() throws Exception {
	
		// binary association
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		TestCounterPlayer counter = new TestCounterPlayer();
		counter.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/1");
		
		test.setBinaryAssociation(counter);
		
		this.session.persist(test);
		
		
		Map<String,Set<String>> counterPlayer = new HashMap<String,Set<String>>();
		counterPlayer.put("http://topicmapslab.de/aranuka/test/counter_player_01_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/binary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counterPlayer));

	}
	
	@Test
	public void testCase11() throws Exception {
		
		// binary associations as set
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		TestCounterPlayer counter1 = new TestCounterPlayer();
		counter1.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/1");

		Set<TestCounterPlayer> counterPlayer = new HashSet<TestCounterPlayer>();
		counterPlayer.add(counter1);

		test.setSetBinaryAssociation(counterPlayer);

		this.session.persist(test);
		
		Set<String> counterRoles = new HashSet<String>();
		counterRoles.add("http://topicmapslab.de/aranuka/test/counter_player_01_role");
		
		Map<String,Set<String>> counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_01_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_binary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));

	}
	
	@Test
	public void testCase12() throws Exception {
		
		// binary associations as array
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		TestCounterPlayer counter1 = new TestCounterPlayer();
		counter1.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/1");

		TestCounterPlayer[] array = {counter1};
	
		test.setArrayBinaryAssociation(array);

		this.session.persist(test);
		
		Map<String,Set<String>> counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_01_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_binary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));

	}
	
	@Test
	public void testCase13() throws Exception {
		
		// nnary association
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		TestAssociationContainer container = new TestAssociationContainer();
		
		TestCounterPlayer counter1 = new TestCounterPlayer();
		counter1.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/1");
		
		TestCounterPlayer counter2 = new TestCounterPlayer();
		counter2.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/2");
		
		TestCounterPlayer counter3 = new TestCounterPlayer();
		counter3.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/3");
		
		Set<TestCounterPlayer> set = new HashSet<TestCounterPlayer>();
		set.add(counter2);
		set.add(counter3);
		
		container.setCounterPlayer(counter1);
		container.setCounterPlayerSet(set);
		
		test.setNnaryAssociation(container);
		
		this.session.persist(test);
		this.session.flushTopicMap(); // for testing
		
		
		Map<String,Set<String>> counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_set_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/2","http://topicmapslab.de/aranuka/test/counter/3")));
		
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));
	
	}
	
	@Test
	public void testCase14() throws Exception {
			
		// nnary association as set
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		TestAssociationContainer container = new TestAssociationContainer();
				
		TestCounterPlayer counter1 = new TestCounterPlayer();
		counter1.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/1");
		
		TestCounterPlayer counter2 = new TestCounterPlayer();
		counter2.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/2");
		
		TestCounterPlayer counter3 = new TestCounterPlayer();
		counter3.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/3");

		Set<TestCounterPlayer> set = new HashSet<TestCounterPlayer>();
		set.add(counter2);
		set.add(counter3);

		container.setCounterPlayer(counter1);
		container.setCounterPlayerSet(set);

		Set<TestAssociationContainer> containerSet = new HashSet<TestAssociationContainer>();
		
		containerSet.add(container);

		test.setSetNnaryAssociation(containerSet);
				
		this.session.persist(test);
		this.session.flushTopicMap(); // for testing
		
		Map<String,Set<String>> counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_set_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/2","http://topicmapslab.de/aranuka/test/counter/3")));
		
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));
	
	}
	
	@Test
	public void testCase15() throws Exception {
		
		// nnary association as array
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		TestAssociationContainer container = new TestAssociationContainer();
				
		TestCounterPlayer counter1 = new TestCounterPlayer();
		counter1.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/1");
		
		TestCounterPlayer counter2 = new TestCounterPlayer();
		counter2.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/2");
		
		TestCounterPlayer counter3 = new TestCounterPlayer();
		counter3.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/3");
		
		Set<TestCounterPlayer> set = new HashSet<TestCounterPlayer>();
		set.add(counter2);
		set.add(counter3);

		container.setCounterPlayer(counter1);
		container.setCounterPlayerSet(set);

		TestAssociationContainer[] containerArray = {container};
		
		test.setArrayNnaryAssociation(containerArray);
				
		this.session.persist(test);
		this.session.flushTopicMap(); // for testing
		
		Map<String,Set<String>> counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_set_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/2","http://topicmapslab.de/aranuka/test/counter/3")));
		
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));

	}
		
	@Test
	public void testCase16() throws Exception {
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		test.setNamedOccurrence("Named Occurrence");
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/named_occurrence", "Named Occurrence"));

		TopicMap tm = this.session.getTopicMap();
		
		assertNotNull(tm);
		
		Topic occType = tm.getTopicBySubjectIdentifier(tm.createLocator("http://topicmapslab.de/aranuka/test/named_occurrence"));
		assertNotNull(occType);
		assertEquals(1,occType.getNames().size());
		assertEquals("Named Occurrence",occType.getNames().iterator().next().getValue());
		
	}

	@Test
	public void testCase17() throws Exception {
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		test.setResolvedNamedOccurrence("Resolved Named Occurrence");
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/resolved_named_occurrence", "Resolved Named Occurrence"));

		TopicMap tm = this.session.getTopicMap();
		
		assertNotNull(tm);
		
		Topic occType = tm.getTopicBySubjectIdentifier(tm.createLocator("http://topicmapslab.de/aranuka/test/resolved_named_occurrence"));
		assertNotNull(occType);
		assertEquals(1,occType.getNames().size());
		assertEquals("Resolved Named Occurrence",occType.getNames().iterator().next().getValue());
		
	}
	
	@After
	public void tearDown(){
		
		
	}
	
}
