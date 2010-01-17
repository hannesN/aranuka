package de.topicmapslab.aranuka.binding;

import java.util.HashMap;
import java.util.Map;

public class TestAssociationBinding extends AbstractFieldBinding {

	private String associationType;
	private String playedRole;
	
	private Map<TopicBinding, String> counterPlayerBindingsAndRoles; // <Binding,RoleType>
	
	
	public TestAssociationBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		super(prefixMap, parent);
	}


	// getter and setter 
	
	public void addCounterPlayer(TopicBinding counterPlayerBinding, String counterPlayerRoleType){
		
		if(counterPlayerBindingsAndRoles == null)
			counterPlayerBindingsAndRoles = new HashMap<TopicBinding, String>();
		
		
		counterPlayerBindingsAndRoles.put(counterPlayerBinding, counterPlayerRoleType);
	}
	
	public String getAssociationType() {
		return associationType;
	}


	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}


	public String getPlayedRole() {
		return playedRole;
	}


	public void setPlayedRole(String playedRole) {
		this.playedRole = playedRole;
	}
	
	
	
	
	
}
