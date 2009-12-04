package de.topicmapslab.aranuka.codegen.core;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;

import de.topicmapslab.aranuka.codegen.core.code.CodeBuffer;
import de.topicmapslab.aranuka.codegen.core.code.POJOTypes;
import de.topicmapslab.aranuka.codegen.core.definition.TopicAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.core.factory.AssociationAnnotationFactory;
import de.topicmapslab.aranuka.codegen.core.factory.CodeBufferFactory;
import de.topicmapslab.aranuka.codegen.core.factory.ITopicAnnotationFactory;
import de.topicmapslab.aranuka.codegen.core.factory.TopicAnnotationFactory;
import de.topicmapslab.aranuka.codegen.properties.PropertyLoader;

/**
 * 
 * @author Sven Krosse
 * 
 */
public class TopicMap2JavaMapper {

	private final AssociationAnnotationFactory associationAnnotationFactory;
	private final ITopicAnnotationFactory topicAnnotationFactory;
	private final CodeBufferFactory codeBufferFactory;

	private final TopicMap topicMap;
	private final String targetDirectory;
	private final String targetPackage;
	private final PropertyLoader propertyLoader;

	// private static TopicMap2JavaMapper topicMap2JavaMapper = null;

	public TopicMap2JavaMapper(final TopicMapSystem topicMapSystem,
			final TopicMap topicMap, final String targetPackage,
			final String targetDirectory) throws TopicMap2JavaMapperException {
		this(topicMapSystem, topicMap, targetPackage, targetDirectory, null);
	}

	public TopicMap2JavaMapper(final TopicMapSystem topicMapSystem,
			final TopicMap topicMap, final String targetPackage,
			final String targetDirectory, final ITopicAnnotationFactory factory)
			throws TopicMap2JavaMapperException {
		this.topicMap = topicMap;
		this.targetDirectory = targetDirectory;
		this.targetPackage = targetPackage;
		this.codeBufferFactory = CodeBufferFactory.getFactory();

		this.propertyLoader = PropertyLoader.initialize(topicMapSystem,
				topicMap);

		if (factory == null) {
			this.topicAnnotationFactory = new TopicAnnotationFactory(topicMap);
		} else {
			this.topicAnnotationFactory = factory;
		}

		this.associationAnnotationFactory = AssociationAnnotationFactory
				.buildFactory(topicMap);
	}

	public AssociationAnnotationFactory getAssociationAnnotationFactory() {
		return associationAnnotationFactory;
	}

	public TopicMap getTopicMap() {
		return topicMap;
	}

	public PropertyLoader getPropertyLoader() {
		return propertyLoader;
	}

	public void run(POJOTypes pojoType) throws TopicMap2JavaMapperException {
		for (TopicAnnotationDefinition topicAnnotationDefinition : topicAnnotationFactory
				.getTopicAnnotationDefinitions()) {
			CodeBuffer buffer = this.codeBufferFactory.newCodeBuffer(pojoType,
					targetDirectory, targetPackage, topicAnnotationDefinition);
			buffer.toClassFile();
		}
	}
}
