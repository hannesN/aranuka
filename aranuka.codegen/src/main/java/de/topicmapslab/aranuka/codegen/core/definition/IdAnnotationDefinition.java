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

	public String getAnnotationType() {
		return annotationType.name();
	}


	public String getAnnotation() {
		return "@Id";
	}

	public String getAnnotationAttributes() {
		return "type=IDTYPE." + getAnnotationType();
	}


	public String getFieldType() {
		if (isMany())
			return "Set<String>";
		else
			return "String";
	}

	public String getPredefinition() {
		if (isMany())
			return " = new THashSet<String>()";
		else
			return "";
	}

	public String getTMQLType() {
		return "Identifiers";
	}
	
	public String getTMQLFilterType() {
		return "IDTYPE." + annotationType.name(); 
	}
}
