package de.topicmapslab.aranuka.codegen.core.definition;

import de.topicmapslab.aranuka.codegen.model.definition.FieldDefinition;
import annoTM.core.annotations.IDTYPE;

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
		return "Set<String>";
	}

	public String getPredefinition() {
		return " = new THashSet<String>()";
	}

	public boolean doesFieldTypeExtendsCollection() {
		return true;
	}
	
	public String getTMQLType() {
		return "Identifiers";
	}
	
	public String getTMQLFilterType() {
		return "IDTYPE." + annotationType.name(); 
	}
}
