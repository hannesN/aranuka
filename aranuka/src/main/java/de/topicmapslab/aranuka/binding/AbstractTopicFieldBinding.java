package de.topicmapslab.aranuka.binding;

import java.util.Map;

/**
 * Anstract class for topic field binding classes.
 * @author christian haß
 *
 */
public abstract class AbstractTopicFieldBinding extends AbstractFieldBinding {

	/**
	 * Constructor
	 * @param prefixMap - Configured prefixes to resolve iris.
	 * @param parent - Binding of the field owner, i.e. the association container.
	 */
	public AbstractTopicFieldBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		super(prefixMap, parent);
	}

}
