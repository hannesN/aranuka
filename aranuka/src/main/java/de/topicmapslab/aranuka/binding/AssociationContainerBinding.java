package de.topicmapslab.aranuka.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Association;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.exception.BadAnnotationException;

public class AssociationContainerBinding extends AbstractClassBinding {

	private static Logger logger = LoggerFactory.getLogger(AssociationContainerBinding.class);
	
	private AssociationContainerBinding parent; // supertype
	private List<RoleBinding> roles;
	private Map<Object, Association> associationObjects;
	
	
	public Association persist(TopicMap topicMap, Object associationContainerObject, AssociationBinding associationBinding) throws BadAnnotationException{
		
		// look in cache
		Association ass = getAssociation(associationContainerObject);
		
		if(ass == null){
			
			logger.info("Create new association container association.");
			ass = persist(topicMap, associationContainerObject, associationBinding, this);
			
			
		}else{
			
			if(!isUpdated(ass))
				updateAssociation(ass, associationContainerObject);
		}
		
		return  ass;
	}

	private void updateAssociation(Association association, Object associationContainerObject) throws BadAnnotationException{
		
		setToUpdated(association);
		
		logger.info("Update existing association container association.");
		
		if(this.parent != null)
			createRoles(association, associationContainerObject, this.parent);
		
		createRoles(association, associationContainerObject, this);

	}
	
	private Association persist(TopicMap topicMap, Object associationContainerObject, AssociationBinding associationBinding, AssociationContainerBinding associationContainerBinding) throws BadAnnotationException{
		
		Association newAssociation;
		
		if(associationContainerBinding.parent != null)
			newAssociation = persist(topicMap, associationContainerObject, associationBinding, associationContainerBinding.parent);
		else newAssociation = topicMap.createAssociation(topicMap.createTopicBySubjectIdentifier(topicMap.createLocator(associationBinding.getAssociationType())),associationBinding.getScope(topicMap));
		
		// store in cache
		addAssociation(associationContainerObject, newAssociation);
		
		// create roles
		createRoles(newAssociation, associationContainerObject, associationContainerBinding);
		
				
		return newAssociation;
	}
	
	private void createRoles(Association association, Object associationContainerObject, AssociationContainerBinding associationContainerBinding) throws BadAnnotationException{
		
		for(RoleBinding role:associationContainerBinding.roles)
			role.persist(association, associationContainerObject);
		
	}
	
	private void addAssociation(Object object, Association association){
		
		if(associationObjects == null)
			associationObjects = new HashMap<Object, Association>();
		
		associationObjects.put(object, association);
		
	}
	
	private Association getAssociation(Object object){
		
		if(associationObjects == null)
			return null;
		
		return associationObjects.get(object);
	}
	
	
	public void setParent(AssociationContainerBinding parent) {
		this.parent = parent;
	}
	
	// role bindings
	
	public void addRoleBinding(RoleBinding rb) {
		if (roles==null)
			roles = new ArrayList<RoleBinding>();
		roles.add(rb);
	}
	
	public List<RoleBinding> getRoleBindings() {
		if (roles==null)
			return Collections.emptyList();
		return roles;
	}

	@Override
	public String toString() {
		return "AssociationContainerBinding [parent=" + parent + ", roles="
				+ roles + "]";
	}
	
	
}
