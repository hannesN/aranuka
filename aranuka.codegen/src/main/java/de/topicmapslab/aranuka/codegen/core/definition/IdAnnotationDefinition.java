package de.topicmapslab.aranuka.codegen.core.definition;

import de.topicmapslab.aranuka.enummerations.IdType;



/**
 * 
 * @author Sven Krosse
 *
 */
public class IdAnnotationDefinition extends FieldDefinition {

	private IdType annotationType;
	private final String fieldName;
	private final Class<?> fieldType;

	public IdAnnotationDefinition(final IdType idtype) {
		this(idtype, String.class);
	}
	
	public IdAnnotationDefinition(final IdType idType, Class<?> fieldType) {
		this(idType, null, fieldType);
	}
	
	public IdAnnotationDefinition(final IdType idType, String fieldName, Class<?> fieldType) {
		this.fieldType = fieldType;
		this.annotationType = idType;
		if (fieldName == null) {
			switch (annotationType) {
			case SUBJECT_IDENTIFIER:
				this.fieldName = "subjectIdentifiers";
				break;
			case SUBJECT_LOCATOR:
				this.fieldName = "subjectLocators";
				break;
			default:
				this.fieldName = "itemIdentifiers";
				break;
			}
		} else {
			this.fieldName = fieldName;
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public IdType getIdentifierType() {
		return annotationType;
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public String getTMQLType() {
		return "Identifiers";
	}
	
	public String getTMQLFilterType() {
		return "IDTYPE." + annotationType.name(); 
	}

	public String getAnnotation() {
		throw new UnsupportedOperationException();
	}

	public String getAnnotationAttributes() {
		throw new UnsupportedOperationException();
	}

	public String getPredefinition() {
		throw new UnsupportedOperationException();
	}
}
