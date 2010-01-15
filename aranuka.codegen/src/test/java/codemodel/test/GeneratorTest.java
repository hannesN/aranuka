/**
 * 
 */
package codemodel.test;

import java.io.File;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.aranuka.codegen.core.CodeGenerator;
import de.topicmapslab.tmcl_loader.TMCLLoader;

/**
 * @author niederhausen
 * 
 */
public class GeneratorTest extends AbstractGenerationTest {

	TopicMap topicMap;

	public static void main(String[] args) {
		try {
			new GeneratorTest().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() throws Exception {
		init();

		new CodeGenerator().generateCode(topicMap, getDir(), "test.model");
	}

	private void init() throws Exception {
		TopicMapSystem topicMapSystem = TopicMapSystemFactory.newInstance()
				.newTopicMapSystem();
		topicMap = topicMapSystem
				.createTopicMap("de.topicmapslab.de/aranuka-codegen/schema-test");

		TMCLLoader.readTMCLSchema(topicMap, new File("src/test/resources/tmclschema.ctm"));

	}
}
