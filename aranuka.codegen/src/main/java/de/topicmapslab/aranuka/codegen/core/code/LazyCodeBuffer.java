package de.topicmapslab.aranuka.codegen.core.code;

import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_CLASS_NAME;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_FIELD_NAME;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_FIELD_TYPE;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_IMPORTS;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_METHOD_NAME;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_PACKAGE;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_SUBJECT_IDENTIFIER;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_TIMESTAMP;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_TMQLTYPE;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_TOPIC_TYPE_NAME;
import static de.topicmapslab.aranuka.codegen.core.code.templates.TemplateVariables.TEMPLATE_VARIABLE_TYPE;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_CLASSFOOT;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_CLASSHEAD;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_COMMENT;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_CONSTRUCTOR;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_IMPORTS;
import static de.topicmapslab.aranuka.codegen.core.code.templates.Templates.TEMPLATE_LAZY_METHODDEFINITION_GETTER;
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
public class LazyCodeBuffer extends AnnotationSupportingCodeBuffer {

	public LazyCodeBuffer(String directory, String packageName,
			TopicAnnotationDefinition topicAnnotationDefinition) {
		super(directory, packageName, topicAnnotationDefinition);
	}

	@Override
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
		template = template
				.replace(TEMPLATE_VARIABLE_PACKAGE, getPackageName());
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
				getTopicAnnotationDefinition().getType());
		template = template
				.replace(
						TEMPLATE_VARIABLE_SUBJECT_IDENTIFIER,
						getTopicAnnotationDefinition().getSubjectIdentifer() == null ? "none"
								: getTopicAnnotationDefinition()
										.getSubjectIdentifer());
		template = template.replace(TEMPLATE_VARIABLE_CLASS_NAME,
				getTopicAnnotationDefinition().getName());
		appendCodeBlock(template);

		/*
		 * generate constructor
		 */
		template = TEMPLATE_CONSTRUCTOR;
		template = template.replace(TEMPLATE_VARIABLE_CLASS_NAME,
				getTopicAnnotationDefinition().getName());
		appendCodeBlock(template);

		/*
		 * generate fields methods
		 */
		internalWriteOperationFieldMethods(getTopicAnnotationDefinition()
				.getIdAnnotationDefinitions());
		internalWriteOperationFieldMethods(getTopicAnnotationDefinition()
				.getNameAnnotationDefinitions());
		internalWriteOperationFieldMethods(getTopicAnnotationDefinition()
				.getOccurrenceAnnotationDefinitions());
		internalWriteOperationFieldMethods(AssociationAnnotationFactory
				.buildFactory(
						getTopicAnnotationDefinition().getTopic().getParent())
				.getAssociationAnnotationDefinitions(
						getTopicAnnotationDefinition().getTopic()));

		/*
		 * generate class foot
		 */
		appendCodeBlock(TEMPLATE_CLASSFOOT);
	}

	/**
	 * generate fields methods
	 */
	private void internalWriteOperationFieldMethods(
			Set<? extends FieldDefinition> definitions) {
		for (FieldDefinition definition : definitions) {
			String template = TEMPLATE_LAZY_METHODDEFINITION_GETTER;
			template = template.replace(TEMPLATE_VARIABLE_FIELD_TYPE,
					definition.getFieldType());
			template = template.replace(TEMPLATE_VARIABLE_FIELD_NAME,
					definition.getFieldName());
			template = template.replace(TEMPLATE_VARIABLE_METHOD_NAME,
					definition.getMethodName());
			template = template.replace(TEMPLATE_VARIABLE_TMQLTYPE, definition
					.getTMQLType());
			template = template.replace(TEMPLATE_VARIABLE_TYPE, definition
					.getTMQLFilterType());
			appendCodeBlock(template);

		}
	}

}
