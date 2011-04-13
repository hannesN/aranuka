/**
 * Copyright 2010-2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Set;

import model.Address;
import model.Person;
import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.AranukaException;


/**
 * Main class for the small example.
 * 
 * @author Hannes Niederhausen
 *
 */
public class Test {

	public static void main(String[] args) throws AranukaException {
		// create configuration with tinyTiM connector
		Configuration conf = new Configuration();
		
		// set classname of connector
		conf.setProperty(IProperties.CONNECTOR_CLASS, "de.topicmapslab.aranuka.tinytim.connectors.TinyTiMConnector");
		
		// set backend - not needed for tinytimn but necessary for majortom and ontopia
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
		Set<Person> persons = session.getAll(Person.class);
		
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
