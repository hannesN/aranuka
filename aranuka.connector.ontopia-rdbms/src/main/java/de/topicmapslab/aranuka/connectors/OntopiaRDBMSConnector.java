package de.topicmapslab.aranuka.connectors;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource;
import net.ontopia.topicmaps.impl.tmapi2.MemoryTopicMapSystemImpl;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;


public class OntopiaRDBMSConnector extends AbstractEngineConnector {
	

	
	private TopicMapStoreIF store;
	private TopicMap topicMap;
	
	// interface methods
	
	public TopicMap getTopicMap() {
	
		if(this.topicMap != null)
			return this.topicMap;

		// local var because it's used very often
		String baseLocator = getProperty(IProperties.BASE_LOCATOR);
		if(baseLocator == null)
			throw new RuntimeException("Base locator property not specified.");
		
		
		try{
			
			RDBMSTopicMapSource source = new RDBMSTopicMapSource(getProperties());
			source.setSupportsCreate(true);
			
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			TopicMapSystem sys = factory.newTopicMapSystem();
			
			// try to open an existing file
			
			@SuppressWarnings("unchecked")
			Collection<TopicMapReferenceIF> refs = source.getReferences();
			
			for(TopicMapReferenceIF ref:refs){
				
				 this.store = ref.createStore(false);
				
				 if(store.getBaseAddress().getExternalForm().equals(baseLocator)){
					 
					TopicMapIF tm = store.getTopicMap();
					this.topicMap = ((MemoryTopicMapSystemImpl) sys).createTopicMap(tm); 
					return this.topicMap;
				 }
				 
				 this.store.close();
				 this.store = null;
			}
			
			// create a new topic map
			
			TopicMapReferenceIF ref = source.createTopicMap(baseLocator, baseLocator);
			
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
