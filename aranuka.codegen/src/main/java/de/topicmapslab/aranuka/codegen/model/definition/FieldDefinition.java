package de.topicmapslab.aranuka.codegen.model.definition;

/**
 * 
 * @author Sven Krosse
 *
 */
public abstract class FieldDefinition implements IFieldDefinition {
	
	public String getMethodName() {
		return new String(getFieldName().charAt(0) + "").toUpperCase()
				+ getFieldName().substring(1);
	}	
	
	public boolean doesFieldTypeExtendsCollection(){
		return false;
	}

}
