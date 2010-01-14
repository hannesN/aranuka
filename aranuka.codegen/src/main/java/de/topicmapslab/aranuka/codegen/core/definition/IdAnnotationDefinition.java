package de.topicmapslab.aranuka.codegen.core.definition;

import de.topicmapslab.aranuka.annotations.IDTYPE;


/**
 * 
 * @author Sven Krosse
 *
 */
public class IdAnnotationDefinition extends FieldDefinition {

	private IDTYPE annotationType;
	private String fieldName;

	public IdAnnotationDefinition(final IDTYPE idtype) {
		this.annotationType = idtype;
		switch (annotationType) {
		case ITEM_IDENTIFIER:
			fieldName = "itemIdentifiers";
			break;
		case SUBJECT_IDENTIFIER:
			fieldName = "subjectIdentifiers";
			break;
		case SUBJECT_LOCATOR:
			fieldName = "subjectLocators";
			break;
		}

	}

	public String getFieldName() {
		return fieldName;
	}

	public IDTYPE getIdentifierType() {
		return annotationType;
	}

	public Class<?> getFieldType() {
		return String.class;
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
