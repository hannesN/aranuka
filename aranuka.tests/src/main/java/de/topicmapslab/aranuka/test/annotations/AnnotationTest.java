package de.topicmapslab.aranuka.test.annotations;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.test.AbstractTest;

public class AnnotationTest extends AbstractTest {
	private Configuration config;

	@Before
	public void setUp() throws Exception {

		this.config = new Configuration();
		
		this.config.setProperties(getProperties());
		
		// TODO set connector
		this.config.setProperty(IProperties.BASE_LOCATOR, "http://topicmapslab.de/aranuka/test/");
		this.config.addPrefix("test", "http://topicmapslab.de/aranuka/test/");

		
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

	@Test(expected = AranukaException.class)
	public void testCase02() throws Exception {

		// TopicType02 has no identifier fields

		this.config.addClass(TopicType02.class);
		this.config.getSession(false);

	}

	@Test(expected = AranukaException.class)
	public void testCase03() throws Exception {

		// class is not @Topic annotated
		this.config.addClass(TopicType03.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = AranukaException.class)
	public void testCase04() throws Exception {

		// has too many subject identifier
		this.config.addClass(TopicType04.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = AranukaException.class)
	public void testCase05() throws Exception {

		// has too many subject locator
		this.config.addClass(TopicType05.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = AranukaException.class)
	public void testCase06() throws Exception {

		// has too many item identifier
		this.config.addClass(TopicType06.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = AranukaException.class)
	public void testCase07() throws Exception {

		// name is not of type string
		this.config.addClass(TopicType07.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = AranukaException.class)
	public void testCase08() throws Exception {

		// field has no setter
		this.config.addClass(TopicType08.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = AranukaException.class)
	public void testCase09() throws Exception {

		// field has no getter
		this.config.addClass(TopicType09.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}

	@Test(expected = AranukaException.class)
	public void testCase10() throws Exception {

		// two name fields have the same type
		this.config.addClass(TopicType10.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}
	
	@Test(expected = AranukaException.class)
	public void testCase11() throws Exception {

		// two occurrence fields have the same type
		this.config.addClass(TopicType11.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}
	
	@Test(expected = AranukaException.class)
	public void testCase12() throws Exception {

		// field type of an unary association is not boolean
		this.config.addClass(TopicType12.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
		
	}

	@Test(expected = AranukaException.class)
	public void testCase13() throws Exception {

		// counter player of an non unary association is not @Topic or @AssociationContainer annotated
		this.config.addClass(TopicType13.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}
	
	@Test(expected = AranukaException.class)
	public void testCase14() throws Exception {

		// association container has no roles
		this.config.addClass(TopicType14.class);
		@SuppressWarnings("unused")
		Session session = this.config.getSession(false);
	}
	
	@Test(expected = AranukaException.class)
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
	
	@Test
	public void testCase17() throws Exception {

		// TopicType02 has no identifier fields
		// should work - no excpetion because class is abstract
		this.config.addClass(TopicType17.class);
		this.config.getSession(false);

	}
	
}
