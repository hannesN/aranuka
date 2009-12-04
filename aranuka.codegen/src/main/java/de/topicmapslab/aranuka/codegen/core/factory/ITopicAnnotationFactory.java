package de.topicmapslab.aranuka.codegen.core.factory;

import java.util.Set;

import de.topicmapslab.aranuka.codegen.core.definition.TopicAnnotationDefinition;

public interface ITopicAnnotationFactory {

	public abstract Set<TopicAnnotationDefinition> getTopicAnnotationDefinitions();

}