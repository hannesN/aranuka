/**
 * 
 */
package de.topicmapslab.aranuka.majortom.connector;

import java.io.File;
import java.io.FileOutputStream;
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

		try {
			File f = new File(filename);

			// overwrite existing file, which means we delete the old one
			if (f.exists())
				f.delete();

			FileOutputStream fo = new FileOutputStream(f);

			TopicMapWriter writer = null;

			if (f.getName().endsWith(".xtm")) {
				writer = new XTM2TopicMapWriter(fo, baseLocator, XTMVersion.XTM_2_0);
				((XTM2TopicMapWriter) writer).setPrettify(true);
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
		

	public TopicMap createTopicMap() {
		try {
				String baseLocator = getProperty(IProperties.BASE_LOCATOR);
				TopicMapSystem tms = getTopicMapSystem();
								
				topicMap = tms.createTopicMap(baseLocator);
				
				// load file if it exists
				if ("memory".equals(getProperty(IProperties.BACKEND))) {
					String filename = getProperty(IProperties.FILENAME);
					if (filename !=null) {
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
		if (f.getName().endsWith("ctm")) {
			CTMTopicMapReader reader = new CTMTopicMapReader(topicMap, f, getProperty(IProperties.BASE_LOCATOR));
			reader.read();
			return;
		}
		if (f.getName().endsWith("xtm")) {
			XTMTopicMapReader reader = new XTMTopicMapReader(topicMap, f, getProperty(IProperties.BASE_LOCATOR));
			reader.read();
			return;
		}		
		
	}

	public TopicMapSystem getTopicMapSystem() {
		try {
			TopicMapSystemFactoryImpl fac = new TopicMapSystemFactoryImpl();
			String backend = getProperty(IProperties.BACKEND);
			if ((backend==null) || (backend.equals("memory"))) {
				fac.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, "de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore");
			} else if (backend.equals("db")) {
				fac.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, "de.topicmapslab.majortom.database.store.JdbcTopicMapStore");
				
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_NAME, getProperty(IProperties.DATABASE_NAME));
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_USER, getProperty(IProperties.DATABASE_LOGIN));
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_PASSWORD, getProperty(IProperties.DATABASE_PASSWORD));
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_HOST, getProperty(IProperties.DATABASE_HOST));
				fac.setProperty(JdbcTopicMapStoreProperty.SQL_DIALECT, getDialect());
				
			}
			fac.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
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
		throw new IllegalArgumentException("Argumant is not a MaJorToM TopicMap!");
	}

}
