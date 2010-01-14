package de.topicmapslab.aranuka.codegen.core.definition;

/**
 * 
 * @author Sven Krosse
 * 
 */
public interface IFieldDefinition {

	public String getFieldName();

	public Class<?> getFieldType();

	public String getMethodName();

	public String getAnnotation();

	public String getAnnotationAttributes();

	public String getPredefinition();

	public boolean doesFieldTypeExtendsCollection();

	public String getTMQLType();

	public String getTMQLFilterType();

}
