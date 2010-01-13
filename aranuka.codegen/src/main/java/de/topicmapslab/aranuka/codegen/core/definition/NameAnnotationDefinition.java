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

	private final String type;
	private final String member;

	public NameAnnotationDefinition(final String type, final String member)
			throws POJOGenerationException {
		this.type = type;
		this.member = member;
	}

	public NameAnnotationDefinition(Name name) throws POJOGenerationException {
		Topic topic = name.getType();
		type = TypeUtility.getTypeAttribute(topic);
		member = TypeUtility.getJavaName(topic);
	}

	public String getAnnotation() {
		return "@Name";
	}

	public String getAnnotationAttributes() {
		return "type=\"" + type + "\"";
	}

	public String getFieldName() {
		return member.toLowerCase();
	}

	public String getFieldType() {
		if (isMany())
			return "Set<String>";
			
		return "String";
	}

	public String getPredefinition() {
		return "";
	}

	public String getTMQLType() {
		return "Field";
	}

	public String getTMQLFilterType() {
		return "\"" + type + "\"";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NameAnnotationDefinition) {
			NameAnnotationDefinition def = (NameAnnotationDefinition) obj;
			return def.getFieldName().equalsIgnoreCase(getFieldName())
					&& def.getFieldType().equalsIgnoreCase(getFieldType());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getFieldName().hashCode() * 999999 + getFieldType().hashCode();
	}

}
