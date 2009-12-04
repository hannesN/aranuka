package de.topicmapslab.aranuka.codegen.core.code;

import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_ANNOTATION_ATTRIBUTES;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_ANNOTATION_NAME;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_CLASS_NAME;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_FIELD_NAME;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_FIELD_TYPE;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_IMPORTS;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_METHOD_NAME;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_PACKAGE;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_PREDEFINITION;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_SUBJECT_IDENTIFIER;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_TIMESTAMP;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_TOPIC_TYPE_NAME;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_CLASSFOOT;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_CLASSHEAD;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_COMMENT;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_FIELDDEFINITION;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_IMPORTS;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_METHODDEFINITION_ADDER;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_METHODDEFINITION_GETTER;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_METHODDEFINITION_SETTER;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_PACKAGE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import de.topicmapslab.aranuka.codegen.core.definition.TopicAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.core.factory.AssociationAnnotationFactory;
import de.topicmapslab.aranuka.codegen.core.utility.ImportHandler;
import de.topicmapslab.aranuka.codegen.model.definition.FieldDefinition;

/**
 * 
 * @author Sven Krosse
 *
 */
public class AnnotationSupportingCodeBuffer extends CodeBuffer {

	private final TopicAnnotationDefinition topicAnnotationDefinition;
	private final String packageName;

	public AnnotationSupportingCodeBuffer(final String directory,
			final String packageName,
			final TopicAnnotationDefinition topicAnnotationDefinition) {
		super(directory, topicAnnotationDefinition.getName());
		this.topicAnnotationDefinition = topicAnnotationDefinition;
		this.packageName = packageName;
	}

	@Override
	public void toClassFile() throws TopicMap2JavaMapperException {
		clearBuffer();
		internalWriteOperation();
		super.toClassFile();
	}

	protected void internalWriteOperation() throws TopicMap2JavaMapperException {

		/*
		 * generate comment
		 */
		final String timeStamp = new SimpleDateFormat().format(new Date(System
				.currentTimeMillis()));
		String template = TEMPLATE_COMMENT;
		template = template.replace(TEMPLATE_VARIABLE_TIMESTAMP, timeStamp);
		appendCodeBlock(template);

		/*
		 * generate package
		 */
		template = TEMPLATE_PACKAGE;
		template = template.replace(TEMPLATE_VARIABLE_PACKAGE, packageName);
		appendCodeBlock(template);

		/*
		 * generate imports
		 */
		template = TEMPLATE_IMPORTS;
		template = template.replace(TEMPLATE_VARIABLE_IMPORTS, ImportHandler
				.generateImports());
		appendCodeBlock(template);

		/*
		 * generate class-head
		 */
		template = TEMPLATE_CLASSHEAD;
		template = template.replace(TEMPLATE_VARIABLE_TOPIC_TYPE_NAME,
				topicAnnotationDefinition.getType());
		template = template
				.replace(
						TEMPLATE_VARIABLE_SUBJECT_IDENTIFIER,
						topicAnnotationDefinition.getSubjectIdentifer() == null ? "none"
								: topicAnnotationDefinition
										.getSubjectIdentifer());
		template = template.replace(TEMPLATE_VARIABLE_CLASS_NAME,
				topicAnnotationDefinition.getName());
		appendCodeBlock(template);

		/*
		 * generate fields
		 */
		internalWriteOperationFields(topicAnnotationDefinition
				.getIdAnnotationDefinitions());
		internalWriteOperationFields(topicAnnotationDefinition
				.getNameAnnotationDefinitions());
		internalWriteOperationFields(topicAnnotationDefinition
				.getOccurrenceAnnotationDefinitions());
		internalWriteOperationFields(AssociationAnnotationFactory.buildFactory(
				topicAnnotationDefinition.getTopic().getParent())
				.getAssociationAnnotationDefinitions(
						topicAnnotationDefinition.getTopic()));

		/*
		 * generate fields methods
		 */
		internalWriteOperationFieldMethods(topicAnnotationDefinition
				.getIdAnnotationDefinitions());
		internalWriteOperationFieldMethods(topicAnnotationDefinition
				.getNameAnnotationDefinitions());
		internalWriteOperationFieldMethods(topicAnnotationDefinition
				.getOccurrenceAnnotationDefinitions());
		internalWriteOperationFieldMethods(AssociationAnnotationFactory
				.buildFactory(topicAnnotationDefinition.getTopic().getParent())
				.getAssociationAnnotationDefinitions(
						topicAnnotationDefinition.getTopic()));

		/*
		 * generate class foot
		 */
		appendCodeBlock(TEMPLATE_CLASSFOOT);
	}

	/**
	 * generate fields
	 */
	protected void internalWriteOperationFields(
			Set<? extends FieldDefinition> definitions) {
		for (FieldDefinition definition : definitions) {
			String template = TEMPLATE_FIELDDEFINITION;
			template = template.replace(TEMPLATE_VARIABLE_ANNOTATION_NAME,
					definition.getAnnotation());
			template = template.replace(
					TEMPLATE_VARIABLE_ANNOTATION_ATTRIBUTES, definition
							.getAnnotationAttributes());
			template = template.replace(TEMPLATE_VARIABLE_FIELD_TYPE,
					definition.getFieldType());
			template = template.replace(TEMPLATE_VARIABLE_FIELD_NAME,
					definition.getFieldName());
			template = template.replace(TEMPLATE_VARIABLE_PREDEFINITION,
					definition.getPredefinition());
			appendCodeBlock(template);
		}
	}

	/**
	 * generate fields methods
	 */
	private void internalWriteOperationFieldMethods(
			Set<? extends FieldDefinition> definitions) {
		for (FieldDefinition definition : definitions) {
			String template = TEMPLATE_METHODDEFINITION_GETTER;
			template = template.replace(TEMPLATE_VARIABLE_FIELD_TYPE,
					definition.getFieldType());
			template = template.replace(TEMPLATE_VARIABLE_FIELD_NAME,
					definition.getFieldName());
			template = template.replace(TEMPLATE_VARIABLE_METHOD_NAME,
					definition.getMethodName());
			appendCodeBlock(template);

			if (definition.doesFieldTypeExtendsCollection()) {
				template = TEMPLATE_METHODDEFINITION_ADDER;
			} else {
				template = TEMPLATE_METHODDEFINITION_SETTER;
			}
			template = template.replace(TEMPLATE_VARIABLE_FIELD_TYPE,
					definition.getFieldType());
			template = template.replace(TEMPLATE_VARIABLE_FIELD_NAME,
					definition.getFieldName());
			template = template.replace(TEMPLATE_VARIABLE_METHOD_NAME,
					definition.getMethodName());
			appendCodeBlock(template);
		}
	}

	protected TopicAnnotationDefinition getTopicAnnotationDefinition() {
		return topicAnnotationDefinition;
	}
	
	protected String getPackageName() {
		return packageName;
	}	
	
}
