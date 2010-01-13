package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.Map;

import org.tmapi.core.Association;
import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.exception.BadAnnotationException;


public class RoleBinding extends AbstractAssociationFieldBinding {
	
	private String roleType;
	private TopicBinding playerBinding;
	
	public RoleBinding(Map<String,String> prefixMap, AssociationContainerBinding parent) {
		super(prefixMap, parent);
	}
	
	@SuppressWarnings("unchecked")
	public void persist(Association association, Object associationObject) throws BadAnnotationException{
		
		if(this.getValue(associationObject) == null)
			return;
		
		
		if (this.isArray())

			for (Object obj : (Object[]) this.getValue(associationObject))
				createRole(association, obj);
				

		else if (this.isCollection())

			for (Object obj : (Collection<Object>) this.getValue(associationObject))
				createRole(association, obj);

		else

			if (this.getValue(associationObject) != null)
				createRole(association, this.getValue(associationObject));
	}
	
	private void createRole(Association association, Object playerObject) throws BadAnnotationException{
		
		Topic player = playerBinding.persist(association.getTopicMap(), playerObject);
		association.createRole(association.getTopicMap().createTopicBySubjectIdentifier(association.getTopicMap().createLocator(roleType)), player);
		
		
	}
	
	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleTypeIdentifier) {
		this.roleType = roleTypeIdentifier;
	}

	public TopicBinding getPlayerBinding() {
		return playerBinding;
	}

	public void setPlayerBinding(TopicBinding playerBinding) {
		this.playerBinding = playerBinding;
	}
	

	@Override
	public String toString() {
		return "RoleBinding [roleType=" + roleType + "]";
	}

	
}
