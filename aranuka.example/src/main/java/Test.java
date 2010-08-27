import java.util.Set;

import model.Address;
import model.Person;
import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapException;
import de.topicmapslab.aranuka.exception.TopicMapIOException;
import de.topicmapslab.aranuka.exception.TopicMapInconsistentException;
import de.topicmapslab.aranuka.tinytim.connectors.TinyTiMConnector;


/**
 * Main class for the small example.
 * 
 * @author Hannes Niederhausen
 *
 */
public class Test {

	public static void main(String[] args) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapException, TopicMapIOException, TopicMapInconsistentException {
		// create configuration with tinyTiM connector
		Configuration conf = new Configuration();
		
		// set classname of connector
		conf.setProperty(IProperties.CONNECTOR_CLASS, TinyTiMConnector.class.getName());
		
		// set backend - not needed for tinytimn but necessary for majortom and ontopia (soon)
		conf.setProperty(IProperties.BACKEND, "memory");
		
		// add classes to map
		conf.addClass(Person.class);
		conf.addClass(Address.class);
		
		// set base lacotor which identifies the topic map in the engine
		conf.setProperty(IProperties.BASE_LOCATOR, "http://aranuka.example.org/");
		
		// set filename for de/serialisation
		conf.setProperty(IProperties.FILENAME, "/tmp/test.ctm");
	
		// add prefix used in QNames of annotations
		conf.addPrefix("ex", "http://aranuka.example.org/");
		
		// add some names to the used types
		conf.addName("ex:person", "Person");
		conf.addName("ex:firstname", "First Name");
		conf.addName("ex:lastname", "Surname");
		conf.addName("ex:lives", "lives");
		conf.addName("ex:habitant", "Habitant");
		conf.addName("ex:place", "Place");
		
		conf.addName("ex:address", "Address");
		conf.addName("ex:zipcode", "Zip Code");
		conf.addName("ex:street", "Street");
		conf.addName("ex:city", "City");
		conf.addName("ex:number", "House Number");
		
		
		// get the session
		Session session = conf.getSession(false);
		
		// try getting all persons in the topic map
		Set<Object> persons = session.getAll(Person.class);
		
		// persons.size() == 0 at the first start 1 else
		System.out.println(persons.size());
		
		Person p = createPerson();
		session.persist(p);
		
		session.flushTopicMap();
	}

	private static Person createPerson() {
		Address a = new Address();
		a.setId(1);
		a.setZipCode("00815");
		a.setCity("Example City");
		a.setStreet("Example Street");
		a.setNumber("1");
		
		Person p = new Person();
		p.setId("ex:max");
		p.setFirstName("Max");
		p.setLastName("Powers");
		p.setAddress(a);
		
		return p;
	}
}
