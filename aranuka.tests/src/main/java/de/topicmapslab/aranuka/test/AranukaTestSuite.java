package de.topicmapslab.aranuka.test;

import java.util.Properties;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.topicmapslab.aranuka.test.annotations.AnnotationTest;
import de.topicmapslab.aranuka.test.associations.AssociationTest;
import de.topicmapslab.aranuka.test.count.CountTest;
import de.topicmapslab.aranuka.test.delete.DeleteTest;
import de.topicmapslab.aranuka.test.occurrencedatatype.OccDatatypeTest;
import de.topicmapslab.aranuka.test.performance.PerformanceTest;
import de.topicmapslab.aranuka.test.pkgparsing.ParseTest;
import de.topicmapslab.aranuka.test.preusker.PreuskerAssociationTest;
import de.topicmapslab.aranuka.test.read.PrefixReadTest;
import de.topicmapslab.aranuka.test.read.ReadTest;
import de.topicmapslab.aranuka.test.rewrite.RewriteTest;
import de.topicmapslab.aranuka.test.supertypes.SupertypeTest;
import de.topicmapslab.aranuka.test.tmql.QueryTest;
import de.topicmapslab.aranuka.test.transitive.TransientTest;
import de.topicmapslab.aranuka.test.update.UpdateTest;
import de.topicmapslab.aranuka.test.write.WriteTest;

/**
 * Test suite for all connectors. 
 * 
 * All connector test suits should inherit from this class and specify 
 * 
 * @author Hannes Niederhausen
 *
 */
@RunWith(Suite.class)
@SuiteClasses({CountTest.class, RewriteTest.class, UpdateTest.class, PreuskerAssociationTest.class, PerformanceTest.class, AssociationTest.class, QueryTest.class, ParseTest.class, AnnotationTest.class, DeleteTest.class, OccDatatypeTest.class, ReadTest.class, PrefixReadTest.class, WriteTest.class, SupertypeTest.class, TransientTest.class})
public class AranukaTestSuite {

	/**
	 * Sets the properties for the aranuka tests
	 * 
	 * @param props the new properties
	 */
	protected static void setProperties (Properties props) {
		AbstractTest.setProperties(props);
	}
	
	/**
	 * Returns the properties
	 * @return
	 */
	protected static Properties getProperties () {
		return AbstractTest.getProperties();
	}
}
