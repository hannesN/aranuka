/**
 * 
 */
package de.topicmapslab.aranuka.persist;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.tmapi.core.Topic;

/**
 * 
 * Cache for the topic2object conversion
 * 
 * @author Hannes Niederhausen
 *
 */
public class AranukaCache {

	private Map<Object, Topic> objectMap;
	private Map<Topic, Object> topicMap;
	
	public void addPair(Object instance, Topic topic) {
		if (objectMap == null) {
			objectMap = new HashMap<Object, Topic>();
			topicMap = new HashMap<Topic, Object>();
		}
		objectMap.put(instance, topic);
		topicMap.put(topic, instance);
	}
	
	public void removePair(Object instance, Topic topic) {
		if (objectMap != null) {
			objectMap.remove(instance);
			topicMap.remove(topic);
		}
	}
	
	public void clear() {
		objectMap = null;
		topicMap = null;
	}
	
	public Topic getTopic(Object instance) {
		return getObjectMap().get(instance);
	}
	
	public Object getInstance(Topic topic) {
		return getTopicMap().get(topic);
	}
	
	/**
	 * @return the objectMap
	 */
	private Map<Object, Topic> getObjectMap() {
		if (objectMap==null)
			return Collections.emptyMap();
		return objectMap;
	}
	
	/**
	 * @return the topicMap
	 */
	private Map<Topic, Object> getTopicMap() {
		if (topicMap==null)
			return Collections.emptyMap();
		return topicMap;
	}
	
	public String getTopic2ObjectMapDump() {
		StringBuilder builder = new StringBuilder();
		for (Entry<Topic, Object> e : getTopicMap().entrySet()) {
			builder.append(e.getKey().toString());
			builder.append(" = ");
			builder.append(e.getValue().toString());
			builder.append(System.getProperty("line.separator"));
		}
		
		return builder.toString();
	}
}
