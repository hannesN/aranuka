package de.topicmapslab.aranuka.ontopia.connectors;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource;
import net.ontopia.topicmaps.impl.tmapi2.MemoryTopicMapSystemImpl;
import net.ontopia.topicmaps.impl.tmapi2.TopicMapImpl;
import net.ontopia.topicmaps.impl.tmapi2.TopicMapSystemIF;
import net.ontopia.topicmaps.utils.ctm.CTMTopicMapReader;
import net.ontopia.topicmaps.xml.XTM2TopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.aranuka.connectors.AbstractEngineConnector;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.constants.IAranukaIRIs;

public class OntopiaConnector extends AbstractEngineConnector{
	private TopicMap topicMap;
	private TopicMapSystem topicMapSystem;
	private TopicMapStoreIF store;
	
	private static HashMap<String, String> DB_LOOKUP;
	
	{
		DB_LOOKUP = new HashMap<String, String>();
		
		DB_LOOKUP.put("h2", "h2");
		DB_LOOKUP.put("mysql", "h2");
		DB_LOOKUP.put("postgresql", "h2");
		
	}
	
	// interface methods
	
	public TopicMap createFileTopicMap() {
	
		if(this.topicMap != null)
			return topicMap;
		
		if(getProperty(IProperties.BASE_LOCATOR) == null)
			throw new RuntimeException("Base locator property not specified.");

		try{
			
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			topicMapSystem = factory.newTopicMapSystem();
			this.topicMap = topicMapSystem.createTopicMap(topicMapSystem.createLocator(getProperty(IProperties.BASE_LOCATOR)));
			
			String filename = getProperty(IProperties.FILENAME);
			if(filename != null){
			
				File f = new File(filename);
				
				if(f.exists()){
					
					// load
					if (filename.endsWith(".xtm")) {
						XTMTopicMapReader  reader = new XTMTopicMapReader(new FileInputStream(f), URILocator.create(IAranukaIRIs.ITEM_IDENTIFIER_PREFIX));
						reader.setValidation(false);
						TopicMapIF tm = reader.read();
						this.topicMap = new TopicMapImpl((TopicMapSystemIF) topicMapSystem, tm.getStore());
					} else if (filename.endsWith(".ctm")) {
						CTMTopicMapReader reader = new CTMTopicMapReader(new FileInputStream(f), URILocator.create(IAranukaIRIs.ITEM_IDENTIFIER_PREFIX));
						TopicMapIF tm = reader.read();
						this.topicMap = new TopicMapImpl((TopicMapSystemIF) topicMapSystem, tm.getStore());
					}
				}
			}
			
			return this.topicMap;


		}catch(Exception e){
			
			e.printStackTrace();
			return null;
		}
		
	}
	
	private void removeItemIdentifiers() {
		for (Topic t : topicMap.getTopics()) {
			Set<Locator> iiSet = new HashSet<Locator>(t.getItemIdentifiers());
			for (Locator l : iiSet) {
				if (l.toExternalForm().startsWith(IAranukaIRIs.ITEM_IDENTIFIER_PREFIX))
					t.removeItemIdentifier(l);
			}
		}
	}
	
