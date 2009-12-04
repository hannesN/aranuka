package de.topicmapslab.aranuka.codegen.core;

import java.io.File;

import junit.framework.TestCase;

import org.tinytim.mio.XTM20TopicMapReader;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

/**
 * 
 * @author Sven Krosse
 *
 */
public abstract class AranukaCodeGenTestCase extends TestCase {

	private TopicMapSystem topicMapSystem;
	private TopicMap topicMap;

	@Override
	protected void setUp() throws Exception {
		topicMapSystem = TopicMapSystemFactory.newInstance()
				.newTopicMapSystem();
		topicMap = topicMapSystem
				.createTopicMap("de.topicmapslab.de/aranuka-codegen/toytm-test");

		XTM20TopicMapReader reader = new XTM20TopicMapReader(topicMap,
				new File("src/test/resources/toyTM.xtm2"));
		reader.read();
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

}
