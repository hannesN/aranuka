package de.topicmapslab.aranuka.codegen.core.tmqlbridge;

import gnu.trove.THashSet;

import java.util.Set;

import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import annoTM.core.annotations.IDTYPE;
import de.topicmapslab.aranuka.codegen.core.exception.LazyOperationException;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.core.utility.TypeUtility;
import de.topicmapslab.aranuka.codegen.model.databrdige.IDataBridge;
import de.topicmapslab.aranuka.codegen.properties.PropertyLoader;
import de.topicmapslab.tmql4j.common.core.exception.TMQLRuntimeException;
import de.topicmapslab.tmql4j.common.core.process.TMQLRuntime;
import de.topicmapslab.tmql4j.common.core.query.TMQLQuery;
import de.topicmapslab.tmql4j.common.model.tuplesequence.ITupleSequence;

/**
 * 
 * @author Sven Krosse
 *
 */
public final class TMQLDataBridge implements IDataBridge {

	private final TMQLRuntime runtime;

	public TMQLDataBridge(final PropertyLoader propertyLoader)
			throws TopicMap2JavaMapperException {
		Set<TopicMap> topicMaps = new THashSet<TopicMap>();
		topicMaps.add(propertyLoader.getTopicMap());
		runtime = new TMQLRuntime(propertyLoader.getTopicMapSystem(), topicMaps);
	}

	public ITupleSequence<?> execute(final String query)
			throws LazyOperationException {
		try {
			runtime.run(new TMQLQuery(query));
			return (ITupleSequence<?>) runtime
					.getStoredValue(TMQLRuntime.TMQL_RUNTIME_INTERPRETER_RESULT);
		} catch (TMQLRuntimeException e) {
			throw new LazyOperationException("execution of tmql-query failed!");
		}
	}

	public Object getField(Topic topic, String internalFieldType)
			throws LazyOperationException {
		try {
			ITupleSequence<?> results = execute(TypeUtility.getLocator(topic)
					.getReference()
					+ " >> characteristics " + internalFieldType + " [0]");
			Object obj = results.iterator().next();
			if (obj instanceof Name) {
				return ((Name) obj).getValue();
			} else if (obj instanceof DatatypeAware) {
				return ((DatatypeAware) obj).getValue();
			} else {
				throw new LazyOperationException("invalid result type");
			}
		} catch (Exception e) {
			throw new LazyOperationException(e);
		}
	}

	public Set<String> getIdentifiers(final Topic topic, final IDTYPE idtype)
			throws LazyOperationException {
		try {
			String query = TypeUtility.getLocator(topic).getReference()
					+ " >> ";
			switch (idtype) {
			case ITEM_IDENTIFIER:
				query += "item";
				break;
			case SUBJECT_IDENTIFIER:
				query += "indicators";
				break;
			case SUBJECT_LOCATOR:
				query += "locators";
				break;
			}

			Set<String> identifiers = new THashSet<String>();
			for (Object obj : execute(query)) {
				if (obj instanceof Locator) {
					identifiers.add(((Locator) obj).getReference());
				}
			}
			return identifiers;
		} catch (Exception e) {
			throw new LazyOperationException(e);
		}
	}

	public Set<?> getTraversalPlayers(final Topic topic,
			final Class<?> traversalPOJOType, final String otherRoleType)
			throws LazyOperationException {
		try {
			Set<Object> traversal = new THashSet<Object>();
			for (Object obj : execute(TypeUtility.getLocator(topic)
					.getReference()
					+ " >> traverse " + otherRoleType)) {
				if (obj instanceof Topic) {
					Topic traversalPlayer = (Topic) obj;
					traversal.add(traversalPOJOType.getConstructor(Topic.class)
							.newInstance(traversalPlayer));
				}
			}
			return traversal;
		} catch (Exception e) {
			throw new LazyOperationException(e);
		}
	}

	public Set<Topic> getAllDatasetByType(final Topic type)
			throws LazyOperationException {
		try {
			return getAllDatasetByType(TypeUtility.getLocator(type)
					.getReference());
		} catch (Exception e) {
			throw new LazyOperationException(e);
		}
	}

	public Set<Topic> getAllDatasetByType(final String type)
			throws LazyOperationException {
		try {
			Set<Topic> instances = new THashSet<Topic>();
			for (Object obj : execute(type + " >> instances")) {
				if (obj instanceof Topic) {
					instances.add((Topic) obj);
				}
			}
			return instances;
		} catch (Exception e) {
			throw new LazyOperationException(e);
		}
	}

}