	public boolean flushFileTopicMap() {

		if(this.topicMap == null)
			return false;
		
		String filename = getProperty(IProperties.FILENAME);
		if(filename == null)
			throw new RuntimeException("Filename property not specified.");
		
		File f = new File(filename);

		removeItemIdentifiers();
		try{
			
			if (filename.endsWith(".xtm")) {
				XTM2TopicMapWriter writer = new XTM2TopicMapWriter(f);
				writer.write(((TopicMapImpl) topicMap).getWrapped());
			} else if (filename.endsWith(".ctm")) {
				// TODO add CTMWriter
//				CTMTopicMapWriter writer = new CTMTopicMapWriter(new FileOutputStream(f), topicMap.getBaseLocator());
//				writer.write(((TopicMapImpl) topicMap).getWrapped());
			} 
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;
		
	}
	
	public TopicMap createDBTopicMap() {
		
		// local var because it's used very often
		String baseLocator = getProperty(IProperties.BASE_LOCATOR);
		if(baseLocator == null)
			throw new RuntimeException("Base locator property not specified.");

		try{
			// creating Properties for Onotpia connections

			RDBMSTopicMapSource source = new RDBMSTopicMapSource(getOntopiaDBProperties());
			source.setSupportsCreate(true);
			
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			topicMapSystem = factory.newTopicMapSystem();
			
			// try to open an existing file
			
			@SuppressWarnings("unchecked")
			Collection<TopicMapReferenceIF> refs = source.getReferences();
			
			for(TopicMapReferenceIF ref:refs){
				
				 this.store = ref.createStore(false);
				
				 if(store.getBaseAddress().getExternalForm().equals(baseLocator)){
					 
					TopicMapIF tm = store.getTopicMap();
					this.topicMap = ((MemoryTopicMapSystemImpl) topicMapSystem).createTopicMap(tm); 
					return this.topicMap;
				 }
				 
				 this.store.close();
				 this.store = null;
			}
			
			// create a new topic map
			
			TopicMapReferenceIF ref = source.createTopicMap(baseLocator, baseLocator);
			
			this.store = ref.createStore(false);
			TopicMapIF tm = this.store.getTopicMap();
			
			this.topicMap = ((MemoryTopicMapSystemImpl) topicMapSystem).createTopicMap(tm); 
			return this.topicMap;
			
		}catch(Exception e){
			
			e.printStackTrace();
			return null;
		}

	}
	
	private Properties getOntopiaDBProperties() {
		Properties prop = new Properties();
		if (getProperty("net.ontopia.topicmaps.impl.rdbms.Database")!=null) {
			// copying the ontopia props into the new property
			prop.setProperty("net.ontopia.topicmaps.impl.rdbms.Database", 
				 getProperty("net.ontopia.topicmaps.impl.rdbms.Database"));
			
			prop.setProperty("net.ontopia.topicmaps.impl.rdbms.ConnectionString",
					"net.ontopia.topicmaps.impl.rdbms.ConnectionString");
			
			prop.setProperty("net.ontopia.topicmaps.impl.rdbms.DriverClass", 
					"net.ontopia.topicmaps.impl.rdbms.DriverClass");
			
			prop.setProperty("net.ontopia.topicmaps.impl.rdbms.UserName", 
				 getProperty("net.ontopia.topicmaps.impl.rdbms.UserName"));
			
			prop.setProperty("net.ontopia.topicmaps.impl.rdbms.Password", 
				 getProperty("net.ontopia.topicmaps.impl.rdbms.Password"));
			
			return prop;
		}
			
		
		prop.setProperty("net.ontopia.topicmaps.impl.rdbms.Database", getProperty(IProperties.DATABASESYSTEM));
		prop.setProperty("net.ontopia.topicmaps.impl.rdbms.ConnectionString", getJDBCString());
		prop.setProperty("net.ontopia.topicmaps.impl.rdbms.DriverClass", 
				getJDBCDriver(getProperty(IProperties.DATABASESYSTEM)));
		
		prop.setProperty("net.ontopia.topicmaps.impl.rdbms.UserName", getProperty(IProperties.DATABASE_LOGIN));
		prop.setProperty("net.ontopia.topicmaps.impl.rdbms.Password", getProperty(IProperties.DATABASE_PASSWORD));
		
		
		return prop;
	}

	protected String getJDBCString() {
		String dbms = getProperty(IProperties.DATABASESYSTEM);
		String host = getProperty(IProperties.DATABASE_HOST);
		String p = getProperty(IProperties.DATABASE_PORT);
		String database = getProperty(IProperties.DATABASE_NAME);
		
		if ("h2".equals(dbms)) {
			return "jdbc:h2:"+host+";MVCC=true";
		}
		
		if ("postgresql".equals(dbms)) {
			if (p==null)
				p = "5432";
			return "jdbc:postgresql://"+host+":"+p+"/"+database;
		}
		if ("mysql".equals(dbms)) {
			if (p==null)
				p = "3306";
			return "jdbc:mysql://"+host+":"+p+"/"+database+"?useUnicode=yes&characterEncoding=utf8&relaxAutoCommit=true";
		}
		
		return dbms;
	}
	
	public boolean flushDBTopicMap() {
	
		if(this.store != null){
			this.store.commit();
			return true;
		}
		
		return false;
		
	}
	
	public boolean flushTopicMap() {
		if ("memory".equals(getProperty(IProperties.BACKEND))) {
			return flushFileTopicMap();
		} else if ("db".equals(getProperty(IProperties.BACKEND))) {
			return flushDBTopicMap();
		}
		return false;
	}

	public TopicMap createTopicMap() {
		String baseLocator = getProperty(IProperties.BASE_LOCATOR);
		if(baseLocator == null)
			throw new RuntimeException("Base locator property not specified.");
		
		if ("memory".equals(getProperty(IProperties.BACKEND))) {
			return createFileTopicMap();
		} else if ("db".equals(getProperty(IProperties.BACKEND))) {
			return createDBTopicMap();
		}
		
		return null;
	}

	public TopicMapSystem getTopicMapSystem() {
		return topicMapSystem;
	}
	
	public void clearTopicMap(TopicMap topicMap) {
		if (topicMap instanceof TopicMapImpl) {
			((TopicMapImpl) topicMap).getWrapped().clear();
			((TopicMapImpl) topicMap).getWrapped().getStore().commit();
		}
	}
}
