/**
 * 
 */
package codemodel.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.aranuka.codegen.core.CodeGenerator;
import de.topicmapslab.tmcl_loader.TMCLLoader;

/**
 * 
 * 
 * @author Hannes Niederhausen
 * 
 */
public class GeneratorTest extends AbstractGeneratorTest {

	TopicMap topicMap;
	File path = null;


	@Before
	public void prepare() throws Exception {
		path = getDir();
		init();
		generateCode();
	}

	@After
	public void shutdown() {
		deleteTestDir();
	}

	public void generateCode() throws IOException {
		new CodeGenerator().generateCode(topicMap, path, "test.model");
	}

	@Test
	public void testNumberOfFiles() {
		File srcDir = new File(path.getAbsolutePath() + "/test/model");
		assertTrue("Number of Files is not 6", srcDir.listFiles().length == 6);

	}

	@Test
	public void testFilenames() {
		File srcDir = new File(path.getAbsolutePath() + "/test/model");
		List<String> names = Arrays.asList(new String[] { "Author.java", "City.java", "Corporation.java",
		        "Document.java", "Person.java", "Project.java" });

		for (File f : srcDir.listFiles()) {
			assertTrue(f.getName() + " is an invallid filename", names.contains(f.getName()));
		}
	}

	@Test
	public void testCompileFiles() {
		File srcDir = new File(path.getAbsolutePath() + "/test/model");
		String classpath = System.getProperty("java.class.path") + ":" + path.getAbsolutePath();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		for (File f : srcDir.listFiles()) {
			int result = compiler.run(null, null, null, f.getAbsolutePath(), "-source", "1.5", "-cp", classpath);
			assertEquals(0, result);
		}
	}

	private void init() throws Exception {
		TopicMapSystem topicMapSystem = TopicMapSystemFactory.newInstance().newTopicMapSystem();
		topicMap = topicMapSystem.createTopicMap("http://www.topicmapslab.de/aranuka-codegen");

		TMCLLoader.readTMCLSchema(topicMap, new File("src/test/resources/tmclschema.ctm"));

	}
}
