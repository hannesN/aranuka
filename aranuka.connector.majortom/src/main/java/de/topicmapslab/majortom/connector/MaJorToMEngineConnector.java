/**
 * 
 */
package de.topicmapslab.majortom.connector;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map.Entry;

import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapix.io.TopicMapWriter;
import org.tmapix.io.XTM20TopicMapWriter;

import de.topicmapslab.aranuka.connectors.AbstractEngineConnector;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.ctm.writer.core.CTMTopicMapWriter;
import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;

/**
 * @author Hannes Niederhausen
 *
 */
public class MaJorToMEngineConnector extends AbstractEngineConnector {
	private TopicMap topicMap;
	private TopicMapSystem tmSystem;

	public boolean flushTopicMap() {

		if (this.topicMap == null)
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
				((XTM20TopicMapWriter) writer).setPrettify(true);
			} else {
				writer = new CTMTopicMapWriter(fo, baseLocator);

				for (Entry<String, String> e : getPrefixMap().entrySet()) {
					if (e.getKey().equals(IProperties.BASE_LOCATOR))
						continue;
					((CTMTopicMapWriter) writer).setPrefix(e.getKey(),
							e.getValue());
				}
			}
			writer.write(this.topicMap);

			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		

	/* (non-Javadoc)
	 * @see de.topicmapslab.aranuka.connectors.IEngineConnector#getTopicMap()
	 */
	public TopicMap getTopicMap() {
		try {
			if (topicMap == null) {
				String baseLocator = getProperty(IProperties.BASE_LOCATOR);
				topicMap = getTopicMapSystem().createTopicMap(baseLocator);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return topicMap;
	}

	/* (non-Javadoc)
	 * @see de.topicmapslab.aranuka.connectors.IEngineConnector#getTopicMapSystem()
	 */
	public TopicMapSystem getTopicMapSystem() {
		try {
			if (tmSystem==null)
				tmSystem = new TopicMapSystemFactoryImpl().newTopicMapSystem();
			return tmSystem;
		} catch (TMAPIException e) {
			throw new RuntimeException(e);
		}
	}

}
