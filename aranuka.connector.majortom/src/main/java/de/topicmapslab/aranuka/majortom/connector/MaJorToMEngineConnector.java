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
package de.topicmapslab.aranuka.majortom.connector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapix.io.CTMTopicMapReader;
import org.tmapix.io.TopicMapWriter;
import org.tmapix.io.XTM2TopicMapWriter;
import org.tmapix.io.XTMTopicMapReader;
import org.tmapix.io.XTMVersion;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.connectors.AbstractEngineConnector;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.constants.IAranukaIRIs;
import de.topicmapslab.ctm.writer.core.CTMTopicMapWriter;
import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStoreProperty;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * @author Hannes Niederhausen
 * 
 */
public class MaJorToMEngineConnector extends AbstractEngineConnector {
	private TopicMap topicMap;

	public MaJorToMEngineConnector() {
	}

	public MaJorToMEngineConnector(Configuration conf) {
		setConfiguration(conf);
	}

	public boolean flushTopicMap() {

		if (this.topicMap == null)
			return false;

		String baseLocator = getProperty(IProperties.BASE_LOCATOR);
		if (baseLocator == null)
			throw new RuntimeException("Base locator property not specified.");

		String filename = getProperty(IProperties.FILENAME);
		if (filename == null)
			return false;

		File temporaryFile = null;
		try {
			// create a temporary file so that write failures won't affect the
			// original file
			temporaryFile = new File(filename + ".tmp");

			FileOutputStream fo = new FileOutputStream(temporaryFile);

			TopicMapWriter writer = null;

			if (filename.endsWith(".xtm")) {
				writer = new XTM2TopicMapWriter(fo,
						IAranukaIRIs.ITEM_IDENTIFIER_PREFIX, XTMVersion.XTM_2_1);
				((XTM2TopicMapWriter) writer).setPrettify(true);
			} else {
				writer = new CTMTopicMapWriter(fo, baseLocator);

				for (Entry<String, String> e : getPrefixMap().entrySet()) {
					((CTMTopicMapWriter) writer).setPrefix(e.getKey(),
							e.getValue());
				}
			}
			writer.write(this.topicMap);
			fo.close();
			
			// overwrite the original file
			if (temporaryFile.exists()) {
				String sCurrentLine = "";
				try
			    {
			        BufferedReader br = new BufferedReader(new FileReader(filename + ".tmp"));
			        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

			        while ((sCurrentLine = br.readLine()) != null)
			        {
			            bw.write(sCurrentLine);
			            bw.newLine();
			        }

			        br.close();
			        bw.close();

//					temporaryFile.delete();
			    }
			    catch (Exception e)
			    {
					throw new RuntimeException(e);
			    }
			}

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

	public TopicMap createTopicMap() {
		try {
			String baseLocator = getProperty(IProperties.BASE_LOCATOR);
			TopicMapSystem tms = getTopicMapSystem();

			topicMap = tms.createTopicMap(baseLocator);

			// load file if it exists
			if ("memory".equals(getProperty(IProperties.BACKEND))) {
				String filename = getProperty(IProperties.FILENAME);
				if (filename != null) {
					File f = new File(filename);
					if (f.exists()) {
						readTopicMap(f);
					}
				}
			}
			return topicMap;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void readTopicMap(File f) throws IOException {
		FileInputStream inStream = new FileInputStream(f);
		if (f.getName().endsWith("ctm")) {
			CTMTopicMapReader reader = new CTMTopicMapReader(topicMap,
					inStream, IAranukaIRIs.ITEM_IDENTIFIER_PREFIX);
			reader.read();
		}
		if (f.getName().endsWith("xtm")) {
			XTMTopicMapReader reader = new XTMTopicMapReader(topicMap,
					inStream, IAranukaIRIs.ITEM_IDENTIFIER_PREFIX);
			reader.read();
		}
		inStream.close();
		return;
	}

	public TopicMapSystem getTopicMapSystem() {
		try {
			TopicMapSystemFactoryImpl fac = new TopicMapSystemFactoryImpl();
			String backend = getProperty(IProperties.BACKEND);
			if ((backend == null) || (backend.equals("memory"))) {
				fac.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS,
						"de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore");
			} else if (backend.equals("db")) {
				fac.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS,
						"de.topicmapslab.majortom.database.store.JdbcTopicMapStore");

				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_NAME,
						getProperty(IProperties.DATABASE_NAME));
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_USER,
						getProperty(IProperties.DATABASE_LOGIN));
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_PASSWORD,
						getProperty(IProperties.DATABASE_PASSWORD));
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_HOST,
						getProperty(IProperties.DATABASE_HOST));
				fac.setProperty(JdbcTopicMapStoreProperty.SQL_DIALECT,
						getDialect());

			}
			fac.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION,
					false);

			if (getProperty(IProperties.DISABLE_HISTORY) != null) {
				fac.setFeature(FeatureStrings.SUPPORT_HISTORY, false);
			}

			return fac.newTopicMapSystem();
		} catch (TMAPIException e) {
			throw new RuntimeException(e);
		}
	}

	private String getDialect() {
		String dbms = getProperty(IProperties.DATABASESYSTEM);

		if ("postgresql".equals(dbms))
			return "POSTGRESQL99";

		return null;
	}

	public void clearTopicMap(TopicMap topicMap) {
		if (topicMap instanceof ITopicMap) { // should always be ture
			((ITopicMap) topicMap).clear();
			return;
		}
		throw new IllegalArgumentException(
				"Argumant is not a MaJorToM TopicMap!");
	}

}
