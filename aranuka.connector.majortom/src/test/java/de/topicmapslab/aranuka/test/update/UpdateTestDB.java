package de.topicmapslab.aranuka.test.update;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.majortom.connector.MaJorToMEngineConnector;
import de.topicmapslab.aranuka.test.AbstractTest;
import de.topicmapslab.aranuka.test.write.TestAssociationContainer;
import de.topicmapslab.aranuka.test.write.TestCounterPlayer;
import de.topicmapslab.aranuka.test.write.TestTopicType;


public class UpdateTestDB extends  AbstractTest {

	Configuration config;
	Session session;
	
	@Before
	public void setUp() throws Exception {

		String filename = "/tmp/read_test_map.xtm";
		
		File f = new File(filename);
		if(f.exists())
			f.delete();

		this.config = new Configuration();
		this.config.setProperty(IProperties.CONNECTOR_CLASS, MaJorToMEngineConnector.class.getName());
		this.config.setProperty(IProperties.BASE_LOCATOR, "http://topicmapslab.de/aranuka/test/");
		
		// database connectors
		this.config.setProperty(IProperties.BACKEND, "db");
		this.config.setProperty(IProperties.DATABASESYSTEM, "postgresql");
		this.config.setProperty(IProperties.DATABASE_NAME, "aranuka");
		this.config.setProperty(IProperties.DATABASE_PASSWORD, "_testAranuka#");
		this.config.setProperty(IProperties.DATABASE_LOGIN, "postgres");


		this.config.addPrefix("test", "http://topicmapslab.de/aranuka/test/");
		
		this.config.addClass(TestTopicType.class);
		this.config.addClass(TestCounterPlayer.class);
		
		this.session = this.config.getSession(false);
		
		assertNotNull(this.session);

	}
	
