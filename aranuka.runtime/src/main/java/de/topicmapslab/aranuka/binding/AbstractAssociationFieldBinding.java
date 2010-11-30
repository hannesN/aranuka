/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.binding;

import java.util.Map;

/**
 * Abstract class for association field binding classes.
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
