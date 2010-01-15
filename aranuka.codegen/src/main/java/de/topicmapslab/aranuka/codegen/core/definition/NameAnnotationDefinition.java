package de.topicmapslab.aranuka.codegen.core.definition;

import org.tmapi.core.Name;
import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
import de.topicmapslab.aranuka.codegen.core.util.TypeUtility;

/**
 * 
 * @author Sven Krosse
 *
 */
public class NameAnnotationDefinition extends FieldDefinition {

	private final String topicType;
	private final String member;

	public NameAnnotationDefinition(final String topicType, final String member)
			throws POJOGenerationException {
		this.topicType = topicType;
		this.member = member;
	}

	public NameAnnotationDefinition(Name name) throws POJOGenerationException {
		Topic topic = name.getType();
		topicType = TypeUtility.getTypeAttribute(topic);
		member = TypeUtility.getJavaName(topic);
	}


	public String getFieldName() {
		return member.toLowerCase();
	}

	

	public String getPredefinition() {
		return "";
	}

	public String getTMQLType() {
		return "Field";
	}

	public String getTMQLFilterType() {
		return "\"" + topicType + "\"";
	}

	public String getTopicType() {
		return topicType;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((member == null) ? 0 : member.hashCode());
		result = prime * result + ((topicType == null) ? 0 : topicType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NameAnnotationDefinition other = (NameAnnotationDefinition) obj;
		if (member == null) {
			if (other.member != null)
				return false;
		} else if (!member.equals(other.member))
			return false;
		if (topicType == null) {
			if (other.topicType != null)
				return false;
		} else if (!topicType.equals(other.topicType))
			return false;
		return true;
	}

	public String getAnnotation() {
		throw new UnsupportedOperationException();
	}

	public String getAnnotationAttributes() {
		throw new UnsupportedOperationException();
	}

	public Class<?> getFieldType() {
		return String.class;
	}

}
