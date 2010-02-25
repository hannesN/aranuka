package de.topicmapslab.aranuka.engine;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource;
import net.ontopia.topicmaps.impl.tmapi2.MemoryTopicMapSystemImpl;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;


public class OntopiaDatabaseDriver extends AbstractEngineDriver {
	
	public static final String PROPERTY_FILE = "property_file";
	public static final String BASE_LOCATOR = "base_locator";
	
	private TopicMapStoreIF store;
	private TopicMap topicMap;
	
	// interface methods
	
	public TopicMap getTopicMap() {
	
		if(this.topicMap != null)
			return this.topicMap;

		if(getProperty(BASE_LOCATOR) == null)
			throw new RuntimeException("Base locator property not specified.");
		
		if(getProperty(PROPERTY_FILE) == null)
			throw new RuntimeException("Property file property not specified.");
		
		try{
			
			RDBMSTopicMapSource source = new RDBMSTopicMapSource(getProperty(PROPERTY_FILE));
			source.setSupportsCreate(true);
			
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			TopicMapSystem sys = factory.newTopicMapSystem();
			
			// try to open an existing file
			
			@SuppressWarnings("unchecked")
			Collection<TopicMapReferenceIF> refs = source.getReferences();
			
			for(TopicMapReferenceIF ref:refs){
				
				 this.store = ref.createStore(false);
				
				 if(store.getBaseAddress().getExternalForm().equals(getProperty(BASE_LOCATOR))){
					 
					TopicMapIF tm = store.getTopicMap();
					this.topicMap = ((MemoryTopicMapSystemImpl) sys).createTopicMap(tm); 
					return this.topicMap;
				 }
				 
				 this.store.close();
				 this.store = null;
			}
			
			// create a new topic map
			
			TopicMapReferenceIF ref = source.createTopicMap(getProperty(BASE_LOCATOR), getProperty(BASE_LOCATOR));
			
			this.store = ref.createStore(false);
			TopicMapIF tm = this.store.getTopicMap();
			
			this.topicMap = ((MemoryTopicMapSystemImpl) sys).createTopicMap(tm); 
			return this.topicMap;
			
		}catch(Exception e){
			
			e.printStackTrace();
			return null;
		}

	}
	
	public boolean flushTopicMap() {
	
		if(this.store != null){
			this.store.commit();
			return true;
		}
		
		return false;
		
	}

}
