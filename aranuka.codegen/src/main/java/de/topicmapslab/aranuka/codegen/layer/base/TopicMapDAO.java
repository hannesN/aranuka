package de.topicmapslab.aranuka.codegen.layer.base;

import gnu.trove.THashSet;

import java.lang.reflect.ParameterizedType;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.layer.model.ITopicMapDAO;
import de.topicmapslab.aranuka.codegen.model.databrdige.IDataBridge;
import de.topicmapslab.aranuka.codegen.properties.PropertyLoader;

/**
 * 
 * @author Sven Krosse
 *
 */
public abstract class TopicMapDAO<T> implements ITopicMapDAO<T> {

	private final Class<? extends T> clazz;
	private final TopicMap topicMap;
	private final IDataBridge databridge;

	@SuppressWarnings("unchecked")
	protected TopicMapDAO(TopicMap topicMap) throws TopicMap2JavaMapperException{
		clazz = (Class<? extends T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		this.topicMap = topicMap;
		this.databridge = PropertyLoader.getInstance().getDataBridge();
	}

	public T find(Locator locator) throws TopicMap2JavaMapperException {
		Topic topic = this.topicMap.getTopicBySubjectIdentifier(locator);
		if (topic == null) {
			Construct construct = topicMap
					.getConstructByItemIdentifier(locator);
			if (construct != null && construct instanceof Topic) {
				topic = (Topic) construct;
			} else {
				topic = this.topicMap.getTopicBySubjectLocator(locator);
			}
		}

		if (topic == null) {
			return null;
		} else {
			return toInstance(topic);
		}
	}

	public Set<T> retrieve() throws TopicMap2JavaMapperException {
		Set<T> results = new THashSet<T>();
		for (Topic topic : databridge.getAllDatasetByType(getType())) {
			results.add(toInstance(topic));
		}
		return results;
	}

	private T toInstance(Topic t) throws TopicMap2JavaMapperException {
		try {
			return clazz.getConstructor(Topic.class).newInstance(t);
		} catch (Exception e) {
			throw new TopicMap2JavaMapperException(e);
		}
	}

	public abstract String getType();

	public Class<?> getDAOClass() {
		return clazz;
	}

	public TopicMap getTopicMap() {
		return topicMap;
	}
}
