/**
 * 
 */
package de.topicmapslab.aranuka.codegen.core.tmcl;

import java.io.File;

import junit.framework.TestCase;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.aranuka.codegen.core.TopicMap2JavaMapper;
import de.topicmapslab.aranuka.codegen.core.code.POJOTypes;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.core.factory.TMCLTopicAnnotationFactory;
import de.topicmapslab.tmcl_loader.TMCLLoader;

/**
 * 
 * @author Hannes Niederhausen
 * 
 */
public class TMCLFactoryTest extends TestCase {
	private TopicMapSystem topicMapSystem;
	private TopicMap topicMap;
	
	@Override
	protected void setUp() throws Exception {
		topicMapSystem = TopicMapSystemFactory.newInstance()
				.newTopicMapSystem();
		topicMap = topicMapSystem
				.createTopicMap("de.topicmapslab.de/aranuka-codegen/schema-test");

		TMCLLoader loader = new TMCLLoader();
		loader.readTMCLSchema(topicMap, new File("src/test/resources/tmclschema.ctm"));
		
	}

	public TopicMapSystem getTopicMapSystem() {
		return topicMapSystem;
	}

	public TopicMap getTopicMap() {
		return topicMap;
	}

	public String getDirectoryPrefix() {
		return "src/test/java/";
	}

	public void testClassFileGeneration() throws TopicMap2JavaMapperException {
		final String packageName = getClass().getPackage().getName()
				+ ".generated";
		TopicMap2JavaMapper mapper = new TopicMap2JavaMapper(
				getTopicMapSystem(), getTopicMap(), packageName,
				getDirectoryPrefix() + packageName.replaceAll("\\.", "/"),
				new TMCLTopicAnnotationFactory(getTopicMap()));
		mapper.run(POJOTypes.SIMPLE_POJO);
	}

}
