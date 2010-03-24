package de.topicmapslab.aranuka.codegen.core.definition;


import java.util.HashSet;
import java.util.Set;

import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
import de.topicmapslab.aranuka.codegen.core.util.TypeUtility;

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

	private String superType;

	private final Set<IdAnnotationDefinition> idAnnotationDefinitions = new HashSet<IdAnnotationDefinition>();
	private final Set<NameAnnotationDefinition> nameAnnotationDefinitions = new HashSet<NameAnnotationDefinition>();
	private final Set<OccurrenceAnnotationDefinition> occurrenceAnnotationDefinitions = new HashSet<OccurrenceAnnotationDefinition>();
	private final Set<AssociationAnnotationDefinition> associationAnnotationDefinitions = new HashSet<AssociationAnnotationDefinition>();

	public TopicAnnotationDefinition(String name, String type, String subjectIdentifer) {
		this.name = name;
		this.type = type;
		this.subjectIdentifer = subjectIdentifer;
		this.topic = null;
	}

	public TopicAnnotationDefinition(Topic topic) throws POJOGenerationException {

		this.topic = topic;
		type = TypeUtility.getTypeAttribute(topic);
		name = TypeUtility.getJavaName(topic);

		if (!topic.getSubjectIdentifiers().isEmpty()) {
			subjectIdentifer = topic.getSubjectIdentifiers().iterator().next().getReference();
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

	public Set<AssociationAnnotationDefinition> getAssociationAnnotationDefinitions() {
		return associationAnnotationDefinitions;
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

	public void addAssociationAnnotationDefinitions(
	        Set<AssociationAnnotationDefinition> associationAnnotationDefinitions) {
		this.associationAnnotationDefinitions.addAll(associationAnnotationDefinitions);
	}

	public void addNameAnnotationDefinitions(Set<NameAnnotationDefinition> nameAnnotationDefinitions) {
		this.nameAnnotationDefinitions.addAll(nameAnnotationDefinitions);
	}

	public void addOccurrenceAnnotationDefinitions(Set<OccurrenceAnnotationDefinition> occurrenceAnnotationDefinitions) {
		this.occurrenceAnnotationDefinitions.addAll(occurrenceAnnotationDefinitions);
	}

	public void addIdAnnotationDefinitions(Set<IdAnnotationDefinition> idAnnotationDefinitions) {
		this.idAnnotationDefinitions.addAll(idAnnotationDefinitions);
	}

	public void setSuperType(String superType) {
	    this.superType = superType;
    }
	
	public String getSuperType() {
	    return superType;
    }
}
