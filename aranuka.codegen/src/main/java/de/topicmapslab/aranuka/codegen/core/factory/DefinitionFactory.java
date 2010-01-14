package de.topicmapslab.aranuka.codegen.core.factory;

import gnu.trove.THashSet; 

import java.util.HashSet;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.aranuka.annotations.ASSOCIATIONKIND;
import de.topicmapslab.aranuka.annotations.IDTYPE;

import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.ASSOCIATION_TYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CARD_MAX;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONSTRAINED;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONSTRAINED_ROLE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONSTRAINED_STATEMENT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONSTRAINED_TOPIC_TYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONSTRAINS;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONTAINEE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONTAINER;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.DATATYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.INSTANCE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.NAME_TYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.OCCURRENCE_DATATYPE_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.ROLE_TYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.SUBJECT_IDENTIFIER_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.SUBJECT_LOCATOR_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.SUBTYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.SUPERTYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.SUPERTYPE_SUBTYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TMDM;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TOPIC_NAME_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TOPIC_OCCURRENCE_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TOPIC_ROLE_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TOPIC_TYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TYPE_INSTANCE;

import de.topicmapslab.aranuka.codegen.core.definition.AssociationAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.IdAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.NameAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.OccurrenceAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.TopicAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
import de.topicmapslab.aranuka.codegen.core.util.TypeUtility;



public class DefinitionFactory {

	private final TopicMap topicMap;

	// -- topic types
	private Topic topicType;
	private Topic associationType;
	private Topic roleType;
//	private Topic occurrenceType;
	private Topic nameType;
//	private Topic scopeType;

//	private Topic topicMapSchema;

	// -- associations
	private Topic constraintStatement;
	private Topic constrainedTopicType;
	private Topic constrainedRole;
//	private Topic allowedReifier;
//	private Topic allowedScope;
//	private Topic otherConstrainedRole;
//	private Topic otherConstrainedTopicType;
	// private Topic overlaps;
//	private Topic belongsTo;

	// -- roles
	private Topic constrains;
	private Topic constrained;
//	private Topic allows;
//	private Topic allowed;

	// -- constrains
	// private Topic constraint; // -- not needed is super type
//	private Topic abstractConstraint;
	private Topic subjectIdentifierConstraint;
	private Topic subjectLocatorConstraint;
	private Topic topicNameConstraint;
	private Topic topicOccurrenceConstraint;
	private Topic topicRoleConstraint;
//	private Topic scopeConstraint;
//	private Topic reifierConstraint;
//	private Topic topicReifiesConstraint;
//	private Topic associationRoleConstraint;
//	private Topic roleCombinationConstraint;
	private Topic occurrenceDatatypeConstraint;
//	private Topic uniqueValueConstraint;
//	private Topic regularExpressionConstraint;
//	private Topic overlapDeclaration;

	// -- occurrence types
	private Topic datatype;
//	private Topic cardMin;
	private Topic cardMax;
//	private Topic regExp;
//	private Topic comment;
//	private Topic seeAlso;
//	private Topic description;

//	private Topic topicName;

	private Topic subject;

	private TypeInstanceIndex idx;

	public DefinitionFactory(TopicMap topicMap) {
		this.topicMap = topicMap;
		init();
	}

