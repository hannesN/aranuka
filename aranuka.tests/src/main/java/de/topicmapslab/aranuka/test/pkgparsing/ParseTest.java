package de.topicmapslab.aranuka.test.pkgparsing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.exception.AranukaException;

public class ParseTest {

	private static final String PACKAGE_NAME = "de.topicmapslab.aranuka.test.pkgparsing.model";
	private Configuration conf;

	@Before
	public void setUp() throws AranukaException {
		conf = new Configuration();
		conf.addPackage(PACKAGE_NAME);
	}
	
	@Test
	public void testNumberOfClasses() {
		assertEquals(5, conf.getClasses().size());
	}
	
	@Test
	public void testClasses() throws ClassNotFoundException {
		assertEquals(5, conf.getClasses().size());
		
		String[] names = {"Lamp", "Person", "Place", "Thing", "Place$AssocContainer"};
		
		for (String name : names) {
			String qName = PACKAGE_NAME + "." + name;
			getClass();
			Class<?> c = getClass().getClassLoader().loadClass(qName);
			assertNotNull(c);
			assertTrue(conf.getClasses().contains(c));
		}
		
	}
	
}
