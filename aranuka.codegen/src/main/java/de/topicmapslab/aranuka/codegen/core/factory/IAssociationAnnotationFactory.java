package de.topicmapslab.aranuka.codegen.core.factory;

import java.util.Set;

import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.codegen.core.definition.AssociationAnnotationDefinition;

public interface IAssociationAnnotationFactory {

	public abstract Set<AssociationAnnotationDefinition> getAssociationAnnotationDefinitions(
			final Topic roleType);

}