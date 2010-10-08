package de.topicmapslab.aranuka.test.occurrencedatatype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapException;
import de.topicmapslab.aranuka.exception.TopicMapIOException;
import de.topicmapslab.aranuka.exception.TopicMapInconsistentException;
import de.topicmapslab.aranuka.test.AbstractTest;

public class OccDatatypeTest extends AbstractTest {

	private Session session;
	private City city;

	@Before
	public void setUp() throws BadAnnotationException, NoSuchMethodException,
			ClassNotSpecifiedException, TopicMapException {
		Configuration conf = new Configuration();
		conf.setProperties(getProperties());

		conf.addClass(City.class);
		conf.setProperty(IProperties.BASE_LOCATOR,
				"http://test.aranuka.de/testcase_delete");
		conf.addPrefix("tp", "http://test.de/");

		session = conf.getSession(false);

		city = new City();
		city.setSi("http://city.de/");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 10);
		cal.set(Calendar.MONTH, 1);
		cal.set(Calendar.YEAR, 1834);

		cal.set(Calendar.HOUR_OF_DAY, 14);
		cal.set(Calendar.MINUTE, 44);
		cal.set(Calendar.SECOND, 44);
		cal.set(Calendar.MILLISECOND, 44);

		city.setBuildDate(cal.getTime());
		city.setLastUpdate(new Date());
		city.setCoords("58.845;-12.453");

	}

	@After
	public void tearDown() {
		session.clearTopicMap();
	}

	@Test
	public void testCityOccurrences() throws BadAnnotationException,
			NoSuchMethodException, ClassNotSpecifiedException,
			TopicMapIOException, TopicMapInconsistentException,
			TopicMapException {
		Set<Object> cities = session.getAll(City.class);
		assertEquals(0, cities.size());

		session.persist(city);

		cities = session.getAll(City.class);
		assertEquals(1, cities.size());

		City oCity = (City) cities.iterator().next();

		assertTrue(city.equals(oCity));

		TopicMap tm = session.getTopicMap();
		assertNotNull(tm);

		org.tmapi.core.Topic t = getTopic(city.getSi());
		assertNotNull(t);

		assertEquals(3, t.getOccurrences().size());

		Set<Occurrence> occs = t.getOccurrences(getTopic("http://test.de/builddata"));
		assertEquals(1, occs.size());
		assertEquals("http://www.w3.org/2001/XMLSchema#date", occs.iterator().next().getDatatype().toExternalForm());

		occs = t.getOccurrences(getTopic("http://test.de/lastupdate"));
		assertEquals(1, occs.size());
		assertEquals("http://www.w3.org/2001/XMLSchema#dateTime", occs.iterator().next().getDatatype().toExternalForm());

		occs = t.getOccurrences(getTopic("http://test.de/coords"));
		assertEquals(1, t.getOccurrences(getTopic("http://test.de/coords")).size());
		// TODO set coord datatype
	}

	private org.tmapi.core.Topic getTopic(String si) {
		return session.getTopicMap().getTopicBySubjectIdentifier(
				session.getTopicMap().createLocator(si));
	}

}
