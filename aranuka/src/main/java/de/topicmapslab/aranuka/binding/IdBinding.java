package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.annotations.IDTYPE;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.utils.TopicMapsUtils;

public class IdBinding extends AbstractFieldBinding {

	private static Logger logger = LoggerFactory.getLogger(TopicBinding.class);
	
	private IDTYPE idtype;
	
//	private Set<IdentifierIdent> cache;
//	
//	private class IdentifierIdent{
//		
//		String uri;
//		Topic topic;
//		boolean changed;
//		
//	}
	
	// --[ public methods ]--------------------------------------------------------------------------------
	
	public IdBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}
	
//	@SuppressWarnings("unchecked")
//	@Deprecated
//	public void persist(Topic topic, Object topicObject) throws BadAnnotationException{
//		
//		if(this.getValue(topicObject) == null)
//			return;
//		
//		String baseLocator = TopicMapsUtils.resolveURI("base_locator:", getPrefixMap()) + topicObject.getClass().getName().replaceAll("\\.", "/") + "/";
//						
//		// create identifier dependent of field type
//		
//		if(this.isArray())
//			
//			for (Object obj:(Object[])this.getValue(topicObject))
//				createIdentifier(topic, obj, baseLocator);
//			
//		else if(this.isCollection())
//				
//			for (Object obj:(Collection<Object>)this.getValue(topicObject))
//				createIdentifier(topic, obj, baseLocator);
//
//		else 
//			createIdentifier(topic, this.getValue(topicObject), baseLocator);
//		
//		// check if obsolete names exist
//		removeObsoleteIdentifier(topic);
//		
//	}

	// getter and setter
	
	@Override
	public String toString() {
		return "IdBinding [idtype=" + idtype + "]";
	}

	public void setIdtype(IDTYPE idtype) {
		this.idtype = idtype;
	}
	
	public IDTYPE getIdtype() {
		return idtype;
	}
	
	// --[ private methods ]-------------------------------------------------------------------------------
	
//	private void createIdentifier(Topic topic, Object obj, String baseLocator) throws BadAnnotationException{
//
//		String locator = null;
//		if (obj instanceof String)
//			locator = obj.toString();
//		else
//			locator = baseLocator + obj.toString();
//
//		logger.info("Add identifier " + locator);
//		
//		if(!isInCache(locator))
//		{
//
//			if(this.getIdtype() == IDTYPE.ITEM_IDENTIFIER)
//				topic.addItemIdentifier(topic.getTopicMap().createLocator(locator));
//			
//			else if(this.getIdtype() == IDTYPE.SUBJECT_IDENTIFIER)
//				topic.addSubjectIdentifier(topic.getTopicMap().createLocator(locator));
//			
//			else if(this.getIdtype() == IDTYPE.SUBJECT_LOCATOR)
//				topic.addSubjectLocator(topic.getTopicMap().createLocator(locator));
//			
//			else
//				throw new BadAnnotationException("Unkonwn IDTYPE.");
//			
//			addIdentifierToCache(locator, topic);
//			
//		}
//		
//		setChanged(locator);
//		
//	}
//	
//	private void addIdentifierToCache(String uri, Topic topic){ 
//		
//		IdentifierIdent i = new IdentifierIdent();
//		i.uri = uri;
//		i.topic = topic;
//		i.changed = true;
//		
//		if(this.cache == null)
//			this.cache = new HashSet<IdentifierIdent>();
//		
//		this.cache.add(i);
//	}
//	
//	private void setChanged(String uri)
//	{
//		if(this.cache == null)
//			return;
//		
//		for(IdentifierIdent i:cache)
//		{
//			if(i.uri == uri)
//				i.changed = true;
//		}
//	}
//
//	private void unsetChanded(){
//		
//		if(this.cache == null)
//			return;
//		
//		for(IdentifierIdent i:cache)
//			i.changed = false;
//	}
//	
//	private void removeObsoleteIdentifier(Topic topic){
//		
//		if(this.cache == null)
//			return;
//		
//		Set<IdentifierIdent> obsoleteEntries = new HashSet<IdentifierIdent>();
//		
//		for(IdentifierIdent i:cache){
//			
//			if(i.topic.equals(topic) && i.changed == false){
//				
//				logger.info("Remove obsolte identifier '" + i.uri + "'.");
//				
//				if(idtype == IDTYPE.ITEM_IDENTIFIER)
//					i.topic.removeItemIdentifier(i.topic.getTopicMap().createLocator(i.uri));
//				
//				if(idtype == IDTYPE.SUBJECT_IDENTIFIER)
//					i.topic.removeSubjectIdentifier(i.topic.getTopicMap().createLocator(i.uri));
//				
//				if(idtype == IDTYPE.SUBJECT_LOCATOR)
//					i.topic.removeSubjectLocator(i.topic.getTopicMap().createLocator(i.uri));
//				
//				obsoleteEntries.add(i);
//			}
//		}
//
//		for(IdentifierIdent i:obsoleteEntries)
//			this.cache.remove(i);
//		
//		// set flags back to false
//		unsetChanded();
//	}
//	
//	private boolean isInCache(String uri){
//		
//		if(this.cache == null)
//			return false;
//		
//		for(IdentifierIdent i:this.cache){
//			
//			if(i.uri == uri)
//				return true;
//			
//		}
//		
//		return false;
//		
//	}

}
