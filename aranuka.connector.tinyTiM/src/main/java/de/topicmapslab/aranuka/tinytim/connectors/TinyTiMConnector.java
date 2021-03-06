/**
 * Copyright 2010-2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.topicmapslab.aranuka.tinytim.connectors;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapReader;
import org.tmapix.io.TopicMapReader;
import org.tmapix.io.TopicMapWriter;
import org.tmapix.io.XTM2TopicMapWriter;
import org.tmapix.io.XTMTopicMapReader;
import org.tmapix.io.XTMVersion;

import de.topicmapslab.aranuka.connectors.AbstractEngineConnector;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.constants.IAranukaIRIs;
import de.topicmapslab.ctm.writer.core.CTMTopicMapWriter;

/**
 * Connector fot the tinyTiM Topic Maps engine
 * @author Hannes Niederhausen
 *
 */
public class TinyTiMConnector extends AbstractEngineConnector {

	private TopicMap topicMap;
	private TopicMapSystem topicMapSystem;

	public boolean flushTopicMap() {
		if (this.topicMap == null)
			return false;

		String baseLocator = getProperty(IProperties.BASE_LOCATOR);
		if (baseLocator == null)
			throw new RuntimeException("Base locator property not specified.");

		String filename = getProperty(IProperties.FILENAME);
		if (filename == null)
			return false;

		removeItemIdentifiers();

		File temporaryFile = null;
		try {
			// create a temporary file so that write failures won't affect the
			// original file
			temporaryFile = new File(filename + "$");

			FileOutputStream fo = new FileOutputStream(temporaryFile);

			TopicMapWriter writer = null;

			if (filename.endsWith(".xtm")) {
				writer = new XTM2TopicMapWriter(fo,
						IAranukaIRIs.ITEM_IDENTIFIER_PREFIX, XTMVersion.XTM_2_1);
				((XTM2TopicMapWriter) writer).setPrettify(true);
			} else {
				writer = new CTMTopicMapWriter(fo,
						IAranukaIRIs.ITEM_IDENTIFIER_PREFIX);

				for (Entry<String, String> e : getPrefixMap().entrySet()) {
					if (e.getKey().equals(IProperties.BASE_LOCATOR))
						continue;
					((CTMTopicMapWriter) writer).setPrefix(e.getKey(),
							e.getValue());
				}
			}
			writer.write(this.topicMap);

			temporaryFile.renameTo(new File(filename));

			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (temporaryFile.exists()) {
				// clean up the temporary file
				temporaryFile.delete();
			}

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

	public TopicMap createTopicMap() {
		return createTopicMap(true);
	}

	private TopicMap createTopicMap(boolean loadFile) {

		if (getProperty(IProperties.BASE_LOCATOR) == null)
			throw new RuntimeException("Base locator property not specified.");

		try {
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			topicMapSystem = factory.newTopicMapSystem();
			this.topicMap = topicMapSystem.createTopicMap(topicMapSystem
					.createLocator(getProperty(IProperties.BASE_LOCATOR)));

			if ((loadFile) && (getProperty(IProperties.FILENAME) != null)) {

				File f = new File(getProperty(IProperties.FILENAME));

				if (f.exists()) {
					// read
					TopicMapReader reader = null;
					if (f.getName().endsWith(".xtm")) {
						reader = new XTMTopicMapReader(this.topicMap, f,
								IAranukaIRIs.ITEM_IDENTIFIER_PREFIX);
					} else {
						reader = new CTMTopicMapReader(this.topicMap, f,
								IAranukaIRIs.ITEM_IDENTIFIER_PREFIX);
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

	/**
	 * {@inheritDoc}
	 */
	public void clearTopicMap(TopicMap topicMap) {
		topicMap.clear();
	}

}
