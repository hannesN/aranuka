package de.topicmapslab.aranuka.codegen.core.factory;

import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONSTRAINED;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONSTRAINED_STATEMENT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONSTRAINED_TOPIC_TYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONTAINEE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.CONTAINER;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.DATATYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.INSTANCE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.NAME_TYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.OCCURRENCE_DATATYPE_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.SUBJECT_IDENTIFIER_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.SUBJECT_LOCATOR_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.SUBTYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.SUPERTYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.SUPERTYPE_SUBTYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TMDM;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TOPIC_NAME_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TOPIC_OCCURRENCE_CONSTRAINT;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TOPIC_TYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TYPE;
import static de.topicmapslab.aranuka.codegen.core.factory.Vocabular.TYPE_INSTANCE;

import java.util.HashSet;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.aranuka.codegen.core.definition.AssociationAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.IdAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.NameAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.OccurrenceAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.definition.TopicAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.exception.InvalidOntologyException;
import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
import de.topicmapslab.aranuka.codegen.core.util.TypeUtility;
import de.topicmapslab.aranuka.enummerations.IdType;
import de.topicmapslab.tmql4j.common.core.runtime.TMQLRuntimeFactory;
import de.topicmapslab.tmql4j.common.model.query.IQuery;
import de.topicmapslab.tmql4j.common.model.runtime.ITMQLRuntime;
import de.topicmapslab.tmql4j.resultprocessing.model.IResult;


/**
 * 
 * The definition factory creates construct definitions based on a TMCL schema. 
 * These definitions will be used to generate the Java code and its annotations.
 * 
 * @author Hannes Niederhausen
 * @since 1.0.0
 */
public class DefinitionFactory {

	private final TopicMap topicMap;
	private final TopicMapSystem system;

	// -- topic types
	private Topic topicType;
	private Topic nameType;

	// -- associations
	private Topic constraintStatement;
	private Topic constrainedTopicType;

	// -- roles
	private Topic constrained;
	private Topic constraint; 

	// -- constraint
	
//	private Topic abstractConstraint;
	private Topic subjectIdentifierConstraint;
	private Topic subjectLocatorConstraint;
	private Topic topicNameConstraint;
	private Topic topicOccurrenceConstraint;
	private Topic occurrenceDatatypeConstraint;

	// -- occurrence types
	private Topic datatype;

	private Topic subject;
	
	private Topic superTypeRole;
	private Topic subTypeRole;
	private Topic superTypeSubTypeAssociation;

	private TypeInstanceIndex idx;
	private ITMQLRuntime runtime;

	/**
	 * Constructor for the factory
	 * @param system the topic map system containing the schema topic map
	 * @param topicMap the topic map containing the TMCL schema
	 */
	public DefinitionFactory(TopicMapSystem system, TopicMap topicMap) {
		this.topicMap = topicMap;
		this.system = system;
		init();
	}

