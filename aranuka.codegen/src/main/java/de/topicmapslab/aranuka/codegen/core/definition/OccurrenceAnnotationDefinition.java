package de.topicmapslab.aranuka.codegen.core.definition;

import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
import de.topicmapslab.aranuka.codegen.core.util.TypeUtility;

/**
 * 
 * @author Sven Krosse
 * 
 */
public class OccurrenceAnnotationDefinition extends FieldDefinition {

	private final String occurrenceType;
	private final String member;
	private final Class<?> datatype;

	public OccurrenceAnnotationDefinition(String occurrenceType, String member,
			Class<?> datatype) {
		this.occurrenceType = occurrenceType;
		this.member = member;
		this.datatype = datatype;
	}

	public OccurrenceAnnotationDefinition(Occurrence occurrence)
			throws POJOGenerationException {
		Topic topic = occurrence.getType();
		occurrenceType = TypeUtility.getTypeAttribute(topic);
		member = TypeUtility.getJavaName(topic);
		datatype = TypeUtility.toJavaType(occurrence.getDatatype());
	}

	public String getAnnotation() {
		return "@Occurrence";
	}

	public String getAnnotationAttributes() {
		return "type=\"" + occurrenceType + "\"";
	}

	public String getFieldName() {
		return member;
	}

	public String getOccurrenceType() {
		return occurrenceType;
	}

	public Class<?> getFieldType() {
		return datatype;
	}

	public String getPredefinition() {
		return "";
	}

	public String getTMQLType() {
		return "Field";
	}

	public String getTMQLFilterType() {
		return "\"" + occurrenceType + "\"";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OccurrenceAnnotationDefinition) {
			OccurrenceAnnotationDefinition def = (OccurrenceAnnotationDefinition) obj;
			return def.getFieldName().equalsIgnoreCase(getFieldName())
					&& def.getFieldType().equals(getFieldType());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getFieldName().hashCode() * 999999 + getFieldType().hashCode();
	}

}
