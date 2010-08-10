/**
 * 
 */
package codemodel.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.aranuka.codegen.core.CodeGenerator;
import de.topicmapslab.aranuka.codegen.core.exception.InvalidOntologyException;

/**
 * 
 * 
 * @author Hannes Niederhausen
 * 
 */
public class GeneratorTest extends AbstractGeneratorTest {

	static TopicMap topicMap;
	static File path = null;
//	private static TopicMapSystemFactory topicMapSystemFactory;
	private static TopicMapSystem topicMapSystem;

	@BeforeClass
	public static void prepare() throws Exception {
		path = getDir();
		init();
		generateCode();
	}

	@AfterClass
	public static void shutdown() {
		 deleteTestDir();
	}

	static public void generateCode() throws IOException, InvalidOntologyException {
		new CodeGenerator().generateCode(topicMapSystem, topicMap, path, "test.model");
		File srcDir = new File(path.getAbsolutePath() + "/test/model");
		String classpath = System.getProperty("java.class.path") + ":" + path.getAbsolutePath();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		for (File f : srcDir.listFiles()) {
			int result = compiler.run(null, null, null, f.getAbsolutePath(), "-source", "1.5", "-cp", classpath);
			assertEquals(0, result);
		}
	}

	@Test
	public void testNumberOfFiles() {
		File srcDir = new File(path.getAbsolutePath() + "/test/model");
		assertEquals("Number of Files ", 8, srcDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".java");
			}
		}).length);

	}

	@Test
	public void testFilenames() {
		File srcDir = new File(path.getAbsolutePath() + "/test/model");
		List<String> names = Arrays.asList(new String[] { "Author.java", "City.java", "Corporation.java",
		        "Document.java", "Person.java", "Project.java", "DiplomaThesis.java", "Student.java" });

		for (File f : srcDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".java");
			}
		})) {
			assertTrue(f.getName() + " is an invallid filename", names.contains(f.getName()));
		}
	}

	
	@Test
	public void testPersonClass() throws URISyntaxException {
		String urlString = "file://" + path.getAbsolutePath() + "/";

		try {
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[] { uri }, getClass().getClassLoader());
			Class<?> clazz = loader.loadClass("test.model.Person");

			Object o = clazz.getConstructor().newInstance();

			PropertyUtils.setProperty(o, "firstname", "Hans");
			assertEquals("Hans", PropertyUtils.getProperty(o, "firstname"));

			PropertyUtils.setProperty(o, "lastname", "Meyer");
			assertEquals("Meyer", PropertyUtils.getProperty(o, "lastname"));

			Set<String> middleNames = new HashSet<String>();
			middleNames.add("Gerd");
			middleNames.add("Max");
			PropertyUtils.setProperty(o, "middlename", middleNames);

			assertEquals(middleNames, PropertyUtils.getProperty(o, "middlename"));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Could not load Person.class");
			

		}
	}

	@Test
	public void testCityClass() throws URISyntaxException {
		String urlString = "file://" + path.getAbsolutePath() + "/";

		try {
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[] { uri }, getClass().getClassLoader());
			Class<?> clazz = loader.loadClass("test.model.City");
			Assert.assertNotNull(clazz);
			Object o = clazz.getConstructor().newInstance();

			Assert.assertNotNull(o);
		} catch (Exception e) {
			Assert.fail("Could not load City.class");

		}
	}

	@Test
	public void testCorporationClass() throws URISyntaxException {
		String urlString = "file://" + path.getAbsolutePath() + "/";

		try {
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[] { uri }, getClass().getClassLoader());
			Class<?> clazz = loader.loadClass("test.model.Corporation");
			Assert.assertNotNull(clazz);
			Object o = clazz.getConstructor().newInstance();

			Assert.assertNotNull(o);
		} catch (Exception e) {
			Assert.fail("Could not load Corporation.class");

		}
	}

	@Test
	public void testAuthorClass() throws URISyntaxException {
		String urlString = "file://" + path.getAbsolutePath() + "/";

		try {
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[] { uri }, getClass().getClassLoader());
			Class<?> clazz = loader.loadClass("test.model.Author");
			Assert.assertNotNull(clazz);

			assertEquals("test.model.Person", clazz.getSuperclass().getName());
			Object o = clazz.getConstructor().newInstance();

			Assert.assertNotNull(o);
		} catch (Exception e) {
			Assert.fail("Could not load Author.class");
		}
	}

	@Test
	public void testProjectClass() throws URISyntaxException {
		String urlString = "file://" + path.getAbsolutePath() + "/";

		try {
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[] { uri }, getClass().getClassLoader());
			Class<?> clazz = loader.loadClass("test.model.Project");
			Assert.assertNotNull(clazz);
			Object o = clazz.getConstructor().newInstance();

			Assert.assertNotNull(o);
		} catch (Exception e) {
			Assert.fail("Could not load Project.class");

		}
	}

	@Test
	public void testDocumentClass() throws URISyntaxException {
		String urlString = "file://" + path.getAbsolutePath() + "/";

		try {
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[] { uri }, getClass().getClassLoader());
			Class<?> clazz = loader.loadClass("test.model.Document");
			Assert.assertNotNull(clazz);
			Object o = clazz.getConstructor().newInstance();

			Assert.assertNotNull(o);
		} catch (Exception e) {
			Assert.fail("Could not load Document.class");

		}
	}

	@Test
	public void testStudentClass() throws URISyntaxException {
		String urlString = "file://" + path.getAbsolutePath() + "/";

		try {
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[] { uri }, getClass().getClassLoader());
			Class<?> clazz = loader.loadClass("test.model.Student");
			Assert.assertNotNull(clazz);
			Class<?> clazz2 = loader.loadClass("test.model.DiplomaThesis");
			Assert.assertNotNull(clazz2);
			try {
				Field f = clazz.getDeclaredField("diplomaThesis");
				assertEquals(clazz2, f.getType());
			} catch (NoSuchFieldException e) {
				Assert.fail("Field diploma theses doesn't exists");
			}
		} catch (Exception e) {
			Assert.fail("Could not load class:"+e.getMessage());

		}

	}

	static private void init() throws Exception {
		topicMapSystem = TopicMapSystemFactory.newInstance().newTopicMapSystem();
		topicMap = topicMapSystem.createTopicMap("http://www.topicmapslab.de/aranuka-codegen");

		File file = new File("src/test/resources/tmclschema.xtm");
		XTMTopicMapReader reader = new XTMTopicMapReader(topicMap, file);
		reader.read();
	}
}
