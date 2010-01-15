package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

public class OccurrenceBinding extends AbstractTopicFieldBinding {

	private static Logger logger = LoggerFactory.getLogger(OccurrenceBinding.class);
	
	private String occurrenceType;
	private String dataType;

	private Map<OccurrenceIdent, Occurrence> cache;
	
	private class OccurrenceIdent{
		
		public String value;
		public Topic topic;
		boolean changed;
		
	}
		
	public OccurrenceBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}
	
	private void addOccurrenceToCache(String value, Topic topic, Occurrence occurrence){
		
		OccurrenceIdent o = new OccurrenceIdent();
		o.value = value;
		o.topic = topic;
		o.changed = true;
		
		if(this.cache == null)
			this.cache = new HashMap<OccurrenceIdent, Occurrence>();
		
		this.cache.put(o, occurrence);
	}
	
	private void setChanged(Occurrence occurrence)
	{
		if(this.cache == null)
			return;
		
		for(Map.Entry<OccurrenceIdent, Occurrence> entry:cache.entrySet()){
			
			if(entry.getValue().equals(occurrence))
				entry.getKey().changed = true;
		}
	}
	
	private void unsetChanded(){
		
		if(this.cache == null)
			return;
		
		for(Map.Entry<OccurrenceIdent, Occurrence> entry:cache.entrySet())
			entry.getKey().changed = false;
		
	}
	
	private Occurrence getOccurrenceFromCache(String value, Topic topic){
		
		if(this.cache == null)
			return null;
		
		for(Map.Entry<OccurrenceIdent, Occurrence> entry:cache.entrySet()){
			
			if(entry.getKey().value == value && entry.getKey().topic.equals(topic)){
				return entry.getValue();								
			}
		}
		
		return null;
	}
	
	private void removeObsoleteOccurrence(Topic topic){
		
		if(this.cache == null)
			return;
		
		Set<OccurrenceIdent> obsoleteEntries = new HashSet<OccurrenceIdent>();
		
		for(Map.Entry<OccurrenceIdent, Occurrence> entry:this.cache.entrySet()){
			
			if(entry.getKey().topic.equals(topic) && entry.getKey().changed == false){
				
				logger.info("Remove obsolte occurrence '" + entry.getKey().value + "'.");
				
				entry.getValue().remove();
				
				obsoleteEntries.add(entry.getKey());
			}
		}

		for(OccurrenceIdent o:obsoleteEntries)
			this.cache.remove(o);
		
		// set flags back to false
		unsetChanded();
	}

	@SuppressWarnings("unchecked")
	public void persist(Topic topic, Object topicObject) {

		if(this.getValue(topicObject) == null)
			return;
		
		if (this.isArray())

			for (Object obj : (Object[])this.getValue(topicObject))
				createOccurrence(topic, obj.toString());

		else if (this.isCollection())

			for (Object obj : (Collection<Object>) this.getValue(topicObject))
				createOccurrence(topic, obj.toString());

		else
			createOccurrence(topic, this.getValue(topicObject).toString());

		// check if obsolete occurrences exist
		removeObsoleteOccurrence(topic);
		
	}
	
	private void createOccurrence(Topic topic, String value){
		
		Occurrence occ = getOccurrenceFromCache(value, topic);
		
		if(occ == null)
		{
			logger.info("Add new occurrrence '" + value + "'.");
			occ = topic.createOccurrence(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(this.occurrenceType)), value, topic.getTopicMap().createLocator(this.dataType), getScope(topic.getTopicMap()));
			
			// add occurrence to cache
			addOccurrenceToCache(value, topic, occ);
			
		}else logger.info("Occurrence '" + value + "' already exist.");
		
		// set the flag
		setChanged(occ);
		
	}

	public String getOccurrenceType() {
		return occurrenceType;
	}

	public void setOccurrenceType(String occurrenceTypeIdentifier) {
		this.occurrenceType = occurrenceTypeIdentifier;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return "OccurrenceBinding [occurrenceType=" + occurrenceType + "]";
	}

}
