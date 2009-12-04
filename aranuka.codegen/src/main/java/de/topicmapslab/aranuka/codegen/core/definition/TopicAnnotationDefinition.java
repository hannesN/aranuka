package de.topicmapslab.aranuka.codegen.core.definition;

import gnu.trove.THashSet;

import java.util.Set;

import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
import de.topicmapslab.aranuka.codegen.core.utility.TypeUtility;
import de.topicmapslab.aranuka.codegen.model.definition.ITopicAnnotationDefinition;

/**
 * 
 * @author Sven Krosse
 *
 */
public class TopicAnnotationDefinition implements ITopicAnnotationDefinition {

	private final String type;
	private String subjectIdentifer = null;
	private final String name;
	private final Topic topic;

	private final Set<IdAnnotationDefinition> idAnnotationDefinitions = new THashSet<IdAnnotationDefinition>();
	private final Set<NameAnnotationDefinition> nameAnnotationDefinitions = new THashSet<NameAnnotationDefinition>();
	private final Set<OccurrenceAnnotationDefinition> occurrenceAnnotationDefinitions = new THashSet<OccurrenceAnnotationDefinition>();

	public TopicAnnotationDefinition(Topic topic)
			throws POJOGenerationException {

		this.topic = topic;
		type = TypeUtility.getTypeAttribute(topic);
		name = TypeUtility.getJavaName(topic);

		if (!topic.getSubjectIdentifiers().isEmpty()) {
			subjectIdentifer = topic.getSubjectIdentifiers().iterator().next()
					.getReference();
		}
	}

	public Set<IdAnnotationDefinition> getIdAnnotationDefinitions() {
		return idAnnotationDefinitions;
	}

	public Set<NameAnnotationDefinition> getNameAnnotationDefinitions() {
		return nameAnnotationDefinitions;
	}

	public Set<OccurrenceAnnotationDefinition> getOccurrenceAnnotationDefinitions() {
		return occurrenceAnnotationDefinitions;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getSubjectIdentifer() {
		return subjectIdentifer;
	}

	public Topic getTopic() {
		return topic;
	}

	public void addNameAnnotationDefinitions(
			Set<NameAnnotationDefinition> nameAnnotationDefinitions) {
		this.nameAnnotationDefinitions.addAll(nameAnnotationDefinitions);
	}

	public void addOccurrenceAnnotationDefinitions(
			Set<OccurrenceAnnotationDefinition> occurrenceAnnotationDefinitions) {
		this.occurrenceAnnotationDefinitions
				.addAll(occurrenceAnnotationDefinitions);
	}

	public void addIdAnnotationDefinitions(
			Set<IdAnnotationDefinition> idAnnotationDefinitions) {
		this.idAnnotationDefinitions.addAll(idAnnotationDefinitions);
	}

}
