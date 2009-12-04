package de.topicmapslab.aranuka.codegen.model.definition;

/**
 * 
 * @author Sven Krosse
 * 
 */
public interface IFieldDefinition {

	public abstract String getFieldName();

	public abstract String getFieldType();

	public String getMethodName();

	public abstract String getAnnotation();

	public abstract String getAnnotationAttributes();

	public abstract String getPredefinition();

	public boolean doesFieldTypeExtendsCollection();

	public String getTMQLType();

	public String getTMQLFilterType();

}
