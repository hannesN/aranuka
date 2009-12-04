package de.topicmapslab.aranuka.codegen.core.code;

/**
 * 
 * @author Sven Krosse
 *
 */
public enum POJOTypes {

	/**
	 * simple POJO class definition. Each characteristics are stored as a field,
	 * setter and getter will be supported.
	 */
	SIMPLE_POJO,

	/**
	 * lazy POJO class definition. Information do not stored any time,
	 * information will be load dynamic on calling by using TMQL4J.
	 */
	LAZY_POJO,

	/**
	 * combination of {@link POJOTypes#SIMPLE_POJO} and
	 * {@link POJOTypes#LAZY_POJO}. Information will be stored after first call,
	 * otherwise information will not be stored.
	 */
	CACHED_POJO;
	
}
