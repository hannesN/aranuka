package de.topicmapslab.aranuka.binding;

import java.util.Map;

/**
 * Abstract class for association field binding classes.
 * @author christian haß
 *
 */
public abstract class AbstractAssociationFieldBinding extends AbstractFieldBinding {

	/**
	 * Constructor
	 * @param prefixMap - Configured prefixes to resolve iris.
	 * @param parent - Binding of the field owner, i.e. the association container.
	 */
	public AbstractAssociationFieldBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		super(prefixMap, parent);
	}
}
