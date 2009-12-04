package de.topicmapslab.aranuka.codegen.core.factory;

import gnu.trove.THashSet;

import java.util.Set;

import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

import annoTM.core.annotations.IDTYPE;
import de.topicmapslab.aranuka.codegen.core.definition.IdAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.NameAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.OccurrenceAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.TopicAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.exception.InitializationException;
import de.topicmapslab.aranuka.codegen.core.exception.LazyOperationException;
import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
import de.topicmapslab.aranuka.codegen.properties.PropertyConstants;
import de.topicmapslab.aranuka.codegen.properties.PropertyLoader;

/**
 * 
 * @author Sven Krosse
 *
 */
public class TopicAnnotationFactory implements ITopicAnnotationFactory {

	private final Set<TopicAnnotationDefinition> topicAnnotationDefinitions = new THashSet<TopicAnnotationDefinition>();
	
	private final TopicMap topicMap;

	public TopicAnnotationFactory(final TopicMap topicMap)
			throws POJOGenerationException, LazyOperationException,
			InitializationException {
		this.topicMap = topicMap;
		init(topicMap);

	}	

	private void init(final TopicMap topicMap) throws POJOGenerationException,
			LazyOperationException, InitializationException {

		TypeInstanceIndex index = topicMap.getIndex(TypeInstanceIndex.class);
		for (Topic topicType : index.getTopicTypes()) {
			TopicAnnotationDefinition topicAnnotationDefinition = new TopicAnnotationDefinition(
					topicType);
			topicAnnotationDefinition
					.addNameAnnotationDefinitions(getNameAnnotationDefinitions(topicType));
			topicAnnotationDefinition
					.addOccurrenceAnnotationDefinitions(getOccurrenceAnnotationDefinitions(topicType));
			
			topicAnnotationDefinition.addIdAnnotationDefinitions(getIdAnnotationDefinitions(topicType));
			
			topicAnnotationDefinitions.add(topicAnnotationDefinition);
		}

	}

	public TopicMap getTopicMap() {
		return topicMap;
	}

	/* (non-Javadoc)
	 * @see de.topicmapslab.aranuka.codegen.core.factory.ITopicAnnotationFactory#getTopicAnnotationDefinitions()
	 */
	public Set<TopicAnnotationDefinition> getTopicAnnotationDefinitions() {
		return topicAnnotationDefinitions;
	}

	private Set<NameAnnotationDefinition> getNameAnnotationDefinitions(
			Topic topicType) throws POJOGenerationException,
			LazyOperationException, InitializationException {
		Set<NameAnnotationDefinition> nameAnnotationDefinitions = new THashSet<NameAnnotationDefinition>();
		for (Name name : topicType.getNames()) {
			nameAnnotationDefinitions.add(new NameAnnotationDefinition(name));
		}
		if (Boolean.parseBoolean(PropertyLoader.getInstance().getProperty(
				PropertyConstants.PROPERTY_GENERATION_FROM_INSTANCES))) {
			for (Topic topic : PropertyLoader.getInstance().getDataBridge()
					.getAllDatasetByType(topicType)) {
				for (Name name : topic.getNames()) {
					nameAnnotationDefinitions.add(new NameAnnotationDefinition(
							name));
				}
			}

		}
		return nameAnnotationDefinitions;
	}

	private Set<OccurrenceAnnotationDefinition> getOccurrenceAnnotationDefinitions(
			Topic topicType) throws POJOGenerationException,
			InitializationException, LazyOperationException {
		Set<OccurrenceAnnotationDefinition> occurrenceAnnotationDefinitions = new THashSet<OccurrenceAnnotationDefinition>();
		for (Occurrence occurrence : topicType.getOccurrences()) {
			occurrenceAnnotationDefinitions
					.add(new OccurrenceAnnotationDefinition(occurrence));
		}
		if (Boolean.parseBoolean(PropertyLoader.getInstance().getProperty(
				PropertyConstants.PROPERTY_GENERATION_FROM_INSTANCES))) {
			for (Topic topic : PropertyLoader.getInstance().getDataBridge()
					.getAllDatasetByType(topicType)) {
				for (Occurrence occurrence : topic.getOccurrences()) {
					occurrenceAnnotationDefinitions
							.add(new OccurrenceAnnotationDefinition(occurrence));
				}
			}

		}
		return occurrenceAnnotationDefinitions;
	}

	private Set<IdAnnotationDefinition> getIdAnnotationDefinitions(
			Topic topicType) throws POJOGenerationException,
			InitializationException, LazyOperationException {
		Set<IdAnnotationDefinition> idAnnotationDefinitions = new THashSet<IdAnnotationDefinition>();

		idAnnotationDefinitions.add(new IdAnnotationDefinition(
				IDTYPE.ITEM_IDENTIFIER));
		idAnnotationDefinitions.add(new IdAnnotationDefinition(
				IDTYPE.SUBJECT_IDENTIFIER));
		idAnnotationDefinitions.add(new IdAnnotationDefinition(
				IDTYPE.SUBJECT_LOCATOR));

		return idAnnotationDefinitions;
	}

}
