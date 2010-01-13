/**
 * 
 */
package de.topicmapslab.aranuka.codegen.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.tmapi.core.TopicMap;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;

import de.topicmapslab.aranuka.codegen.core.definition.IdAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.TopicAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.factory.DefinitionFactory;
import de.topicmapslab.aranuka.codegen.core.util.TypeUtility;

/**
 * @author Hannes Niederhausen
 *
 */
public class CodeGenerator {

	private JClass topicAnnotation;
	private JClass nameAnnotation;
	private JClass associationAnnotation;
	private JClass associationContainerAnnotation;
	private JClass occurrenceAnnotation;
	private JClass identifierAnnotation;
	
	private JCodeModel cm;
	
	public void generateCode(TopicMap schemaMap, File directory) throws IOException {
		Set<TopicAnnotationDefinition> annotations = new DefinitionFactory(schemaMap).getTopicAnnotationDefinitions();
		
		cm = new JCodeModel();
		topicAnnotation = cm.ref(de.topicmapslab.aranuka.annotations.Topic.class.getName());
		nameAnnotation = cm.ref(de.topicmapslab.aranuka.annotations.Name.class.getName());
		occurrenceAnnotation = cm.ref(de.topicmapslab.aranuka.annotations.Occurrence.class.getName());
		identifierAnnotation = cm.ref(de.topicmapslab.aranuka.annotations.Identifier.class.getName());
		associationAnnotation = cm.ref(de.topicmapslab.aranuka.annotations.Association.class.getName());
		associationContainerAnnotation = cm.ref(de.topicmapslab.aranuka.annotations.AssociationContainer.class.getName());
		
		
		for (TopicAnnotationDefinition tad : annotations) {
			createType(tad);
		}
		
		cm.build(directory);
	}

	private void createType(TopicAnnotationDefinition tad) {
		try {
			JDefinedClass type = cm._class(tad.getType());
			JAnnotationUse use = type.annotate(topicAnnotation);
			use.param("type", tad.getSubjectIdentifer());
			
			for (IdAnnotationDefinition idad : tad.getIdAnnotationDefinitions()) {
				createIdFiled(type, idad);
			}
			
		} catch (JClassAlreadyExistsException e) {
			return;
		}
		
	}

	private void createIdFiled(JDefinedClass type, IdAnnotationDefinition idad) {
		Class<?> jt = TypeUtility.toJavaType(idad.getFieldType());
		if (idad.isMany())
			jt =  Set.class;//. TypeUtility.toJavaType(idad.getFieldType());
		
		
		
		JFieldVar var = type.field(JMod.PRIVATE, jt, idad.getFieldName());
		
		System.out.println(var.type());
		
		
	}
}
