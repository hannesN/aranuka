/**
 * 
 */
package de.topicmapslab.aranuka.codegen.core.factory;

/**
 * @author Hannes Niederhausen Comments from tinyTiM
 */
public interface Vocabular {

	public static final String TMDM = "http://psi.topicmaps.org/iso13250/model/";
	public static final String TMCL = "http://psi.topicmaps.org/tmcl/";

	/**
	 * Core concept of a constraint.
	 * 
	 * Used as constraint (topic) type.
	 */
	public static final String CONSTRAINT = TMCL + "constraint";

	/**
	 * Core concept of a subject. Although it this identifier is in the TMDM
	 * namespace, it really is specified by TMCL.
	 */
	public static final String SUBJECT = TMDM + "subject";

	/**
	 * Core concept of type-instance relationships. Used as association type.
	 */
	public static final String TYPE_INSTANCE = TMDM + "type-instance";

	/**
	 * Core concept of type within a type-instance relationship. Used as role
	 * type.
	 */
	public static final String TYPE = TMDM + "type";

	/**
	 * Core concept of instance within a type-instance relationship. Used as
	 * role type.
	 */
	public static final String INSTANCE = TMDM + "instance";

	/**
	 * Core concept of supertype-subtype relationship. Used as association type.
	 */
	public static final String SUPERTYPE_SUBTYPE = TMDM + "supertype-subtype";

	/**
	 * Core concept of supertype within a supertype-subtype relationship. Used
	 * as role type.
	 */
	public static final String SUPERTYPE = TMDM + "supertype";

	/**
	 * Core concept of subtype within a supertype-subtype relationship. Used as
	 * role type.
	 */
	public static final String SUBTYPE = TMDM + "subtype";

	/**
	 * Core concept of a topic name. Used as topic name type.
	 */
	public static final String TOPIC_NAME = TMDM + "topic-name";

	/**
	 * Used to indicate that a variant can be used for sorting purposes. Used as
	 * variant theme.
	 */
	public static final String SORT = TMDM + "sort";

	/*
	 * Topic types
	 * 
	 * c.f. 6. TMCL Declarations
	 */

	/**
	 * Indicates that a topic may be used as a topic type.
	 * 
	 * Used as topic type.
	 */
	public static final String TOPIC_TYPE = TMCL + "topic-type";

	/**
	 * Indicates that a topic may be used as association type.
	 * 
	 * Used as topic type.
	 */
	public static final String ASSOCIATION_TYPE = TMCL + "association-type";

	/**
	 * Indicates that a topic may be used as role type.
	 * 
	 * Used as topic type.
	 */
	public static final String ROLE_TYPE = TMCL + "role-type";

	/**
	 * Indicates that a topic may be used as occurrence type.
	 * 
	 * Used as topic type.
	 */
	public static final String OCCURRENCE_TYPE = TMCL + "occurrence-type";

	/**
	 * Indicates that a topic may be used as name type.
	 * 
	 * Used as topic type.
	 */
	public static final String NAME_TYPE = TMCL + "name-type";

	/**
	 * The tmcl:overlap-declaration is used to declare that the sets of
	 * instances of two or more topic types are non-disjoint (that is, that they
	 * may overlap). The default is that the instance sets of different topic
	 * types are disjoint.
	 * 
	 * Used as topic type.
	 */
	public static final String OVERLAP_DECLARATION = TMCL
			+ "overlap-declaration";

	@Deprecated
	// FIXME: What's the replacement for this PSI?
	public static final String SCOPE_TYPE = TMCL + "scope-type";

	/*
	 * Constraint types c.f. 7 TMCL Constraint Types
	 */

	/**
	 * The tmcl:abstract-constraint provides a way to express that a given topic
	 * type must not have any direct instances
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.2 Abstract Topic Type Constraint
	 */
	public static final String ABSTRACT_CONSTRAINT = TMCL
			+ "abstract-constraint";

	/**
	 * Use {@link #ABSTRACT_CONSTRAINT}
	 */
	@Deprecated
	public static final String ABSTRACT_TOPIC_TYPE_CONSTRAINT = ABSTRACT_CONSTRAINT;