	/**
     * Returns a set of {@link TopicAnnotationDefinition} which is the meta data to generate a java class 
     * for the topic.
     * 
     * @return set of {@link TopicAnnotationDefinition}; is never <code>null</code>
     * @throws InvalidOntologyException invalid ontology exception if the schema is not valid, e.g. a topic has subject identifier
     */
    public Set<TopicAnnotationDefinition> getTopicAnnotationDefinitions() throws InvalidOntologyException {
    
    	Set<TopicAnnotationDefinition> defs = new HashSet<TopicAnnotationDefinition>();
    
    	idx = topicMap.getIndex(TypeInstanceIndex.class);
    	if (!idx.isOpen()) {
    		idx.open();
    	}
    	try {
    		for (Topic t : idx.getTopics(topicType)) {
    			String typeName = getTypeName(t);
    			String si = null;
    			if (!t.getSubjectIdentifiers().isEmpty())
    				si = t.getSubjectIdentifiers().iterator().next().toExternalForm();
    
    			
    			
    			TopicAnnotationDefinition tad = new TopicAnnotationDefinition(
    					typeName, getTopicName(t), si);
    
    			tad.setSuperType(getTypeName(getSuperType(t)));
    			
    			findAbstractConstraint(t, tad);
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

	private void init()  {
		// removing TMDM types from cache
		createStandardTopic(TYPE);
		createStandardTopic(INSTANCE);
		createStandardTopic(TYPE_INSTANCE);
		superTypeRole = createStandardTopic(SUPERTYPE);
		subTypeRole = createStandardTopic(SUBTYPE);
		superTypeSubTypeAssociation = createStandardTopic(SUPERTYPE_SUBTYPE);
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

		datatype = createStandardTopic(DATATYPE);

		// roles
		constraint = createStandardTopic(CONSTRAINT);
		constrained = createStandardTopic(CONSTRAINED);

		// associations
		constraintStatement = createStandardTopic(CONSTRAINED_STATEMENT);
		constrainedTopicType = createStandardTopic(CONSTRAINED_TOPIC_TYPE);

		subjectIdentifierConstraint = createStandardTopic(SUBJECT_IDENTIFIER_CONSTRAINT);
		subjectLocatorConstraint = createStandardTopic(SUBJECT_LOCATOR_CONSTRAINT);
		topicNameConstraint = createStandardTopic(TOPIC_NAME_CONSTRAINT);
		topicOccurrenceConstraint = createStandardTopic(TOPIC_OCCURRENCE_CONSTRAINT);
		occurrenceDatatypeConstraint = createStandardTopic(OCCURRENCE_DATATYPE_CONSTRAINT);
		
        runtime = TMQLRuntimeFactory.newFactory().newRuntime(system, topicMap);
        runtime.getProperties().enableLanguageExtensionTmqlUl(true);
	}

	private Topic createStandardTopic(String type) {
		Locator l = topicMap.createLocator(type);
		return topicMap.createTopicBySubjectIdentifier(l);
	}

	private Topic getSuperType(Topic t) throws InvalidOntologyException {
	    Set<Role> roles = t.getRolesPlayed(subTypeRole, superTypeSubTypeAssociation);
	    if (roles.size()==0)
	    	return null;
	    if (roles.size()>1)
	    	throw new InvalidOntologyException("Only one super type is supported.");
	    Role r = roles.iterator().next();
	    
	    Set<Role> superTypes = r.getParent().getRoles(superTypeRole);
	    if (superTypes.size()!=1)
	    	throw new InvalidOntologyException("Only one super type is supported.");
	    
	    return superTypes.iterator().next().getPlayer();
	    	
	    
    }

	private String getTopicName(Topic t) {
		for (Name name : t.getNames()) {
			return name.getValue();
		}
		return null;
	}

	private String getTypeName(Topic t) {
		try {
			if (t==null)
				return null;
			
			String name = getNameByAnnotation(t);
			
			if (name == null) {
				name = getTopicName(t);
				if (name == null)
					return TypeUtility.getJavaName(t);
			}
			
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

	private String getNameByAnnotation(Topic t) {
		String queryString = "%PREFIX ara http://onotoa.topicmapslab.de/annotation/de/topicmapslab/aranuka/\n" +
						getTMQLIdentifierString(t) + " / ara:name ";
		IQuery q = runtime.run(queryString);
		
		for (IResult r : q.getResults()) {
			return (String) r.getResults().get(0);
		}
		return null;
    }

	private void findAbstractConstraint(Topic t, TopicAnnotationDefinition tad) {
		try {
	        String id = getIdentifierString(t);
	        
	        String query = "%prefix tmcl http://psi.topicmaps.org/tmcl/ \n"
	                + "for $c in // tmcl:abstract-constraint\n"
	                + "where tmcl:constrained-topic-type (tmcl:constraint : $c, tmcl:constrained : "+id+")"
	                + "return $c";
	        IQuery q = runtime.run(query);
	        if (q.getResults().size()==0)
	        	return;
	        
	        tad.setAbstract(true);
	        
	        
        } catch (Exception e) {
	       e.printStackTrace();
	       return;
        }
        
    }

	private String getIdentifierString(Topic t) {
	    StringBuilder b = new StringBuilder(10);
	    char suffix = '~';
	    b.append('"');
	    
	    Set<Locator> idSet = t.getSubjectIdentifiers();
	    if (idSet.size()>0) {
	    	b.append(idSet.iterator().next().toExternalForm());
	    } else {
	    	idSet = t.getSubjectLocators();
		    if (idSet.size()>0) {
		    	b.append(idSet.iterator().next().toExternalForm());
		    	suffix = '=';
		    } else {
		    	idSet = t.getSubjectLocators();
			    if (idSet.isEmpty()) {
			    	throw new RuntimeException("No Identifier in topic found");
			    }
			    b.append(idSet.iterator().next().toExternalForm());
		    	suffix = '!';
		    }
	    }
	    
	    b.append('"');
	    b.append(suffix);
	    
	    return b.toString();
    }

	private void findNameConstraints(Topic t, TopicAnnotationDefinition tad)
			throws POJOGenerationException {
		Set<NameAnnotationDefinition> nadSet = new HashSet<NameAnnotationDefinition>();

		for (Role constrainedTopic : t.getRolesPlayed(constrained,
				constrainedTopicType)) {
			Set<Role> roles = constrainedTopic.getParent().getRoles(constraint);
			if (roles.size()==0)
				continue;
			Topic ntc = roles.iterator().next().getPlayer();
			if (!ntc.getTypes().contains(topicNameConstraint))
				continue;

			// check if the generate field annotation exists and is false
			if (isHidden(ntc))
				continue;
			
			Topic nameType = ntc
					.getRolesPlayed(constraint, constraintStatement).iterator()
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
			// check if a name annotation exists for the current constraint, if so set it to member
			String tmp = getNameByAnnotation(ntc);
			if (tmp!=null)
				member = tmp;

			NameAnnotationDefinition nad = new NameAnnotationDefinition(
					typeId, member);
			nad.setMany(isMany(ntc));
			nadSet.add(nad);
		}
		tad.addNameAnnotationDefinitions(nadSet);
	}

	/**
	 * Checks if the constraint should not generate an attribute for the class.
	 * <p>This method checks if the Topic has an occurrence of type "http://onotoa.topicmapslab.de/annotation/de/topicmapslab/aranuka/generateattribute".
	 * If it exists and its value is "false" the field is hidden.
	 *
	 * @param constraint the constraint
	 * @return <code>true</code> if the field is hidden, <code>false</code> else
	 */
	private boolean isHidden(Topic constraint) {
		Locator loc = topicMap.createLocator("http://onotoa.topicmapslab.de/annotation/de/topicmapslab/aranuka/generateattribute");
		Topic annotationtype = topicMap.createTopicBySubjectIdentifier(loc);
		
		Set<Occurrence> occs = constraint.getOccurrences(annotationtype);
		if (occs.size()==0)
			return false;
		
		// if the annotation exists and its value is false, the field is hidden and won't be generated
		if ("false".equals(occs.iterator().next().getValue())) {
			return true;
		}
		
		
	    return false;
    }

	private void findOccurrenceConstraints(Topic t,
			TopicAnnotationDefinition tad) throws POJOGenerationException {
		Set<OccurrenceAnnotationDefinition> oadSet = new HashSet<OccurrenceAnnotationDefinition>();

		for (Role constrainedTopic : t.getRolesPlayed(constrained,
				constrainedTopicType)) {
			Set<Role> roles = constrainedTopic.getParent().getRoles(constraint);
			if (roles.size()==0)
				continue;
			Topic otc = roles.iterator().next().getPlayer();
			if (!otc.getTypes().contains(topicOccurrenceConstraint))
				continue;
			if (isHidden(otc))
				continue;

			Topic occType = otc.getRolesPlayed(constraint, constraintStatement)
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
			// check if a name annotation exists for the current constraint, if so set it to member
			String tmp = getNameByAnnotation(otc);
			if (tmp!=null)
				member = tmp;

			// get datatype
			String datatype = "xsd:string";
			for (Role role : occType.getRolesPlayed(
					constrained,constraintStatement)) {
				for (Role r : role.getParent().getRoles(constraint)) {
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

	/**
	 * Looks for identifier constraints associated with the given topic 
	 * @param t the topic which is the base of the topic annotation definition
	 * @param tad the annotation definition for the topic
	 * @throws POJOGenerationException
	 */
	private void findIdentifierConstraints(Topic t,
			TopicAnnotationDefinition tad) throws POJOGenerationException {
		Set<IdAnnotationDefinition> iadSet = new HashSet<IdAnnotationDefinition>();

		for (Role constrainedTopic : t.getRolesPlayed(constrained,
				constrainedTopicType)) {
			Set<Role> roles = constrainedTopic.getParent().getRoles(constraint);
			if (roles.size()==0)
				continue;
			Topic ic = roles.iterator().next().getPlayer();
			IdType type;
			if (ic.getTypes().contains(subjectIdentifierConstraint)) {
				type = IdType.SUBJECT_IDENTIFIER;
			} else if (ic.getTypes().contains(subjectLocatorConstraint)) {
				type = IdType.SUBJECT_LOCATOR;
			} else {
				continue;
			}
			
			if (isHidden(ic))
				continue;
			
			IdAnnotationDefinition idd = new IdAnnotationDefinition(type);
			idd.setMany(isMany(ic));
			iadSet.add(idd);

		}
		// there are no constraints for identifier so we create one item identifier constraint
		// every class needs an attribute for id
		if (iadSet.isEmpty()) {
			IdAnnotationDefinition idd = new IdAnnotationDefinition(IdType.ITEM_IDENTIFIER, "id", int.class);
			idd.setMany(false);
			iadSet.add(idd);
		}
		
		tad.addIdAnnotationDefinitions(iadSet);
	}

	private void findAssociationConstraints(Topic t,
			TopicAnnotationDefinition tad) throws POJOGenerationException {
		Set<AssociationAnnotationDefinition> aadSet = new HashSet<AssociationAnnotationDefinition>();

		// get 
		String queryString = "%PREFIX ara http://onotoa.topicmapslab.de/annotation/de/topicmapslab/aranuka/\n" + 
				" FOR $c IN // tmcl:topic-role-constraint" +
				" [  ( . / ara:generateattribute =~ \"true\" ) OR  fn:count ( . / ara:generateattribute) == 0 ]\n" +
				" WHERE $c >> traverse tmcl:constrained-topic-type == " + getTMQLIdentifierString(t) + 
				" RETURN $c, $c >> traverse tmcl:constrained-statement[0], " +
				" $c >> traverse tmcl:constrained-role[0]";
		
		IQuery query = runtime.run(queryString);
		
		
		for (IResult result : query.getResults()) {
			Topic topicRoleConstraint = (Topic) result.getResults().get(0);
	
			Topic assocType = (Topic) result.getResults().get(1);
			
			Topic roleType = (Topic) result.getResults().get(2);
			
			
			Set<AssociationAnnotationDefinition.AssocOtherPlayers> playerSet = new HashSet<AssociationAnnotationDefinition.AssocOtherPlayers>();
			AssociationAnnotationDefinition aad = new AssociationAnnotationDefinition(assocType, roleType, playerSet);
			aad.setFieldName(getNameByAnnotation(topicRoleConstraint));
					
			// if the play cardinality says many, we set the container to many
			// if not let it be, because another player could be set to many
			if (isMany(topicRoleConstraint))
				aad.setMany(true);
			
			// filling out the other players
			Topic otherRole = null;			
			Topic otherPlayer = null;
			boolean isMany = false;
			
			queryString = "FOR $c IN // tmcl:topic-role-constraint\n" + 
					" [ . >> traverse tmcl:constrained-statement == " + getTMQLIdentifierString(assocType) + "]" +
					" WHERE not ($c >> traverse tmcl:constrained-role == " + getTMQLIdentifierString(roleType) + ")" +
					" RETURN $c, $c >> traverse tmcl:constrained-role," + 
					" $c >> traverse tmcl:constrained-topic-type";
			
			IQuery query2 = runtime.run(queryString);
			
			for (IResult result2 : query2.getResults()) {
				Topic trc = (Topic) result2.getResults().get(0);
				otherRole = (Topic) result2.getResults().get(1);
				otherPlayer = (Topic) result2.getResults().get(2);
				isMany = isManyRole(assocType, otherRole);
				
				
				
				AssociationAnnotationDefinition.AssocOtherPlayers aop = new AssociationAnnotationDefinition.AssocOtherPlayers(otherRole, otherPlayer);
				aop.setMany(isMany);
				aop.setFieldName(getNameByAnnotation(trc));
					
				
				// check if role combination is set, if yes create a new assocdefinition (binary assoc) else add the player to the current
				if (roleCombinationConstraintExists(assocType)) {
					if (roleCombinationConstraintExists(assocType, t, roleType, otherPlayer, otherRole)) {
						Set<AssociationAnnotationDefinition.AssocOtherPlayers> pSet = new HashSet<AssociationAnnotationDefinition.AssocOtherPlayers>();
						AssociationAnnotationDefinition binAad = new AssociationAnnotationDefinition(assocType, roleType, pSet);
						pSet.add(aop);
						aadSet.add(binAad);
						binAad.setContainerTypeName(getTypeName(assocType));
					}
				} else {
					playerSet.add(aop);
					aad.setContainerTypeName(getTypeName(assocType));
					aadSet.add(aad);					
				}
			}
			// we have an unary association and need to add the aad to the set
			if (query2.getResults().size()==0) {
				aadSet.add(aad);
			}
		}
		tad.addAssociationAnnotationDefinitions(aadSet);
	}

	/**
	 * Returns a tmql query part which returns the given topic.
	 * 
	 * @param t the topic which identifier should be used
	 * @return a string containing the identifier string
	 */
	protected String getTMQLIdentifierString(Topic t) {
	    Set<Locator> siSet = t.getSubjectIdentifiers();
		if (!siSet.isEmpty())
			return "\""+siSet.iterator().next().toExternalForm()+"\" << indicators";
		
		Set<Locator> slSet = t.getSubjectLocators();
		if (!slSet.isEmpty())
			return "\""+slSet.iterator().next().toExternalForm()+"\" << locators";
		
		Set<Locator> iiSet = t.getItemIdentifiers();
		if (!iiSet.isEmpty())
			return "\""+iiSet.iterator().next().toExternalForm()+"\" << item";
		
		throw new IllegalArgumentException("The given topic has no identifier!");
    }

	/**
	 * Checks if the topic map contains a role combination constraint for the players.
	 * 
	 * 
	 * 
	 * @param assocType association type
	 * @param topicType a topic type
	 * @param roleType the role of the topic type
	 * @param otherPlayer the topic type of the other player
	 * @param otherRole the role of the other player
	 * @return <code>true</code> if a role combination constraint for this type exists
	 */
    private boolean roleCombinationConstraintExists(Topic assocType, Topic topicType, Topic roleType, Topic otherPlayer, Topic otherRole) {
		String queryString = "FOR $c IN // tmcl:role-combination-constraint \n" + 
				" [ . >> traverse tmcl:constrained-statement == " + getTMQLIdentifierString(assocType) + "]" + 
				" WHERE \n" + 
				" ($c>> traverse tmcl:constrained-role == " +  getTMQLIdentifierString(roleType) + 
				" AND\n" + 
				" $c >> traverse tmcl:constrained-topic-type == " + getTMQLIdentifierString(topicType) + 
				" AND\n" + 
				" $c >> traverse tmcl:other-constrained-role == " + getTMQLIdentifierString(otherRole) + 
				" AND\n" + 
				" $c >> traverse tmcl:other-constrained-topic-type == " + getTMQLIdentifierString(otherPlayer) + ")" + 
				" OR" + 
				" ($c>> traverse tmcl:constrained-role == " +  getTMQLIdentifierString(otherRole) + 
				" AND\n" + 
				" $c >> traverse tmcl:constrained-topic-type == " + getTMQLIdentifierString(otherPlayer) + 
				" AND\n" + 
				" $c >> traverse tmcl:other-constrained-role == " + getTMQLIdentifierString(roleType) + 
				" AND\n" + 
				" $c >> traverse tmcl:other-constrained-topic-type == " + getTMQLIdentifierString(topicType) + ")" + 
				"RETURN $c";
		
		
		IQuery q = runtime.run(queryString);
				
		// if we get a result we now theres is a constraint
	    return !q.getResults().isEmpty();
    }

    /**
     * Checks if a role combination constraint for this topic exists
     * 
     * @param assocType the assoc type to check
     * 
     * @return <code>true</code> if their is at least one role combination constraint; <code>false</code> else
     */
    private boolean roleCombinationConstraintExists(Topic assocType) {
    	String queryString = "FOR $c IN // tmcl:role-combination-constraint \n" + 
		" [ . >> traverse tmcl:constrained-statement == " + getTMQLIdentifierString(assocType) + "]" + 
		" RETURN $c";
    	
    	IQuery q = runtime.run(queryString);
    	
    	return !q.getResults().isEmpty();
    }
    
	/**
	 * Checks if the cardmax of the association role constraint of the given assocType
	 * using the role typ is > 1
	 * 
	 * @param assocType the association type
	 * @param otherRole the role to check
	 * @return <code>true</code> if the card-max of the topic-role-constraint connecting the two types is grater than 0
	 */
	private boolean isManyRole(Topic assocType, Topic otherRole) {
		
		String queryString = "FOR $c IN // tmcl:association-role-constraint\n" + 
				" [ . >> traverse tmcl:constrained-statement == " + getTMQLIdentifierString(assocType) + "]" +
				" WHERE $c >> traverse tmcl:constrained-role == " + getTMQLIdentifierString(otherRole) + 
				" RETURN $c / tmcl:card-max || \"*\"";
		
		IQuery query = runtime.run(queryString);
		
		for (IResult res : query.getResults()) {
			String cardMax = (String) res.getResults().get(0);
			return (!"1".equals(cardMax));
		}
			
		
		return false;
	}

	/**
	 * Checks the cardinality of the constraint.
	 * 
	 * @param constraint the constraint with cardinality occurrences
	 * @return <code>true</code>  card-max is greater 1, <code>false</code> else
	 */
	private boolean isMany(Topic constraint) {
		
		String queryString ="RETURN "+getTMQLIdentifierString(constraint)+" / tmcl:card-max || \"*\"";

		IQuery query = runtime.run(queryString);

		for (IResult res : query.getResults()) {
			String cardMax = (String) res.getResults().get(0);
			return (!"1".equals(cardMax));
		}
		return false;
	}
}
