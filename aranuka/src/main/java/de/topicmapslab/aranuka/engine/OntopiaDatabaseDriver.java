package de.topicmapslab.aranuka.engine;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource;
import net.ontopia.topicmaps.impl.tmapi2.MemoryTopicMapSystemImpl;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;


public class OntopiaDatabaseDriver implements IEngineDriver {
	
	private String databasePropertyFile;
	private String baseLocator;
	
	private TopicMapStoreIF store;
	
	private TopicMap topicMap;
	
	// interface methods
	
	public TopicMap getTopicMap() {
	
		if(this.topicMap != null)
			return this.topicMap;

		return createDatabaseTopicMap();

	}
	
	public boolean flushTopicMap() {
	
		if(this.store != null)
			this.store.commit();
		
		return true;
		
	}
		
	// other methods
		
	public OntopiaDatabaseDriver(String baseLocator, String propertyFile) {

		if(baseLocator == null)
			throw new RuntimeException("Base Locator must not be null.");
		
		if(propertyFile == null)
			throw new RuntimeException("The property file must not be null.");
		
		File f = new File(propertyFile);
		
		if(!f.exists())
			throw new RuntimeException("The property file dosn't exist.");
		
		
		this.baseLocator = baseLocator;
		this.databasePropertyFile = propertyFile;
		
	}
	
	public void deleteTopicMap(){
		
		if(this.store != null){
			
			// topic map already open
			
			this.store.close();
			this.store.delete(true);
			
			this.topicMap = null;
			this.store = null;
		}
		
		RDBMSTopicMapSource source = new RDBMSTopicMapSource(databasePropertyFile);
		source.setSupportsDelete(true);
		
		// try to open an existing file
		
		@SuppressWarnings("unchecked")
		Collection<TopicMapReferenceIF> refs = source.getReferences();
		
		for(TopicMapReferenceIF ref:refs){
			
			TopicMapStoreIF store;
			
			try{
				
				store = ref.createStore(false);
				
			}catch(IOException e){
				return;
			}
			
			 if(store.getBaseAddress().getExternalForm().equals(this.baseLocator)){
				 
				store.delete(true);
				store.close();

				break;
			 }
		}
	}
	
	private TopicMap createDatabaseTopicMap(){
		
		try{
			
			RDBMSTopicMapSource source = new RDBMSTopicMapSource(databasePropertyFile);
			source.setSupportsCreate(true);
			
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			TopicMapSystem sys = factory.newTopicMapSystem();
			
			// try to open an existing file
			
			@SuppressWarnings("unchecked")
			Collection<TopicMapReferenceIF> refs = source.getReferences();
			
			for(TopicMapReferenceIF ref:refs){
				
				 this.store = ref.createStore(false);
				
				 if(store.getBaseAddress().getExternalForm().equals(this.baseLocator)){
					 
					TopicMapIF tm = store.getTopicMap();
					this.topicMap = ((MemoryTopicMapSystemImpl) sys).createTopicMap(tm); 
					return this.topicMap;
				 }
				 
				 this.store.close();
				 this.store = null;
			}
			
			// create a new topic map
			
			TopicMapReferenceIF ref = source.createTopicMap(this.baseLocator, this.baseLocator);
			
			this.store = ref.createStore(false);
			TopicMapIF tm = this.store.getTopicMap();
			
			this.topicMap = ((MemoryTopicMapSystemImpl) sys).createTopicMap(tm); 
			return this.topicMap;
			
		}catch(Exception e){
			
			e.printStackTrace();
			return null;
		}
	}
	
}
