/**
 * 
 */
package codemodel.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.net.URI;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

	static TopicMap topicMap;
	static File path = null;


	@BeforeClass
	public static void prepare() throws Exception {
		path = getDir();
		init();
		generateCode();
	}

	@AfterClass
	public static void shutdown() {
	//	deleteTestDir();
	}

	static public void generateCode() throws IOException {
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

	@Test
	public void testPersonClass() throws URISyntaxException {
		String urlString = "file://"+path.getAbsolutePath()+"/";
		
		try { 
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[]{uri}, getClass().getClassLoader());	
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
			Assert.fail("Could not load Person.class");
			
		}
	}
	
	@Test
	public void testCityClass() throws URISyntaxException {
		String urlString = "file://"+path.getAbsolutePath()+"/";
		
		try { 
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[]{uri}, getClass().getClassLoader());	
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
		String urlString = "file://"+path.getAbsolutePath()+"/";
		
		try { 
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[]{uri}, getClass().getClassLoader());	
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
		String urlString = "file://"+path.getAbsolutePath()+"/";
		
		try { 
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[]{uri}, getClass().getClassLoader());	
			Class<?> clazz = loader.loadClass("test.model.Author");
			Assert.assertNotNull(clazz);
			Object o = clazz.getConstructor().newInstance();
			
			Assert.assertNotNull(o);
		} catch (Exception e) {
			Assert.fail("Could not load Author.class");
			
		}
	}
	
	@Test
	public void testProjectClass() throws URISyntaxException {
		String urlString = "file://"+path.getAbsolutePath()+"/";
		
		try { 
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[]{uri}, getClass().getClassLoader());	
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
		String urlString = "file://"+path.getAbsolutePath()+"/";
		
		try { 
			URL uri = new URL(urlString);
			ClassLoader loader = URLClassLoader.newInstance(new URL[]{uri}, getClass().getClassLoader());	
			Class<?> clazz = loader.loadClass("test.model.Document");
			Assert.assertNotNull(clazz);
			Object o = clazz.getConstructor().newInstance();
			
			Assert.assertNotNull(o);
		} catch (Exception e) {
			Assert.fail("Could not load Document.class");
			
		}
	}
	
	static private void init() throws Exception {
		TopicMapSystem topicMapSystem = TopicMapSystemFactory.newInstance().newTopicMapSystem();
		topicMap = topicMapSystem.createTopicMap("http://www.topicmapslab.de/aranuka-codegen");

		TMCLLoader.readTMCLSchema(topicMap, new File("src/test/resources/tmclschema.ctm"));

	}
}
