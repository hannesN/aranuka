package de.topicmapslab.aranuka.codegen.model.definition;

import java.util.List;

/**
 * 
 * @author Sven Krosse
 *
 */
public abstract class FieldDefinition implements IFieldDefinition {
	
	private boolean many = false;
	
	public String getMethodName() {
		return new String(getFieldName().charAt(0) + "").toUpperCase()
				+ getFieldName().substring(1);
	}	
	
	public boolean doesFieldTypeExtendsCollection(){
		return many;
	}
	
	public void setMany(boolean many) {
		this.many = many;
	}
	
	public boolean isMany() {
		return many;
	}

	
}
