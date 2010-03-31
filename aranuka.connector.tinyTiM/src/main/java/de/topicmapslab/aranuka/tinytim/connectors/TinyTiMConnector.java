package de.topicmapslab.aranuka.tinytim.connectors;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
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
import de.topicmapslab.ctm.writer.core.CTMTopicMapWriter;


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
		
		// TODO remove me after TMAPIX works properly
		removeItemIdentifiers();
		
		try {
			File f = new File(filename);

			// overwrite existing file, which means we delete the old one
			if (f.exists())
				f.delete();

			FileOutputStream fo = new FileOutputStream(f);

			TopicMapWriter writer = null;

			if (f.getName().endsWith(".xtm")) {
				writer = new XTM20TopicMapWriter(fo, baseLocator);
				((XTM20TopicMapWriter)writer).setPrettify(true);
			} else {
				writer = new CTMTopicMapWriter(fo, baseLocator);
				
				for (Entry<String, String> e : getPrefixMap().entrySet()) {
					if (e.getKey().equals(IProperties.BASE_LOCATOR))
						continue;
			 		((CTMTopicMapWriter)writer).setPrefix(e.getKey(), e.getValue());
				}
			}
			writer.write(this.topicMap);

			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void removeItemIdentifiers() {
		for (Topic t : topicMap.getTopics()) {
			Set<Locator> iiSet = new HashSet<Locator>(t.getItemIdentifiers());
			for (Locator l : iiSet) {
				if (l.toExternalForm().startsWith("file:"))
					t.removeItemIdentifier(l);
			}
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
