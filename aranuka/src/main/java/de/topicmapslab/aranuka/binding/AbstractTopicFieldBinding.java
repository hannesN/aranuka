package de.topicmapslab.aranuka.binding;

import java.util.Map;

public abstract class AbstractTopicFieldBinding extends AbstractFieldBinding {

	public AbstractTopicFieldBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		super(prefixMap, parent);
	}

}