	@Test
	public void testCase01() throws Exception {
		
		// change the only subject identifier
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		
		final String si2 = "http://topicmapslab.de/aranuka/test/test_topic";
		test.setStringSubjectIdentifier(si2);
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si2));
		assertEquals(1,numberOfSubjectIdentifiers(this.session.getTopicMap(), si2));
		
	}
	
	@Test
	public void testCase02() throws Exception {
		
		// add an locator to set
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);

		Set<String> sl = new HashSet<String>();
		sl.add("http://topicmapslab.de/aranuka/test/test_topic/sl1");
		
		test.setSetSubjectLocator(sl);
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfSubjectLocators(this.session.getTopicMap(), si));
		
		sl.add("http://topicmapslab.de/aranuka/test/test_topic/sl2"); // add second subject locator
		test.setSetSubjectLocator(sl);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,numberOfSubjectLocators(this.session.getTopicMap(), si));
		
	}

	@Test
	public void testCase03() throws Exception {
		
		// remove an locator from set
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);

		// create set with two
		Set<String> sl = new HashSet<String>();
		sl.add("http://topicmapslab.de/aranuka/test/test_topic/sl1");
		sl.add("http://topicmapslab.de/aranuka/test/test_topic/sl2");
		
		test.setSetSubjectLocator(sl);
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,numberOfSubjectLocators(this.session.getTopicMap(), si));
		
		// create set with one
		sl = new HashSet<String>();
		sl.add("http://topicmapslab.de/aranuka/test/test_topic/sl1");
		test.setSetSubjectLocator(sl);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfSubjectLocators(this.session.getTopicMap(), si));
		
	}
	
	@Test
	public void testCase04() throws Exception {
		
		// add an locator to array
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);

		String[] ii = {"http://topicmapslab.de/aranuka/test/test_topic/sl1"};
		
		test.setArrayItemIdentifier(ii);
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfItemIdentifiers(this.session.getTopicMap(), si));
		
		String[] ii2 = {"http://topicmapslab.de/aranuka/test/test_topic/sl1", "http://topicmapslab.de/aranuka/test/test_topic/sl2"};
			
		test.setArrayItemIdentifier(ii2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,numberOfItemIdentifiers(this.session.getTopicMap(), si));
		
	}
	
	@Test
	public void testCase05() throws Exception {
		
		// remove an locator from array
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);

		String[] ii = {"http://topicmapslab.de/aranuka/test/test_topic/sl1", "http://topicmapslab.de/aranuka/test/test_topic/sl2"};
		
		test.setArrayItemIdentifier(ii);
		
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,numberOfItemIdentifiers(this.session.getTopicMap(), si));
		
		String[] ii2 = {"http://topicmapslab.de/aranuka/test/test_topic/sl1"};
			
		test.setArrayItemIdentifier(ii2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfItemIdentifiers(this.session.getTopicMap(), si));
		
	}
	
	@Test
	public void testCase06() throws Exception {
		
		// change name
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		String[] n1 = {"Name1"};
		test.setArrayNames(n1);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name").contains("Name1"));
		
		String[] n2 = {"Name2"};
		test.setArrayNames(n2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name").contains("Name2"));
		
	}
	
	@Test
	public void testCase07() throws Exception {
		
		// add name
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		String[] n1 = {"Name1"};
		test.setArrayNames(n1);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name").contains("Name1"));
		
		String[] n2 = {"Name1","Name2"};
		test.setArrayNames(n2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name").contains("Name1"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name").contains("Name2"));
	}
	
	@Test
	public void testCase08() throws Exception {
		
		// remove name
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		String[] n1 = {"Name1","Name2"};
		test.setArrayNames(n1);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name").contains("Name1"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name").contains("Name2"));
		
		String[] n2 = {"Name1"};
		test.setArrayNames(n2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_name").contains("Name1"));
	
	}
	
	@Test
	public void testCase09() throws Exception {
		
		// change string name
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);

		test.setStringName("Name1");
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_name").contains("Name1"));
		
		test.setStringName("Name2");
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_name").contains("Name2"));

		
	}
	
	@Test
	public void testCase10() throws Exception {
		
		// change set name
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		Set<String> names = new HashSet<String>();
		names.add("Name1");
		test.setSetNames(names);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name").contains("Name1"));
		
		names = new HashSet<String>();
		names.add("Name2");
		test.setSetNames(names);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name").contains("Name2"));
		
		
	}
	
	@Test
	public void testCase11() throws Exception {
		
		// add set name
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		Set<String> names = new HashSet<String>();
		names.add("Name1");
		test.setSetNames(names);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name").contains("Name1"));
		
		names.add("Name2");
		test.setSetNames(names);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name").contains("Name1"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name").contains("Name2"));
		
	}

	@Test
	public void testCase12() throws Exception {
		
		// remove set name
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		Set<String> names = new HashSet<String>();
		names.add("Name1");
		names.add("Name2");
		test.setSetNames(names);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name").contains("Name1"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name").contains("Name2"));
		
		names = new HashSet<String>();
		names.add("Name1");
		test.setSetNames(names);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,numberOfTopicNames(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name"));
		assertTrue(getNameValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_name").contains("Name1"));
		
	}
	
	@Test
	public void testCase13() throws Exception {
		
		// change int occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		test.setIntOccurrence(1);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/int_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/int_occurrence", 1));
		
		test.setIntOccurrence(2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/int_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/int_occurrence", 2));
		
	}
	
	@Test
	public void testCase14() throws Exception {
		
		// change float occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		test.setFloatOccurrence(1.1f);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/float_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/float_occurrence", 1.1f));
		
		test.setFloatOccurrence(1.2f);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/float_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/float_occurrence", 1.2f));
		
	}
	
	@Test
	public void testCase15() throws Exception {
		
		// change double occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		test.setDoubleOccurrence(1.1);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/double_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/double_occurrence", 1.1));
		
		test.setDoubleOccurrence(1.2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/double_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/double_occurrence", 1.2));
	}
	
	@Test
	public void testCase16() throws Exception {
		// change date occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		Date date1 = new GregorianCalendar(2010, 02, 04).getTime();
		Date date2 = new GregorianCalendar(1999, 05, 12).getTime();
		
		test.setDateOccurrence(date1);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/date_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/date_occurrence", date1));
		
		test.setDateOccurrence(date2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/date_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/date_occurrence", date2));
	}
	
	@Test
	public void testCase17() throws Exception {
		
		// change boolean occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		test.setBooleanOccurrence(true);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/boolean_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/boolean_occurrence", true));
		
		test.setBooleanOccurrence(false);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/boolean_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/boolean_occurrence", false));
	}
	
	@Test
	public void testCase18() throws Exception {
		
		// change string occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		test.setStringOccurrence("Test1");
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_occurrence", "Test1"));
		
		test.setStringOccurrence("Test2");
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_occurrence"));
		assertTrue(verifyOccurrence(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/string_occurrence", "Test2"));
	}
	
	@Test
	public void testCase19() throws Exception {
		
		// add set occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		Set<String> values = new HashSet<String>();
		values.add("Occurrence1");
		test.setSetSringOccurrence(values);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence").contains("Occurrence1"));
				
		values.add("Occurrence2");
		test.setSetSringOccurrence(values);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence").contains("Occurrence1"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence").contains("Occurrence2"));
		
	}
	
	@Test
	public void testCase20() throws Exception {
		
		// remove set occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		Set<String> values = new HashSet<String>();
		values.add("Occurrence1");
		values.add("Occurrence2");
		test.setSetSringOccurrence(values);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence").contains("Occurrence1"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence").contains("Occurrence2"));
		
		values = new HashSet<String>();
		values.add("Occurrence1");
		test.setSetSringOccurrence(values);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence").contains("Occurrence1"));
	
	}
	
	@Test
	public void testCase21() throws Exception {
		
		// add array occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		String[] values = {"Occurrence1"};
		
		test.setArrayStringOccurrece(values);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence").contains("Occurrence1"));
				
		String[] values2 = {"Occurrence1","Occurrence2"};
		test.setArrayStringOccurrece(values2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence").contains("Occurrence1"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence").contains("Occurrence2"));
	}

	@Test
	public void testCase22() throws Exception {
		
		// remove array occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		String[] values = {"Occurrence1","Occurrence2"};
		
		test.setArrayStringOccurrece(values);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(2,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence").contains("Occurrence1"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence").contains("Occurrence2"));
				
		String[] values2 = {"Occurrence1"};
		test.setArrayStringOccurrece(values2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence").contains("Occurrence1"));
		
	}
	
	@Test
	public void testCase23() throws Exception {
		
		// change set occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		Set<String> values = new HashSet<String>();
		values.add("Occurrence1");
		test.setSetSringOccurrence(values);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence").contains("Occurrence1"));
		
		values = new HashSet<String>();
		values.add("Occurrence2");
		test.setSetSringOccurrence(values);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/set_string_occurrence").contains("Occurrence2"));
	}
	
	@Test
	public void testCase24() throws Exception {
		
		// change array occurrence
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		String[] values = {"Occurrence1"};
		
		test.setArrayStringOccurrece(values);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence").contains("Occurrence1"));
						
		String[] values2 = {"Occurrence2"};
		test.setArrayStringOccurrece(values2);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertEquals(1,getNumberOfOccurrences(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence"));
		assertTrue(getOccurrenceValues(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/array_string_occurrence").contains("Occurrence2"));
	}
	
	@Test
	public void testCase25() throws Exception {
		
		// remove an unary association
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		test.setUnaryAssociation(true);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/unnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", new HashMap<String,Set<String>>()));
		
		// remove association
		test.setUnaryAssociation(false);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertFalse(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/unnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", new HashMap<String,Set<String>>()));
	}
	
	@Test
	public void testCase26() throws Exception {
		
		// remove an binary association
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		TestCounterPlayer counter = new TestCounterPlayer();
		counter.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/1");
		
		test.setBinaryAssociation(counter);
		
		this.session.persist(test);
		this.session.flushTopicMap();
	
		Map<String,Set<String>> counterPlayer = new HashMap<String,Set<String>>();
		counterPlayer.put("http://topicmapslab.de/aranuka/test/counter_player_01_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/binary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counterPlayer));
		
		// remove
		test.setBinaryAssociation(null);
		
		this.session.persist(test);
		this.session.flushTopicMap();
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertFalse(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/binary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counterPlayer));
		
	}
	
	@Test
	public void testCase27() throws Exception {
		
		// add a role to an nnary association
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		TestAssociationContainer container = new TestAssociationContainer();
		
		TestCounterPlayer counter1 = new TestCounterPlayer();
		counter1.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/1");
		
		TestCounterPlayer counter2 = new TestCounterPlayer();
		counter2.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/2");

		Set<TestCounterPlayer> set = new HashSet<TestCounterPlayer>();
		set.add(counter2);
		
		container.setCounterPlayer(counter1);
		container.setCounterPlayerSet(set);
		test.setNnaryAssociation(container);
		this.session.persist(test);

		Map<String,Set<String>> counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_set_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/2")));
		
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));
		
		
		TestCounterPlayer counter3 = new TestCounterPlayer();
		counter3.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/3");
		
		set.add(counter3);
		container.setCounterPlayerSet(set);
		test.setNnaryAssociation(container);
		this.session.persist(test);
		
		counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_set_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/2","http://topicmapslab.de/aranuka/test/counter/3")));
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));
		
	}
	
	@Test
	public void testCase28() throws Exception {
		
		// remove a role of an nnary association
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

		Map<String,Set<String>> counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_set_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/2","http://topicmapslab.de/aranuka/test/counter/3")));
		
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));
		
		set = new HashSet<TestCounterPlayer>();
		set.add(counter2);
		container.setCounterPlayerSet(set);
		test.setNnaryAssociation(container);
		this.session.persist(test);
		
		counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_set_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/2")));
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));

	}
	
	@Test
	public void testCase29() throws Exception {
		
		// remove whole nnary association
		
		final String si = "http://topicmapslab.de/aranuka/test/test_topic/1";
		
		TestTopicType test = new TestTopicType();
		test.setStringSubjectIdentifier(si);
		
		TestAssociationContainer container = new TestAssociationContainer();
		
		TestCounterPlayer counter1 = new TestCounterPlayer();
		counter1.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/1");
		
		TestCounterPlayer counter2 = new TestCounterPlayer();
		counter2.setSubjectIdentifier("http://topicmapslab.de/aranuka/test/counter/2");

		Set<TestCounterPlayer> set = new HashSet<TestCounterPlayer>();
		set.add(counter2);
		
		container.setCounterPlayer(counter1);
		container.setCounterPlayerSet(set);
		test.setNnaryAssociation(container);
		this.session.persist(test);

		Map<String,Set<String>> counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_set_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/2")));
		
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));

		test.setNnaryAssociation(null);
		this.session.persist(test);
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertFalse(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));
		
	}
	
	@Test
	public void testCase30() throws Exception {
		
		// remove card 0..1 role
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
		this.session.flushTopicMap();

		Map<String,Set<String>> counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/1")));
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_set_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/2","http://topicmapslab.de/aranuka/test/counter/3")));
		
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));
		
		container.setCounterPlayer(null); // remove role
		this.session.persist(test);
		
		counter = new HashMap<String,Set<String>>();
		counter.put("http://topicmapslab.de/aranuka/test/counter_player_set_role", new HashSet<String>(Arrays.asList("http://topicmapslab.de/aranuka/test/counter/2","http://topicmapslab.de/aranuka/test/counter/3")));
		
		assertTrue(verifyTopicExist(this.session.getTopicMap(), si));
		assertTrue(associationExist(this.session.getTopicMap(), si, "http://topicmapslab.de/aranuka/test/nnary_association", 
				"http://topicmapslab.de/aranuka/test/topic_type_01_role", counter));
		
	}
	
}
