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

public class TopicBinding extends AbstractBinding{

	private static Logger logger = LoggerFactory.getLogger(TopicBinding.class);
	
	// topic type informations
	private String name; // name of the topic type
	private Set<String> identifiers; // identifier of the topic type
	private TopicBinding parent; // supertype

	private Map<Object, Topic> topicObjects; // the instances
	Topic topic; // the topic type represented by this binding
	
	// topic field bindings
	private List<AbstractTopicFieldBinding> fieldBindings;

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

	public Topic persist(TopicMap topicMap, Object topicObject) throws BadAnnotationException{
		
		logger.info("Persist " + topicObject.toString());

		// look in cache
		Topic topic = getTopicObjects(topicObject);

		// create new topic if not exist
		if(topic == null){
			topic = persist(topicMap, topicObject, this);

		}else{
			
			// update existing topic
			updateTopic(topic, topicObject, this);
		}
		
		return topic;
	}
	
	private Topic persist(TopicMap topicMap, Object topicObject, TopicBinding topicBinding) throws BadAnnotationException{
		
		Topic newTopic;
		
		if(topicBinding.parent != null)
			newTopic = persist(topicMap, topicObject, topicBinding.parent);
		else newTopic = topicMap.createTopic();
		
		// store in cache
		addTopicObject(topicObject, newTopic);
		
		// add type
		newTopic.addType(getTopicType(topicMap, this));
		
		// persist fields
		logger.info("Persist " + fieldBindings.size() + " fields.");
		persistFields(newTopic, topicObject, this);

		return newTopic;
	}
	
	private void updateTopic(Topic topic, Object topicObject, TopicBinding binding) throws BadAnnotationException{
		
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
		
		if(binding.topic == null){
			// create topic type
			
			binding.topic = topicMap.createTopic();
			
			// add supertype
			if(binding.parent != null)
				binding.topic.addType(getTopicType(topicMap, binding.parent));
			
			// add name
			binding.topic.createName(binding.name);
			
		}
		
		return binding.topic;
	}

	// getter and setter
	
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

	
	public void addTopicObject(Object object, Topic topic) {
		
		if(this.topicObjects == null)
			this.topicObjects = new HashMap<Object, Topic>();
		
		this.topicObjects.put(object, topic);
	}
		

	public Topic getTopicObjects(Object object) {
		
		if(this.topicObjects == null)
			return null;
		
		return this.topicObjects.get(object);
		
	}

	@Override
	public String toString() {
		return "TopicBinding [fieldBindings=" + fieldBindings
				+ ", identifiers=" + identifiers + ", name=" + name
				+ ", parent=" + parent + ", topic=" + topic + ", topicObjects="
				+ topicObjects + "]";
	}

	
	
}
