package de.topicmapslab.aranuka.engine;

import java.io.File;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapReader;
import org.tmapix.io.TopicMapReader;
import org.tmapix.io.XTM20TopicMapReader;


public class TinyTiMDriver extends AbstractEngineDriver {

	private TopicMap topicMap;

	public boolean flushTopicMap() {
		// TODO muss in extra projekt
	}

	public TopicMap getTopicMap() {

		if (this.topicMap != null)
			return topicMap;

		if (getProperty(IProperties.BASE_LOCATOR) == null)
			throw new RuntimeException("Base locator property not specified.");

		try {
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			TopicMapSystem sys = factory.newTopicMapSystem();
			this.topicMap = sys.createTopicMap(sys
					.createLocator(getProperty(IProperties.BASE_LOCATOR)));

			if (getProperty(IProperties.FILENAME) != null) {

				File f = new File(getProperty(IProperties.FILENAME));

				if (f.exists()) {
					// read
					TopicMapReader reader = null;
					if (f.getName().endsWith(".xtm")) {
						reader = new XTM20TopicMapReader(this.topicMap, f);
					} else  {
						reader = new CTMTopicMapReader(this.topicMap, f);
					}
					
					reader.read();
				}
			}

			return this.topicMap;

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

	}

}