	/**
	 * 
	 * 
	 * Used as association type.
	 * 
	 * See 7.2 Abstract Topic Type Constraint
	 */
	public static final String CONSTRAINED_TOPIC_TYPE = TMCL
			+ "constrained-topic-type";

	/**
	 * A subject identifier constraint provides a way to constrain the subject
	 * identifiers of instances of a given topic type.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.3 Subject Identifier Constraint
	 */
	public static final String SUBJECT_IDENTIFIER_CONSTRAINT = TMCL
			+ "subject-identifier-constraint";

	/**
	 * A subject String constraint provides a way to constrain the subject
	 * Strings of instances of a given topic type.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.4 Subject String Constraint
	 */
	public static final String SUBJECT_LOCATOR_CONSTRAINT = TMCL
			+ "subject-locator-constraint";

	/**
	 * A topic name constraint provides a way to constrain the type and
	 * cardinality of topic names for instances of a given topic type.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.5 Topic Name Constraint
	 */
	public static final String TOPIC_NAME_CONSTRAINT = TMCL
			+ "topic-name-constraint";

	/**
	 * A topic occurrence constraint defines a way to constrain the type and
	 * cardinality of occurrences connected to a topic of a given type.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.6 Topic Occurrence Constraint
	 */
	public static final String TOPIC_OCCURRENCE_CONSTRAINT = TMCL
			+ "topic-occurrence-constraint";

	/**
	 * A topic role constraint constrains the types of roles topics of a given
	 * type can play in associations of a given type. It can also be seen as
	 * constraining the types of topics which may play roles of a given type in
	 * associations of a given type.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.7 Topic Role Constraint
	 */
	public static final String TOPIC_ROLE_CONSTRAINT = TMCL
			+ "topic-role-constraint";

	/**
	 * Use {@link #TOPIC_ROLE_CONSTRAINT}.
	 */
	@Deprecated
	public static final String ROLE_PLAYER_CONSTRAINT = TOPIC_ROLE_CONSTRAINT;

	/**
	 * Constrains the types of topics which may appear in the scope of a name,
	 * occurrence, or association of a particular type.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.8 Scope Constraint.
	 */
	public static final String SCOPE_CONSTRAINT = TMCL + "scope-constraint";

	/**
	 * Constrains whether or not names, occurrence, and associations of a given
	 * type may be reified, and if so, what the type of the reifying topic must
	 * be.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.9 Reifier Constraint
	 */
	public static final String REIFIER_CONSTRAINT = TMCL + "reifier-constraint";

	/**
	 * Constrains what types of statements topics of a given type may reify.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.10 Topic Reifies Constraint
	 */
	public static final String TOPIC_REIFIES_CONSTRAINT = TMCL
			+ "topic-reifies-constraint";

	/**
	 * Constrains the number of roles of a particular type that may appear in
	 * associations of a given type.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.11 Association Role Constraint
	 */
	public static final String ASSOCIATION_ROLE_CONSTRAINT = TMCL
			+ "association-role-constraint";

	/**
	 * Provides a way to restrict which combinations of topic types are allowed
	 * to appear in associations of a certain type together.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.12 Role Combination Constraint
	 */
	public static final String ROLE_COMBINATION_CONSTRAINT = TMCL + "role-combination-constraint";

	/**
	 * Provides a way to constrain the allowed datatype of an occurrence of a
	 * given type.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.13 Occurrence Data Type Constraint.
	 */
	public static final String OCCURRENCE_DATATYPE_CONSTRAINT = TMCL + "occurrence-datatype-constraint";

	/**
	 * Provides a way to require all names or occurrences of a given type to
	 * have different values.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.14 Unique Value Constraint
	 */
	public static final String UNIQUE_VALUE_CONSTRAINT = TMCL
			+ "unique-value-constraint";

	/**
	 * Provides a mechanism for requiring that all values of a given name or
	 * occurrence type must match a given regular expression.
	 * 
	 * Used as constraint (topic) type.
	 * 
	 * See 7.15 Regular Expression Constraint
	 */
	public static final String REGULAR_EXPRESSION_CONSTRAINT = TMCL
			+ "regular-expression-constraint";