	private void init() {
		// removing TMDM types from cache
		createStandardTopic(TYPE);
		createStandardTopic(INSTANCE);
		createStandardTopic(TYPE_INSTANCE);
		createStandardTopic(SUPERTYPE);
		createStandardTopic(SUBTYPE);
		createStandardTopic(SUPERTYPE_SUBTYPE);
		// removing TMCL roles from cache
		createStandardTopic(CONTAINEE);
		createStandardTopic(CONTAINER);

//		topicName = createStandardTopic(TOPIC_NAME);
		subject = createStandardTopic(TMDM + "subject");

//		topicMapSchema = createStandardTopic(SCHEMA);
//		belongsTo = createStandardTopic(BELONGS_TO_SCHEMA);

		// init topic types
		topicType = createStandardTopic(TOPIC_TYPE);
		nameType = createStandardTopic(NAME_TYPE);
		associationType = createStandardTopic(ASSOCIATION_TYPE);
		roleType = createStandardTopic(ROLE_TYPE);
//		occurrenceType = createStandardTopic(OCCURRENCE_TYPE);
//		scopeType = createStandardTopic(SCOPE_TYPE);

//		cardMin = createStandardTopic(CARD_MIN);
		cardMax = createStandardTopic(CARD_MAX);
//		regExp = createStandardTopic(REGEXP);
		datatype = createStandardTopic(DATATYPE);
//		comment = createStandardTopic(COMMENT);
//		description = createStandardTopic(DESCRIPTION);
//		seeAlso = createStandardTopic(SEE_ALSO);

		// roles
		constrains = createStandardTopic(CONSTRAINS);
		constrained = createStandardTopic(CONSTRAINED);
//		allowed = createStandardTopic(ALLOWED);
//		allows = createStandardTopic(ALLOWS);

		// associations
		constraintStatement = createStandardTopic(CONSTRAINED_STATEMENT);
		constrainedTopicType = createStandardTopic(CONSTRAINED_TOPIC_TYPE);
		constrainedRole = createStandardTopic(CONSTRAINED_ROLE);
//		otherConstrainedTopicType = createStandardTopic(OTHER_CONSTRAINED_TOPIC_TYPE);
//		otherConstrainedRole = createStandardTopic(OTHER_CONSTRAINED_ROLE);
//		allowedReifier = createStandardTopic(ALLOWED_REIFIER);
//		allowedScope = createStandardTopic(ALLOWED_SCOPE);
		constrainedRole = createStandardTopic(CONSTRAINED_ROLE);
//		otherConstrainedRole = createStandardTopic(OTHER_CONSTRAINED_ROLE);
//		otherConstrainedTopicType = createStandardTopic(OTHER_CONSTRAINED_TOPIC_TYPE);

//		abstractConstraint = createStandardTopic(ABSTRACT_CONSTRAINT);
		subjectIdentifierConstraint = createStandardTopic(SUBJECT_IDENTIFIER_CONSTRAINT);
		subjectLocatorConstraint = createStandardTopic(SUBJECT_LOCATOR_CONSTRAINT);
		topicNameConstraint = createStandardTopic(TOPIC_NAME_CONSTRAINT);
		topicOccurrenceConstraint = createStandardTopic(TOPIC_OCCURRENCE_CONSTRAINT);
		topicRoleConstraint = createStandardTopic(TOPIC_ROLE_CONSTRAINT);
//		scopeConstraint = createStandardTopic(SCOPE_CONSTRAINT);
//		reifierConstraint = createStandardTopic(REIFIER_CONSTRAINT);
//		topicReifiesConstraint = createStandardTopic(TOPIC_REIFIES_CONSTRAINT);
//		associationRoleConstraint = createStandardTopic(ASSOCIATION_ROLE_CONSTRAINT);
//		roleCombinationConstraint = createStandardTopic(ROLE_COMBINATION_CONSTRAINT);
		occurrenceDatatypeConstraint = createStandardTopic(OCCURRENCE_DATATYPE_CONSTRAINT);
//		uniqueValueConstraint = createStandardTopic(UNIQUE_VALUE_CONSTRAINT);
//		regularExpressionConstraint = createStandardTopic(REGULAR_EXPRESSION_CONSTRAINT);
//		overlapDeclaration = createStandardTopic(OVERLAP_DECLARATION);
	}

	private Topic createStandardTopic(String type) {
		Locator l = topicMap.createLocator(type);
		return topicMap.createTopicBySubjectIdentifier(l);
	}

	public Set<TopicAnnotationDefinition> getTopicAnnotationDefinitions() {

		Set<TopicAnnotationDefinition> defs = new THashSet<TopicAnnotationDefinition>();

		idx = topicMap.getIndex(TypeInstanceIndex.class);
		try {
			for (Topic t : idx.getTopics(topicType)) {

				String typeName = getTypeName(t);
				String si = null;
				if (!t.getSubjectIdentifiers().isEmpty())
					si = t.getSubjectIdentifiers().iterator().next()
							.toExternalForm();

				TopicAnnotationDefinition tad = new TopicAnnotationDefinition(
						typeName, getTopicName(t), si);

				findNameConstraints(t, tad);
				findOccurrenceConstraints(t, tad);
				findIdentifierConstraints(t, tad);
				findAssociationConstraints(t, tad);

				defs.add(tad);
			}
		} catch (POJOGenerationException e) {
			e.printStackTrace();
		}
		return defs;
	}

	private String getTopicName(Topic t) {
		for (Name name : t.getNames()) {
			return name.getValue();
		}
		return null;
	}

