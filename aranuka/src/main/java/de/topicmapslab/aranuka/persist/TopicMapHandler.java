package de.topicmapslab.aranuka.persist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.binding.AbstractClassBinding;
import de.topicmapslab.aranuka.binding.AbstractFieldBinding;
import de.topicmapslab.aranuka.binding.IdBinding;
import de.topicmapslab.aranuka.binding.TopicBinding;
import de.topicmapslab.aranuka.enummerations.IdType;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.utils.TopicMapsUtils;

// handles interaction with the topic map, i.e. creating topics and associations, updating, etc.
public class TopicMapHandler {

	private static Logger logger = LoggerFactory.getLogger(TopicMapHandler.class);
	
	private Configuration config; // the configuration
	private BindingHandler bindingHandler; // the binding handler
	private TopicMap topicMap; // the topic map
	private Map<Object, Topic> topicCache;
	
	// --[ public methods ]------------------------------------------------------------------------------
	
	public TopicMapHandler(Configuration config){

		this.config = config;
	}
	
	public void invokeBinding()  throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		getBindingHandler().createBindingsForAllClasses();
		
//		// test
//		getBindingHandler().printBindings();
	}
	
	/// TODO persist only topics or association container as well?
	public void persist(Object topicObject) throws IOException, BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		List<Object> topicToPersist = new ArrayList<Object>();
				
		topicToPersist.add(topicObject);
		persistTopics(topicToPersist);
	}
	
	
	public TopicMap getTopicMap() throws IOException /// TODO change exception type?
	{
		try{
			
			if(this.topicMap == null)
				this.topicMap = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap(this.config.getBaseLocator());
			
			return this.topicMap;
		}
		catch (Exception e) {
			throw new IOException("Could not create topic map (" + e.getMessage() + ")");
		}
	}
	
	// --[ private methods ]-------------------------------------------------------------------------------

	private void persistTopics(List<Object> topicObjects) throws IOException, BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		Iterator<Object> itr = topicObjects.iterator();
		
	    while(itr.hasNext()){
	    	
	    	Object topicObject = itr.next();
	    	
	    	if(getTopicFromCache(topicObjects) == null)
	    	{
		    	// check
		    	if(!this.config.getClasses().contains(topicObject.getClass()))
		    		throw new ClassNotSpecifiedException("The class " + topicObject.getClass().getName() + " is not registered.");
		    	// get binding
		    	TopicBinding binding = (TopicBinding)getBindingHandler().getBinding(topicObject.getClass());
		    	// create topic
		    	persistTopic(topicObject, topicObjects, binding);
		    	// remove from list
		    	itr.remove();
	    	
	    	}else{
	    		
	    		/// TODO update topic
	    	}
	    }
	}
	
	private void updateTopic(Topic topic, Object topicObject, TopicBinding binding){
		
	}
	
	// modifies the topic subject identifier to represent the current java object
	// used for create new topic as well
	private void updateSubjectIdentifier(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
		
		Set<String> newSubjectIdentifier = getIdentifier(topicObject, binding, IdType.SUBJECT_IDENTIFIER);
		
		Map<Locator,Boolean> actualSubjectIdentifier = addFlaggs(topic.getSubjectIdentifiers());
		
		for(String si:newSubjectIdentifier){
			
			boolean found = false;
			
			for(Map.Entry<Locator, Boolean> entry:actualSubjectIdentifier.entrySet()){
	
				if(entry.getKey().toExternalForm() == si){
					entry.setValue(true); // set to found
					found = true;
					break;
				}
			}
			
			if(!found){
				// add new identifier
				topic.addSubjectIdentifier(getTopicMap().createLocator(si));
			}
			
		}
		
		// remove obsolete identifier
		for(Map.Entry<Locator, Boolean> entry:actualSubjectIdentifier.entrySet()){
			
			if(!entry.getValue())
				topic.removeSubjectIdentifier(entry.getKey());
		}
	}
	
	private void updateSubjectLocator(Topic topic, Object topicObject, TopicBinding binding){
		
	}

	private void updateItemIdentifier(Topic topic, Object topicObject, TopicBinding binding){
	
	}
	
	// modifies the topic names to represent the current java object
	// used for create new topic as well
	private void updateNames(){
		
	}
	
	// modifies the topic occurrences to represent the current java object
	// used for create new topic as well
	private void updateOccurrences(){
		
	}
	
	// modifies the topic associations to represent the current java object
	// used for create new topic as well
	private void updateAssociations(){
		
	}
	
	// adds flaggs to an set, returns an empty map of the set was null
	private <T extends Object> Map<T, Boolean> addFlaggs(Set<T> set){
		
		Map<T, Boolean> map = new HashMap<T, Boolean>();
		
		if(set == null)
			return map;
		
		for(T obj:set)
			map.put(obj, false);

		return map;
	}
	
	private Topic persistTopic(Object topicObject, List<Object> topicObjects, TopicBinding binding) throws IOException, BadAnnotationException {
		
		Topic newTopic = createTopicByIdentifier(topicObject, binding);

		// add topic to cache
		addTopicToCache(newTopic, topicObject);
		
		// update identifier
		
		// update names
		
		// update occurrences
		
		// update associations
		
		return newTopic;
	}
	
	private Topic createTopicByIdentifier(Object topicObject, TopicBinding binding) throws IOException, BadAnnotationException {
		
		// get subject identifier
		Set<String> subjectIdentifier = getIdentifier(topicObject, binding, IdType.SUBJECT_IDENTIFIER);
		// get subject locator
		Set<String> subjectLocator = getIdentifier(topicObject, binding, IdType.SUBJECT_LOCATOR);
		// get item identifier
		Set<String> itemIdentifier = getIdentifier(topicObject, binding, IdType.ITEM_IDENTIFIER);
		
		Topic topic = null;

		// try to find the topic
		
		topic = getTopicBySubjectIdentifier(subjectIdentifier);
		
		if(topic == null)
			topic = getTopicBySubjectLocator(subjectLocator);
			
		if(topic == null)
			topic = getTopicByItemIdentifier(itemIdentifier);
			
		if(topic == null){
			
			// create new topic
			topic = createNewTopic(subjectIdentifier, subjectLocator, itemIdentifier);
			
		}
		
//		// add identifier TODO move this to an update identifier method
//		
//		for(String si:subjectIdentifier)
//			topic.addSubjectIdentifier(getTopicMap().createLocator(si));
//		
//		
//		for(String sl:subjectLocator)
//			topic.addSubjectLocator(getTopicMap().createLocator(sl));
//		
//		
//		for(String ii:itemIdentifier)
//			topic.addItemIdentifier(getTopicMap().createLocator(ii));
		
	
		return topic;
	}
	
	// tries to find an existing topic by a list of subject identifiers
	private Topic getTopicBySubjectIdentifier(Set<String> subjectIdentifier)throws IOException{
		
		Topic topic = null;
		
		for(String si:subjectIdentifier){
			
			topic = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(si));
			
			if(topic != null)
				return topic;
		}
		
		return null;
	}
	
	// tries to find an existing topic by a list of subject locators
	private Topic getTopicBySubjectLocator(Set<String> subjectLocator)throws IOException{
		
		Topic topic = null;
		
		for(String sl:subjectLocator){
			
			topic = getTopicMap().getTopicBySubjectLocator(getTopicMap().createLocator(sl));
			
			if(topic != null)
				return topic;
		}
		
		return null;
	}
	
	// tries to find an existing topic by a list of item identifiers
	private Topic getTopicByItemIdentifier(Set<String> itemIdentifier)throws IOException{
		
		Construct construct = null;
		
		for(String ii:itemIdentifier){
			
						
			construct = getTopicMap().getConstructByItemIdentifier(getTopicMap().createLocator(ii));

			if(construct != null){
				
				if(!(construct instanceof Topic))
					throw new RuntimeException("The item identifier " + ii + " is already used by a non topic construct.");
				
				return (Topic)construct;
				
			}
				
		}
		
		return null;
	}
	
	// used recursively
	@SuppressWarnings("unchecked")
	private Set<String> getIdentifier(Object topicObject, TopicBinding binding, IdType type){
		
		Set<String> identifier = null;
				
		if(binding.getParent() != null)
			identifier = getIdentifier(topicObject, binding.getParent(), type);
		else identifier = new HashSet<String>();

		// create base locator
		String baseLocator = TopicMapsUtils.resolveURI("base_locator:", this.config.getPrefixMap()) + topicObject.getClass().getName().replaceAll("\\.", "/") + "/";
		
		// add all subject identifier
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof IdBinding && ((IdBinding)afb).getIdtype() == type){
				
				if(afb.getValue(topicObject) != null){ // ignore empty values

					if(((IdBinding)afb).isArray()){
					
						for (Object obj:(Object[])((IdBinding)afb).getValue(topicObject)){
							
							if (obj instanceof String){
								identifier.add(obj.toString());
							}else{
								identifier.add(baseLocator + obj.toString());
							}
						}
					
					}else if(((IdBinding)afb).isCollection()){
						
						for (Object obj:(Collection<Object>)((IdBinding)afb).getValue(topicObject)){
							
							if (obj instanceof String){
								identifier.add(obj.toString());
							}else{
								identifier.add(baseLocator + obj.toString());
							}
						}
		
					}else{
						if (((IdBinding)afb).getValue(topicObject) instanceof String){
							identifier.add(((IdBinding)afb).getValue(topicObject).toString());
						}else{
							identifier.add(baseLocator + ((IdBinding)afb).getValue(topicObject).toString());
						}
					}
				}
			}
		}
		
		return identifier;
	}
	
	private Topic createNewTopic(Set<String> subjectIdentifier, Set<String> subjectLocator, Set<String> itemIdentifier) throws IOException, BadAnnotationException{
		
		Topic topic = null;
		
		if(!subjectIdentifier.isEmpty()){
			topic = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(subjectIdentifier.iterator().next()));
			
		}else if(!subjectLocator.isEmpty()){
			topic = getTopicMap().createTopicBySubjectLocator(getTopicMap().createLocator(subjectLocator.iterator().next()));
			
		}else if(!itemIdentifier.isEmpty()){
			topic = getTopicMap().createTopicByItemIdentifier(getTopicMap().createLocator(itemIdentifier.iterator().next()));
			
		}else{
			throw new BadAnnotationException("Topic class has no identifier");
		}

		return topic;
	}
	
	
	// private getter and setter
	
	private void addTopicToCache(Topic topic, Object object){
		
		if(this.topicCache == null)
			this.topicCache = new HashMap<Object, Topic>();
		
		this.topicCache.put(object, topic);
		
	}
	
	private Topic getTopicFromCache(Object object){
		
		if(this.topicCache == null)
			return null;
		
		return this.topicCache.get(object);
	}
	
	private BindingHandler getBindingHandler(){
		
		if(bindingHandler == null)
			bindingHandler = new BindingHandler(this.config);
		
		return bindingHandler;
		
	}

	
	
}
