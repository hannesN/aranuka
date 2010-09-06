/**
 * 
 */
package de.topicmapslab.aranuka.codegen.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.Generated;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Role;
import de.topicmapslab.aranuka.codegen.core.definition.AssociationAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.AssociationAnnotationDefinition.AssocOtherPlayers;
import de.topicmapslab.aranuka.codegen.core.definition.FieldDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.IdAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.NameAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.OccurrenceAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.TopicAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.exception.InvalidOntologyException;
import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
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
	private JClass generatedAnnotation;
	private JClass associationContainerAnnotation;
	private JClass occurrenceAnnotation;
	private JClass identifierAnnotation;
	private JClass roleAnnotation;

	private JCodeModel cm;
	private JPackage modelPackage;

	private Map<String, JClass> classMap = new HashMap<String, JClass>();
	
	public void generateCode(TopicMapSystem system, TopicMap schemaMap, File directory,
			String packageName) throws IOException, InvalidOntologyException {
		Set<TopicAnnotationDefinition> annotations = new DefinitionFactory(system, schemaMap).getTopicAnnotationDefinitions();

		cm = new JCodeModel();
		topicAnnotation = cm
				.ref(de.topicmapslab.aranuka.annotations.Topic.class.getName());
		nameAnnotation = cm.ref(Name.class.getName());
		occurrenceAnnotation = cm.ref(Occurrence.class.getName());
		identifierAnnotation = cm.ref(Id.class.getName());
		associationAnnotation = cm.ref(Association.class.getName());
		associationContainerAnnotation = cm.ref(AssociationContainer.class
				.getName());
		generatedAnnotation = cm.ref(Generated.class);
		roleAnnotation = cm.ref(Role.class);

		if (packageName == null)
			modelPackage = cm._package("");
		else
			modelPackage = cm._package(packageName);

		for (TopicAnnotationDefinition tad : annotations) {
			createType(tad);
		}

		if (directory.isFile())
			throw new IllegalArgumentException("path does not lead to a directory!");
		if (!directory.exists())
			directory.mkdir();
		
		cm.build(directory);
	}

	private void createType(TopicAnnotationDefinition tad) {
		try {
			
			JDefinedClass type = getType(tad.getName(), tad.isAbstract());
			
			
			JAnnotationUse use = type.annotate(topicAnnotation);
			use.param("subject_identifier", tad.getSubjectIdentifer());

			if (tad.getSuperType()!=null)
				type._extends(getType(tad.getSuperType(), tad.isAbstract()));
			
			for (IdAnnotationDefinition idad : tad.getIdAnnotationDefinitions()) {
				createIdFiled(type, idad);
			}

			for (NameAnnotationDefinition nad : tad
					.getNameAnnotationDefinitions()) {
				createNameField(type, nad);
			}

			for (OccurrenceAnnotationDefinition oad : tad
					.getOccurrenceAnnotationDefinitions()) {
				createOccurrenceField(type, oad);
			}

			for (AssociationAnnotationDefinition aad : tad
					.getAssociationAnnotationDefinitions()) {
				createAssociationFields(type, aad);
			}

		} catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
			return;
		}

	}

	private JDefinedClass getType(String name, boolean isAbtract) throws JClassAlreadyExistsException {
		JDefinedClass type = (JDefinedClass) classMap.get(name);
	    if (type == null) {
	    	int mods = JMod.PUBLIC;
	    	if (isAbtract)
	    		mods |= JMod.ABSTRACT;
	    	type = modelPackage._class(mods, name);
	    	classMap.put(name, type);
	    }
	    return type;
    }

	private void createAssociationFields(JDefinedClass type,
			AssociationAnnotationDefinition aad) {
		switch (aad.getAssocKind()) {
		case BINARY:
			createBinaryAssociationField(type, aad);
			break;
		case NNARY:
			createNnaryAssociationField(type, aad);
			break;
		case UNARY:
			createUnaryAssociationField(type, aad);
			break;

		}

	}

	/*
	 * Used to reference a class cretaed later
	 */
	private JClass getModelReference(Topic t) throws POJOGenerationException {
		return cm.ref(modelPackage.name() + "." + TypeUtility.getTypeName(t));
	}

	private void createBinaryAssociationField(JDefinedClass type,
			AssociationAnnotationDefinition aad) {

		try {
			AssociationAnnotationDefinition.AssocOtherPlayers aop = aad
					.getOtherPlayers().iterator().next();

			if (aop.isMany()) {
				createNnaryAssociationField(type, aad);
				return;
			}
			
			
			JClass ref = getModelReference(aop.getPlayer());
			// TODO set annotation??
			if (aad.isMany()) {
				JClass setRef = cm.ref(Set.class).narrow(ref);
				ref = setRef;
			}
			JFieldVar var = createField(type, aad, ref);

			JAnnotationUse assocAnnot = var.annotate(associationAnnotation);
			assocAnnot.param("type", aad.getAssociationType());
			assocAnnot.param("played_role", aad.getRoleType());
			assocAnnot.param("other_role", TypeUtility
					.getLocator(aop.getRole()).toExternalForm());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void createNnaryAssociationField(JDefinedClass type,
			AssociationAnnotationDefinition aad) {
		try {
			JClass ref = getAssociationContainer(type, aad);
			if (aad.isMany()) {
				JClass setRef = cm.ref(Set.class).narrow(ref);
				ref = setRef;
			}
			JFieldVar var = createField(type, aad, ref);

			JAnnotationUse assocAnnot = var.annotate(associationAnnotation);
			assocAnnot.param("type", aad.getAssociationType());
			assocAnnot.param("played_role", aad.getRoleType());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private JClass getAssociationContainer(JDefinedClass type, AssociationAnnotationDefinition aad) throws Exception {
		JDefinedClass container = type._class(aad.getContainerTypeName());

		container.annotate(associationContainerAnnotation);
		

		for (AssocOtherPlayers aop : aad.getOtherPlayers()) {
			JClass ref = getModelReference(aop.getPlayer());
			if (aop.isMany()) {
				JClass setRef = cm.ref(Set.class).narrow(ref);
				ref = setRef;
			}
			JFieldVar var = createField(container, aop, ref);
			JAnnotationUse use = var.annotate(roleAnnotation);
			use.param("type", TypeUtility.getTypeAttribute(aop.getRole()));
		}
		
		return container;

	}

	private void createUnaryAssociationField(JDefinedClass type,
			AssociationAnnotationDefinition aad) {
		JFieldVar var = createField(type, aad);

		JAnnotationUse assocAnnot = var.annotate(associationAnnotation);
		assocAnnot.param("type", aad.getAssociationType());
		assocAnnot.param("played_role", aad.getRoleType());
	}

	private void createOccurrenceField(JDefinedClass type,
			OccurrenceAnnotationDefinition oad) {
		JFieldVar var = createField(type, oad);

		var.annotate(occurrenceAnnotation).param("type",
				oad.getOccurrenceType());
	}

	private void createIdFiled(JDefinedClass type, IdAnnotationDefinition idad) {
		JFieldVar var = createField(type, idad);

		var.annotate(identifierAnnotation).param("type",
				idad.getIdentifierType());
	}

	private JFieldVar createField(JDefinedClass type, FieldDefinition def,
			JClass typeClass) {
		try {
			JFieldVar var = null;

			if (typeClass != null)
				var = type.field(JMod.PRIVATE, typeClass, def.getFieldName());
			else
				var = type.field(JMod.PRIVATE, def.getFieldType(), def
						.getFieldName());

			generateSetter(type, var);
			JMethod get = generateGetter(type, def.isMany(), var);

			if (def.isMany()) {
				generateAddAndRemove(type, typeClass, var, get);
			}
			return var;
		} catch (Exception e) {
			throw new RuntimeException("In create Field: "+def.getFieldName(), e);
		}
	}

	private JFieldVar createField(JDefinedClass type, FieldDefinition def) {
		Class<?> fieldType = JCodeModel.boxToPrimitive.get(def.getFieldType());
		if (fieldType == null)
			fieldType = def.getFieldType();
		JClass typeClass = null;
		if (def.isMany()) {
			if (fieldType.isPrimitive()) {
				typeClass = cm.ref(Set.class).narrow(JCodeModel.primitiveToBox.get(fieldType));
			} else {
				typeClass = cm.ref(Set.class).narrow(fieldType);
			}
		} else {
			typeClass = fieldType.isPrimitive() ? null : cm.ref(fieldType);
		}

		return createField(type, def, typeClass);
	}

	private void createNameField(JDefinedClass type,
			NameAnnotationDefinition nad) {
		try {
			JFieldVar var = createField(type, nad);

			var.annotate(nameAnnotation).param("type", nad.getTopicType());
		} catch (Exception e) {
			throw new RuntimeException("Exception in create name: "+nad.getFieldName(), e);
		}
	}

	private void generateAddAndRemove(JDefinedClass type, JClass typeClass,
			JFieldVar var, JMethod get) {
		JFieldRef fieldRef = JExpr._this().ref(var);
		
		String methodSuffix = TypeUtility.field2Method(var.name());

		JMethod add = type.method(JMod.PUBLIC, cm.VOID, "add" + methodSuffix);
		JVar param = add.param(typeClass.getTypeParameters().get(0), var.name());
		
		JClass hashSet = cm.ref(HashSet.class).narrow(param.type());
		
		JConditional _if = add.body()._if(fieldRef.eq(JExpr._null()));
		
		_if._then().block().assign(fieldRef, JExpr._new(hashSet));
		add.body().invoke(fieldRef, "add").arg(param);
		add.annotate(generatedAnnotation);

		JMethod remove = type.method(JMod.PUBLIC, cm.VOID, "remove"
				+ methodSuffix);
		param = remove.param(typeClass.getTypeParameters().get(0), var.name());

		_if = remove.body()
				._if(JExpr.invoke(get).invoke("contains").arg(param));
		_if._then().invoke(fieldRef, "remove").arg(param);

		remove.annotate(generatedAnnotation);

	}

	private JMethod generateSetter(JDefinedClass type, JFieldVar var) {
		JMethod set = type.method(JMod.PUBLIC, cm.VOID, "set"
				+ TypeUtility.field2Method(var.name()));
		JVar param = set.param(var.type(), var.name());
		JFieldRef fieldRef = JExpr._this().ref(var);
		set.body().assign(fieldRef, param);

		set.annotate(generatedAnnotation);
		return set;
	}

	private JMethod generateGetter(JDefinedClass type, boolean isMany,
			JFieldVar var) {
		String prefix = "get";
		if (var.type().name().equals("boolean"))
			prefix = "is";
		JMethod get = type.method(JMod.PUBLIC, var.type(), prefix
				+ TypeUtility.field2Method(var.name()));

		if (isMany) {
			JConditional _if = get.body()._if(var.eq(JExpr._null()));
			JClass collections = cm.ref("java.util.Collections");
			_if._then()._return(collections.staticInvoke("emptySet"));
			_if._else()._return(var);
		} else
			get.body()._return(var);

		get.annotate(generatedAnnotation);
		return get;
	}
}
