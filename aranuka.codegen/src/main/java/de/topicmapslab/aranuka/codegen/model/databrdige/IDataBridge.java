package de.topicmapslab.aranuka.codegen.model.databrdige;

import java.util.Set;

import org.tmapi.core.Topic;

import annoTM.core.annotations.IDTYPE;
import de.topicmapslab.aranuka.codegen.core.exception.LazyOperationException;
import de.topicmapslab.tmql4j.common.model.tuplesequence.ITupleSequence;

/**
 * 
 * @author Sven Krosse
 *
 */
public interface IDataBridge {

	public ITupleSequence<?> execute(final String query)
			throws LazyOperationException;

	public Object getField(final Topic topic, final String internalFieldType)
			throws LazyOperationException;

	public Set<String> getIdentifiers(final Topic topic, final IDTYPE idtype)
			throws LazyOperationException;

	public Set<?> getTraversalPlayers(final Topic topic,
			final Class<?> traversalPOJOType, final String otherRoleType)
			throws LazyOperationException;

	public Set<Topic> getAllDatasetByType(final Topic type)
			throws LazyOperationException;

	public Set<Topic> getAllDatasetByType(final String type)
			throws LazyOperationException;

}
