package de.topicmapslab.aranuka.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.utils.HashUtil;

public class TopicBinding extends AbstractClassBinding{

	private static Logger logger = LoggerFactory.getLogger(TopicBinding.class);
	
	private String name; // name of the topic type
	private Set<String> identifiers; // identifier of the topic type
	private TopicBinding parent; // super type

	private Map<Object, Topic> cache; // the instances
	Topic topicType; // the topic type represented by this binding

	private List<AbstractTopicFieldBinding> fieldBindings; // topic field bindings
	
	// --[ public methods ]------------------------------------------------------------------------------
	
	/**
	 * Persists an topic annotated java object in the topic map.
	 */
	public Topic persist(TopicMap topicMap, Object topicObject) throws BadAnnotationException{

		logger.info("Persist " + topicObject.toString());

		// look in cache
		Topic topic = getTopicFromCache(topicObject);

		// create new topic if not exist
		if(topic == null){
			
			topic = persist(topicMap, topicObject, this);

		}else{

			if(!isUpdated(topic))
				updateTopic(topic, topicObject, this);// update existing topic
		}
		
		return topic;
	}
	
	// getter and setter
	
	public List<AbstractTopicFieldBinding> getFieldBindings() {
		
		if (this.fieldBindings==null)
			return Collections.emptyList();
		return this.fieldBindings;
	}
	
	public void addFieldBinding(AbstractTopicFieldBinding fieldBinding) {
		
		if (this.fieldBindings==null)
			this.fieldBindings = new ArrayList<AbstractTopicFieldBinding>();
		
		this.fieldBindings.add(fieldBinding);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setParent(TopicBinding parent) {
		this.parent = parent;
	}
	
	public TopicBinding getParent(){
		return this.parent;
	}
	
	public void addIdentifier(String id) {
		if (identifiers==null)
			identifiers = HashUtil.createSet();
		identifiers.add(id);
	}

	// --[ private methods ]-------------------------------------------------------------------------------

	private Topic persist(TopicMap topicMap, Object topicObject, TopicBinding topicBinding) throws BadAnnotationException{
		
		Topic newTopic;
		
		if(topicBinding.parent != null)
			newTopic = persist(topicMap, topicObject, topicBinding.parent);
		else newTopic = topicMap.createTopic();
		
		// store in cache
		addTopicToCache(topicObject, newTopic);
		
		// add type
		newTopic.addType(getTopicType(topicMap, this));
		
		// persist fields
		logger.info("Persist " + fieldBindings.size() + " fields.");
		persistFields(newTopic, topicObject, this);

		return newTopic;
	}

	private void updateTopic(Topic topic, Object topicObject, TopicBinding binding) throws BadAnnotationException{
		
		setToUpdated(topic);
		
		if(binding.parent != null)
			updateTopic(topic, topicObject, binding.parent);
		
		// persist fields
		for(AbstractTopicFieldBinding fb:binding.fieldBindings)
			fb.persist(topic, topicObject);
		
	}
	
	private void persistFields(Topic topic, Object topicObject, TopicBinding binding) throws BadAnnotationException{
		
		// persist parent fields
		if(binding.parent != null)
			persistFields(topic, topicObject, binding.parent);
		
		// persist own fields
		for(AbstractTopicFieldBinding fb:binding.fieldBindings)
			fb.persist(topic, topicObject);
	}
	
	
	/**
	 * Returns the topic type represented by the topic binding. If not exist, a new topic will be created.
	 * 
	 * @param topicMap
	 * @param binding
	 * @return
	 */
	private Topic getTopicType(TopicMap topicMap, TopicBinding binding){
		
		if(binding.topicType == null){
			// create topic type
			
			binding.topicType = topicMap.createTopic();
			
			// add supertype
			if(binding.parent != null)
				binding.topicType.addType(getTopicType(topicMap, binding.parent));
			
			// add name
			binding.topicType.createName(binding.name);
			
		}
		
		return binding.topicType;
	}

	private void addTopicToCache(Object object, Topic topic) {
		
		if(this.cache == null)
			this.cache = new HashMap<Object, Topic>();
		
		this.cache.put(object, topic);
	}

	private Topic getTopicFromCache(Object object) {
		
		if(this.cache == null)
			return null;
		
		return this.cache.get(object);
		
	}


	
}