	private String getTypeName(Topic t) {
		try {
			String name = getTopicName(t);
			if (name == null)
				return TypeUtility.getJavaName(t);

			StringBuilder builder = new StringBuilder();
			char lastChar = name.charAt(0);
			builder.append(Character.toUpperCase(lastChar));
			for (int i = 1; i < name.length(); i++) {
				char c = name.charAt(i);
				if (c != '-' && c != '_' && c != ':') {
					if ('-' == lastChar || '_' == lastChar || ':' == lastChar
							|| ' ' == lastChar) {
						builder.append(Character.toUpperCase(c));
					} else if (c != ' ') {
						builder.append(c);
					}
				}
				lastChar = c;
			}
			return builder.toString();
		} catch (POJOGenerationException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void findNameConstraints(Topic t, TopicAnnotationDefinition tad)
			throws POJOGenerationException {
		Set<NameAnnotationDefinition> nadSet = new HashSet<NameAnnotationDefinition>();

		for (Role constrainedTopic : t.getRolesPlayed(constrained,
				constrainedTopicType)) {
			Topic ntc = constrainedTopic.getParent().getRoles(constrains)
					.iterator().next().getPlayer();
			if (!ntc.getTypes().contains(topicNameConstraint))
				continue;

			Topic nameType = ntc
					.getRolesPlayed(constrains, constraintStatement).iterator()
					.next().getParent().getRoles(constrained).iterator().next()
					.getPlayer();

			String typeName;
			String member;
			String typeId;
			if (nameType.equals(this.nameType)) {
				typeName = "Name";
				typeId = "aranuka:Name";
				member = "name";
			} else {
				typeId = TypeUtility.getLocator(nameType).toExternalForm();
				typeName = TypeUtility.getJavaName(nameType);
				member = Character.toLowerCase(typeName.charAt(0))
						+ typeName.substring(1);
			}

			NameAnnotationDefinition nad = new NameAnnotationDefinition(
					typeId, member);
			nad.setMany(isMany(ntc));
			nadSet.add(nad);
		}
		tad.addNameAnnotationDefinitions(nadSet);
	}

	private void findOccurrenceConstraints(Topic t,
			TopicAnnotationDefinition tad) throws POJOGenerationException {
		Set<OccurrenceAnnotationDefinition> oadSet = new HashSet<OccurrenceAnnotationDefinition>();

		for (Role constrainedTopic : t.getRolesPlayed(constrained,
				constrainedTopicType)) {
			Topic otc = constrainedTopic.getParent().getRoles(constrains)
					.iterator().next().getPlayer();
			if (!otc.getTypes().contains(topicOccurrenceConstraint))
				continue;

			Topic occType = otc.getRolesPlayed(constrains, constraintStatement)
					.iterator().next().getParent().getRoles(constrained)
					.iterator().next().getPlayer();

			// get type and member
			String typeName;
			String typeId;
			String member;

			if (occType.equals(this.subject)) {
				typeName = "Object";
				member = "object";
				typeId = "aranuka:occurrence"; 
			} else {
				typeId = TypeUtility.getLocator(occType).toExternalForm();
				typeName = getTypeName(occType);
				member = Character.toLowerCase(typeName.charAt(0))
						+ typeName.substring(1);
			}

			// get datatype
			String datatype = "xsd:string";
			for (Role role : occType.getRolesPlayed(
					constrained,constraintStatement)) {
				for (Role r : role.getParent().getRoles(constrains)) {
					Topic player = r.getPlayer();
					if (player.getTypes()
							.contains(occurrenceDatatypeConstraint)) {
						datatype = player.getOccurrences(this.datatype)
								.iterator().next().getValue();
					}

				}
			}

			Class<?> clazz = TypeUtility.toJavaType(datatype);
			OccurrenceAnnotationDefinition oad = new OccurrenceAnnotationDefinition(typeId, member, clazz);
			oad.setMany(isMany(otc));
			oadSet.add(oad);
		}
		tad.addOccurrenceAnnotationDefinitions(oadSet);
	}

	private void findIdentifierConstraints(Topic t,
			TopicAnnotationDefinition tad) throws POJOGenerationException {
		Set<IdAnnotationDefinition> iadSet = new HashSet<IdAnnotationDefinition>();

		for (Role constrainedTopic : t.getRolesPlayed(constrained,
				constrainedTopicType)) {
			Topic ic = constrainedTopic.getParent().getRoles(constrains)
					.iterator().next().getPlayer();
			IDTYPE type;
			if (ic.getTypes().contains(subjectIdentifierConstraint)) {
				type = IDTYPE.SUBJECT_IDENTIFIER;
			} else if (ic.getTypes().contains(subjectLocatorConstraint)) {
				type = IDTYPE.SUBJECT_LOCATOR;
			} else {
				continue;
			}
			IdAnnotationDefinition idd = new IdAnnotationDefinition(type);
			idd.setMany(isMany(ic));
			iadSet.add(idd);

		}
		tad.addIdAnnotationDefinitions(iadSet);
	}

	private void findAssociationConstraints(Topic t,
			TopicAnnotationDefinition tad) throws POJOGenerationException {
		Set<AssociationAnnotationDefinition> aadSet = new HashSet<AssociationAnnotationDefinition>();

		for (Role constrainedTopic : t.getRolesPlayed(constrained,
				constrainedTopicType)) {
			Topic ttc = constrainedTopic.getParent().getRoles(constrains)
					.iterator().next().getPlayer();
			
			if (!ttc.getTypes().contains(topicRoleConstraint))
				continue;
			
			Topic assocType = null;
			for (Role constrainsRole : ttc.getRolesPlayed(constrains, constraintStatement)) {
				Topic tmp = constrainsRole.getParent().getRoles(constrained).iterator().next().getPlayer();
				if (tmp.getTypes().contains(associationType)) {
					assocType = tmp;
					break;
				}
			}
			
			Topic roleType = null;
			for (Role constrainsRole : ttc.getRolesPlayed(constrains, constrainedRole)) {
				Topic tmp = constrainsRole.getParent().getRoles(constrained).iterator().next().getPlayer();
				if (tmp.getTypes().contains(this.roleType)) {
					roleType = tmp;
					break;
				}
			}
			
			Topic otherRole = null;			
			Topic otherPlayer = null;
			for (Role assocRole : assocType.getRolesPlayed(constrained, constraintStatement)) {
				Topic constraint = assocRole.getParent().getRoles(constrains).iterator().next().getPlayer();
				if (!constraint.getTypes().contains(topicRoleConstraint))
					continue;
				
				
				Association assoc = constraint.getRolesPlayed(constrains, constrainedRole).iterator().next().getParent();
				
				// check if we have the otherRole
				Topic tmp = assoc.getRoles(constrained).iterator().next().getPlayer();
				if (tmp.equals(roleType))
					continue;
					
				otherRole = tmp;
				
				
				assoc = constraint.getRolesPlayed(constrains, constrainedTopicType).iterator().next().getParent();
				
				// check if we have the otherRole
				tmp = assoc.getRoles(constrained).iterator().next().getPlayer();
				otherPlayer = tmp;
			}
			
//			for (Role assocRole : assocType.getRolesPlayed(constrained, constraintStatement)) {
//				Topic constraint = fromRolesToOtherRoles(assocRole, constrains).iterator().next().getPlayer();
//				if (!constraint.getTypes().contains(topicRoleConstraint))
//					continue;
//				Topic rt = null;
//				for (Role constrainsRole : ttc.getRolesPlayed(constrains, constrainedRole)) {
//					Topic tmp = constrainsRole.getParent().getRoles(constrained).iterator().next().getPlayer();
//					if (tmp.getTypes().contains(this.roleType)) {
//						rt = tmp;
//						break;
//					}
//				}
//				if (otherRole.equals(rt)) {
//					for (Role constrainsRole : ttc.getRolesPlayed(constrains, constrainedTopicType)) {
//						Topic tmp = constrainsRole.getParent().getRoles(constrained).iterator().next().getPlayer();
//						otherPlayer = tmp;
//						break;
//						
//					}
//				}
//			}
			
			String assocTypeName = TypeUtility.getJavaName(assocType);
			String roleName = TypeUtility.getJavaName(roleType);
			String otherRoleName = (otherRole!=null) ? TypeUtility.getJavaName(otherRole) : "";
			String playerName = (otherPlayer!=null) ? TypeUtility.getJavaName(otherPlayer) : "";
			
			ASSOCIATIONKIND kind = (otherRole==null) ? ASSOCIATIONKIND.UNARY : ASSOCIATIONKIND.BINARY;
			
			AssociationAnnotationDefinition aad = new AssociationAnnotationDefinition(kind, assocTypeName, roleName, otherRoleName, playerName);
			
			aadSet.add(aad);
		}
		tad.addAssociationAnnotationDefinitions(aadSet);
	}

	private boolean isMany(Topic constraint) {
		
		Set<Occurrence> occs = constraint.getOccurrences(cardMax);
		if (occs.isEmpty())
			return false;
		int i = 0;
		String value = occs.iterator().next().getValue();
		if (value.equals("*"))
			return true;
		try {
			i = Integer.parseInt(value); 
		} catch (NumberFormatException e) {
			return false;
		}
		
		return (i>1);
			
	}
}
