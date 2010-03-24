package de.topicmapslab.aranuka.tinytim.connectors;

import java.io.File;
import java.io.FileOutputStream;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapReader;
import org.tmapix.io.TopicMapReader;
import org.tmapix.io.TopicMapWriter;
import org.tmapix.io.XTM20TopicMapReader;
import org.tmapix.io.XTM20TopicMapWriter;

import de.topicmapslab.aranuka.connectors.AbstractEngineConnector;
import de.topicmapslab.aranuka.connectors.IProperties;


public class TinyTiMConnector extends AbstractEngineConnector {

	private TopicMap topicMap;
	private TopicMapSystem topicMapSystem;

	public boolean flushTopicMap() {
		if (this.topicMap==null)
			return false;
		
		String baseLocator = getProperty(IProperties.BASE_LOCATOR);
		if (baseLocator == null)
			throw new RuntimeException("Base locator property not specified.");

		String filename = getProperty(IProperties.FILENAME);
		if (filename == null)
			return false;
		
		try {
			File f = new File(filename);

			// overwrite existing file, which means we delete the old one
			if (f.exists())
				f.delete();

			FileOutputStream fo = new FileOutputStream(f);

			TopicMapWriter writer = null;

			if (f.getName().endsWith(".xtm")) {
				writer = new XTM20TopicMapWriter(fo, baseLocator);
			} else {
//				TODO CTMWriter einbauen
//				writer = new CTMTopicMapWriter(fo, baseLocator);
//				for (Entry<String, String> e : getPrefixMap().entrySet()) {
//					if (e.getKey().equals(IProperties.BASE_LOCATOR))
//						continue;
//			 		((CTMTopicMapWriter)writer).addPrefix(e.getKey(), e.getValue());
//				}
			}
			writer.write(this.topicMap);

			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public TopicMapSystem getTopicMapSystem() {
		return topicMapSystem;
	}

	public TopicMap getTopicMap() {

		if (this.topicMap != null)
			return topicMap;

		if (getProperty(IProperties.BASE_LOCATOR) == null)
			throw new RuntimeException("Base locator property not specified.");

		try {
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			topicMapSystem = factory.newTopicMapSystem();
			this.topicMap = topicMapSystem.createTopicMap(topicMapSystem
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
