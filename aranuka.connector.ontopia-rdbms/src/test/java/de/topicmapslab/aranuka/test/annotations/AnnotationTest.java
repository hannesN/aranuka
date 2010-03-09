package de.topicmapslab.aranuka.test.annotations;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.ontopiy.rdbms.connectors.OntopiaRDBMSConnector;
import de.topicmapslab.aranuka.test.AbstractTest;

public class AnnotationTest extends AbstractTest {

	Configuration config;

	@Before
	public void setUp() throws Exception {

		//OntopiaMemoryDriver driver = new OntopiaMemoryDriver("http://topicmapslab.de/aranuka/test/", "./src/test/resources/test.xtm");
			
		this.config = new Configuration(OntopiaRDBMSConnector.class);
		
		Properties prop = loadProperties();
		prop.setProperty(IProperties.BASE_LOCATOR, "http://topicmapslab.de/aranuka/test/");

		this.config.setProperties(prop);
		this.config.addPrefix("test", "http://topicmapslab.de/aranuka/test/");
		//this.config.setBaseLocator("http://topicmapslab.de/aranuka/");
	}

	@Test
	public void testCase01() throws Exception {

		// good case
		
		this.config.addClass(TopicType01.class);
		this.config.addClass(CounterPlayer01.class);
		this.config.addClass(CounterPlayer02.class);

		Session session = this.config.getSession(false);

		assertNotNull(session);

	}

	@Test(expected = BadAnnotationException.class)
	public void testCase02() throws Exception {

		// TopicType02 has no identifier fields

		this.config.addClass(TopicType02.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);

	}

	@Test(expected = BadAnnotationException.class)
	public void testCase03() throws Exception {

		// class is not @Topic annotated
		this.config.addClass(TopicType03.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = BadAnnotationException.class)
	public void testCase04() throws Exception {

		// has too many subject identifier
		this.config.addClass(TopicType04.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = BadAnnotationException.class)
	public void testCase05() throws Exception {

		// has too many subject locator
		this.config.addClass(TopicType05.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = BadAnnotationException.class)
	public void testCase06() throws Exception {

		// has too many item identifier
		this.config.addClass(TopicType06.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = BadAnnotationException.class)
	public void testCase07() throws Exception {

		// name is not of type string
		this.config.addClass(TopicType07.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = NoSuchMethodException.class)
	public void testCase08() throws Exception {

		// field has no setter
		this.config.addClass(TopicType08.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = NoSuchMethodException.class)
	public void testCase09() throws Exception {

		// field has no getter
		this.config.addClass(TopicType09.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = BadAnnotationException.class)
	public void testCase10() throws Exception {

		// two name fields have the same type
		this.config.addClass(TopicType10.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}
	
	@Test(expected = BadAnnotationException.class)
	public void testCase11() throws Exception {

		// two occurrence fields have the same type
		this.config.addClass(TopicType11.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}
	
	@Test(expected = BadAnnotationException.class)
	public void testCase12() throws Exception {

		// field type of an unary association is not boolean
		this.config.addClass(TopicType12.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
		
	}

	@Test(expected = BadAnnotationException.class)
	public void testCase13() throws Exception {

		// counter player of an non unary association is not @Topic or @AssociationContainer annotated
		this.config.addClass(TopicType13.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}
	
	@Test(expected = BadAnnotationException.class)
	public void testCase14() throws Exception {

		// association container has no roles
		this.config.addClass(TopicType14.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}
	
	@Test(expected = BadAnnotationException.class)
	public void testCase15() throws Exception {

		// association container has a not @Role annotated field
		this.config.addClass(TopicType15.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}
	
	@Test
	public void testCase16() throws Exception {

		// create binding without types
		this.config.addClass(TopicType16.class);
		this.config.addClass(CounterPlayer01.class);
		this.config.addClass(CounterPlayer02.class);

		Session session = this.config.getSession(false);

		assertNotNull(session);

	}
	
}
