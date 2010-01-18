package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;


public class NameBinding extends AbstractFieldBinding {

	private static Logger logger = LoggerFactory.getLogger(NameBinding.class);
	
	private String nameType;
	
//	private Map<NameIdent, Name> cache;
//	
//	private class NameIdent{
//		
//		public String name;
//		public Topic topic;
//		public boolean changed;
//	}
	
	// --[ public methods ]--------------------------------------------------------------------------------
	
	public NameBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}
	
//	@SuppressWarnings("unchecked")
//	@Deprecated
//	public void persist(Topic topic, Object topicObject){
//		
//		if(this.getValue(topicObject) == null)
//			return;
//		
//		if(this.isArray())
//			
//			for (Object obj:(Object[])this.getValue(topicObject))
//				createName(topic, obj.toString());
//			
//		else if(this.isCollection())
//				
//			for (Object obj:(Collection<Object>)this.getValue(topicObject))
//				createName(topic, obj.toString());
//
//		else 
//			createName(topic, this.getValue(topicObject).toString());
//
//		// check if obsolete names exist
//		removeObsoleteNames(topic);
//	}

	// getter and setter
	
	@Override
	public String toString() {
		return "NameBinding [nameType=" + nameType + "]";
	}

	public String getNameType() {
		return nameType;
	}

	public void setNameType(String nameIdentifier) {
		this.nameType = nameIdentifier;
	}
	
	// --[ private methods ]-------------------------------------------------------------------------------
	
//	private void createName(Topic topic, String name){
//
//		Name n = getNameFromCache(name, topic);
//		
//		if(n == null){
//			
//			logger.info("Add new name '" + name + "'.");
//			
//			// create new name
//			n = topic.createName(name, getScope(topic.getTopicMap()));
//			n.setType(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(this.nameType)));
//			
//			// add name construct to cache
//			addNameToCache(name, topic, n);
//		}else logger.info("Name '" + name + "' already exist.");
//		
//		// set the flag
//		setChanged(n);
//		
//	}
//
//	private void addNameToCache(String name, Topic topic, Name topicName){ 
//		
//		NameIdent n = new NameIdent();
//		n.name = name;
//		n.topic = topic;
//		n.changed = true;
//		
//		if(this.cache == null)
//			this.cache = new HashMap<NameIdent, Name>();
//		
//		this.cache.put(n, topicName);
//	}
//
//	private void setChanged(Name name)
//	{
//		if(this.cache == null)
//			return;
//		
//		for(Map.Entry<NameIdent, Name> entry:cache.entrySet()){
//			
//			if(entry.getValue().equals(name))
//				entry.getKey().changed = true;
//		}
//	}
//
//	private void unsetChanded(){
//		
//		if(this.cache == null)
//			return;
//		
//		for(Map.Entry<NameIdent, Name> entry:cache.entrySet())
//			entry.getKey().changed = false;
//		
//	}
//
//	private Name getNameFromCache(String name, Topic topic){
//		
//		if(this.cache == null)
//			return null;
//		
//		for(Map.Entry<NameIdent, Name> entry:cache.entrySet()){
//			
//			if(entry.getKey().name == name && entry.getKey().topic.equals(topic)){
//				return entry.getValue();								
//			}
//		}
//		
//		return null;
//	}
//
//	private void removeObsoleteNames(Topic topic){
//		
//		if(this.cache == null)
//			return;
//		
//		Set<NameIdent> obsoleteEntries = new HashSet<NameIdent>();
//		
//		for(Map.Entry<NameIdent, Name> entry:this.cache.entrySet()){
//			
//			if(entry.getKey().topic.equals(topic) && entry.getKey().changed == false){
//				
//				logger.info("Remove obsolte name '" + entry.getKey().name + "'.");
//				
//				entry.getValue().remove();
//				
//				obsoleteEntries.add(entry.getKey());
//			}
//		}
//
//		for(NameIdent nc:obsoleteEntries)
//			this.cache.remove(nc);
//		
//		// set flags back to false
//		unsetChanded();
//	}

}


