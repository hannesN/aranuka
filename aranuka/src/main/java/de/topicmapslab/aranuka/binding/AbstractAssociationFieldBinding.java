package de.topicmapslab.aranuka.binding;

import java.util.Map;

import org.tmapi.core.Association;

import de.topicmapslab.aranuka.exception.BadAnnotationException;

public abstract class AbstractAssociationFieldBinding extends AbstractFieldBinding {

	public AbstractAssociationFieldBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		super(prefixMap, parent);
	}
	
//	// abstract persist method
//	@Deprecated
//	public abstract void persist(Association association, Object object) throws BadAnnotationException; 
//	
}
