package de.topicmapslab.aranuka.codegen.core.definition;

import java.util.Set;

import org.tmapi.core.Topic;

/**
 * 
 * @author Sven Krosse
 *
 */
public interface ITopicAnnotationDefinition {

	public Set<? extends IFieldDefinition> getIdAnnotationDefinitions();

	public Set<? extends IFieldDefinition> getNameAnnotationDefinitions();

	public Set<? extends IFieldDefinition> getOccurrenceAnnotationDefinitions();

	public String getName();

	public String getType();

	public String getSubjectIdentifer();

	public Topic getTopic();

}
