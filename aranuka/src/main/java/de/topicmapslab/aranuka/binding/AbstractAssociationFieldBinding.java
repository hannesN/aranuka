package de.topicmapslab.aranuka.binding;

import java.util.Map;

public abstract class AbstractAssociationFieldBinding extends AbstractFieldBinding {

	public AbstractAssociationFieldBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		super(prefixMap, parent);
	}
}
