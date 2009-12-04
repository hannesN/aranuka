package de.topicmapslab.aranuka.codegen.layer.base;

import gnu.trove.THashMap;

import java.util.Map;

import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.layer.model.ITopicMapDAO;

/**
 * 
 * @author Sven Krosse
 *
 */
public class TopicMapDAOFactory {

	private static TopicMapDAOFactory factory;
	private final Map<Class<?>, Class<? extends ITopicMapDAO<?>>> daos = new THashMap<Class<?>, Class<? extends ITopicMapDAO<?>>>();
	private final Map<Class<?>, ITopicMapDAO<?>> instances = new THashMap<Class<?>, ITopicMapDAO<?>>();
	private final TopicMap topicMap;

	private TopicMapDAOFactory(final TopicMap topicMap) {
		this.topicMap = topicMap;
	}

	public static TopicMapDAOFactory getInstance(final TopicMap topicMap) {
		if (factory == null) {
			factory = new TopicMapDAOFactory(topicMap);
		}
		return factory;
	}

	public void registerTopicMapDAO(Class<?> pojoClass, Class<? extends ITopicMapDAO<?>> daoClazz
			) {
		daos.put(pojoClass, daoClazz);
	}

	public ITopicMapDAO<?> getTopicMapDAO(Class<?> clazz)
			throws TopicMap2JavaMapperException {
		if (instances.containsKey(clazz)) {
			return instances.get(clazz);
		} else {
			ITopicMapDAO<?> dao = createInstances(clazz);
			instances.put(clazz, dao);
			return dao;
		}
	}

	private ITopicMapDAO<?> createInstances(Class<?> clazz)
			throws TopicMap2JavaMapperException {
		if (daos.containsKey(clazz)) {
			try {
				Class<? extends ITopicMapDAO<?>> daoClass = daos.get(clazz);
				return daoClass.getConstructor(TopicMap.class).newInstance(
						topicMap);
			} catch (Exception e) {
				throw new TopicMap2JavaMapperException(
						"Failed to instanziate dao.");
			}
		}
		throw new TopicMap2JavaMapperException(
				"No ITopicMapDAO registered for given class.");
	}

}