	// Role types
	/**
	 * 
	 * Used as role type.
	 */
	public static final String ALLOWS = TMCL + "allows";

	/**
	 * 
	 * Used as role type.
	 */
	public static final String ALLOWED = TMCL + "allowed";

	/**
	 * 
	 * Used as role type.
	 * @deprecated
	 */
	public static final String CONSTRAINS = TMCL + "constrains";

	/**
	 * 
	 * Used as role type.
	 */
	public static final String CONSTRAINED = TMCL + "constrained";

	/**
	 * 
	 * Used as role type.
	 */
	public static final String CONTAINER = TMCL + "container";

	/**
	 * 
	 * Used as role type.
	 */
	public static final String CONTAINEE = TMCL + "containee";

	/*
	 * Association types
	 */

	/**
	 * 
	 * Used as association type.
	 */
	public static final String CONSTRAINED_STATEMENT = TMCL
			+ "constrained-statement";

	/**
	 * 
	 * Used as association type.
	 */
	public static final String CONSTRAINED_ROLE = TMCL + "constrained-role";

	/**
	 * 
	 * Used as association type.
	 */
	public static final String OVERLAPS = TMCL + "overlaps";

	/**
	 * 
	 * Used as association type.
	 */
	public static final String ALLOWED_SCOPE = TMCL + "allowed-scope";

	/**
	 * 
	 * Used as association type.
	 */
	public static final String ALLOWED_REIFIER = TMCL + "allowed-reifier";

	/**
	 * 
	 * Used as association type.
	 */
	public static final String OTHER_CONSTRAINED_TOPIC_TYPE = TMCL
			+ "other-constrained-topic-type";

	/**
	 * 
	 * Used as association type.
	 */
	public static final String OTHER_CONSTRAINED_ROLE = TMCL
			+ "other-constrained-role";

	/**
	 * 
	 * Used as association type.
	 */
	public static final String BELONGS_TO_SCHEMA = TMCL + "belongs-to-schema";

	// Model topics

	public static final String VALIDATION_EXPRESSION = TMCL
			+ "validation-expression";

	/**
	 * Indicates a minimum cardinality.
	 * 
	 * Used as occurrence type.
	 */
	public static final String CARD_MIN = TMCL + "card-min";

	/**
	 * Indicates a maximum cardinality.
	 * 
	 * Used as occurrence type.
	 */
	public static final String CARD_MAX = TMCL + "card-max";

	/**
	 * Used to define a regular expression.
	 * 
	 * Used as occurrence type.
	 */
	public static final String REGEXP = TMCL + "regexp";

	/**
	 * Used to define the datatype (an IRI) of an occurrence or variant.
	 * 
	 * Used as occurrence type.
	 */
	public static final String DATATYPE = TMCL + "datatype";

	/*
	 * 10 Schema Documentation.
	 */

	/**
	 * 
	 * Used as topic type.
	 * 
	 * See 10.2 The Schema Topic
	 */
	public static final String SCHEMA = TMCL + "schema";

	/**
	 * Used to attach some identifier of the schema's version to the schema
	 * topic.
	 * 
	 * Used as occurrence type. See 10.2 The Schema Topic
	 */
	public static final String VERSION = TMCL + "version";

	/*
	 * 10.3 Documentation Occurrences
	 */

	/**
	 * Used to attach a textual description of a TMCL construct to it inside the
	 * topic map.
	 * 
	 * Used as occurrence type.
	 * 
	 * See 10.3 Documentation Occurrences
	 */
	public static final String DESCRIPTION = TMCL + "description";

	/**
	 * Used to attach any textual information to a TMCL construct inside the
	 * topic map.
	 * 
	 * Used as occurrence type.
	 * 
	 * See 10.3 Documentation Occurrences
	 */
	public static final String COMMENT = TMCL + "comment";

	/**
	 * Used to attach a to a TMCL construct a reference to any kind of external
	 * information about that construct.
	 * 
	 * Used as occurrence type.
	 * 
	 * See 10.3 Documentation Occurrences
	 */
	public static final String SEE_ALSO = TMCL + "see-also";

}
