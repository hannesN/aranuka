package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.exception.BadAnnotationException;


public class RoleBinding extends AbstractFieldBinding {
	
	private static Logger logger = LoggerFactory.getLogger(RoleBinding.class);
	
	private String roleType;
	private TopicBinding playerBinding;
	
//	Map<RoleIdent, Role> cache;
//	
//	private class RoleIdent{
//		
//		Association association;
//		Object player;
//		boolean changed;
//	}
	
	// --[ public methods ]--------------------------------------------------------------------------------
	
	public RoleBinding(Map<String,String> prefixMap, AssociationContainerBinding parent) {
		super(prefixMap, parent);
	}
	
	
	
//	@SuppressWarnings("unchecked")
//	@Deprecated
//	public void persist(Association association, Object associationObject) throws BadAnnotationException{
//		
//		if(this.getValue(associationObject) == null)
//			return;
//
//		if (this.isArray())
//
//			for (Object obj : (Object[]) this.getValue(associationObject))
//				createRole(association, obj);
//				
//
//		else if (this.isCollection())
//
//			for (Object obj : (Collection<Object>) this.getValue(associationObject))
//				createRole(association, obj);
//
//		else
//
//			if (this.getValue(associationObject) != null)
//				createRole(association, this.getValue(associationObject));
//		
//		
//		// remove obsolete roles
//		removeObsoleteRoles(association);
//		
//	}

	// getter and setter
	
	@Override
	public String toString() {
		return "RoleBinding [playerBinding=" + playerBinding + ", roleType="
				+ roleType + "]";
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
		
	// --[ private methods ]-------------------------------------------------------------------------------
	
//	private void createRole(Association association, Object playerObject) throws BadAnnotationException{
//		
//		Role role = getRoleFromCache(playerObject, association);
//		
//		if(role == null){
//			
//			logger.info("Create new role " + getRoleType());
//			
//			//association.createRole();
//			
//			Topic player = playerBinding.persist(association.getTopicMap(), playerObject);
//			role = association.createRole(association.getTopicMap().createTopicBySubjectIdentifier(association.getTopicMap().createLocator(roleType)), player);
//
//			// add role to cache
//			addRoleToCache(association, playerObject, role);
//			
//		}else logger.info("Role already exist.");
//
//		setChanged(role);
//
//	}
	
//	private void addRoleToCache(Association association, Object player, Role role){ 
//		
//		RoleIdent r = new RoleIdent();
//		r.association = association;
//		r.player = player;
//		r.changed = true;
//		
//		if(this.cache == null)
//			this.cache = new HashMap<RoleIdent, Role>();
//		
//		this.cache.put(r, role);
//	}
//	
//	private void setChanged(Role role)
//	{
//		if(this.cache == null)
//			return;
//		
//		for(Map.Entry<RoleIdent, Role> entry:cache.entrySet()){
//			
//			if(entry.getValue().equals(role))
//				entry.getKey().changed = true;
//		}
//	}
//	
//	private void unsetChanded(){
//		
//		if(this.cache == null)
//			return;
//		
//		for(Map.Entry<RoleIdent, Role> entry:cache.entrySet())
//			entry.getKey().changed = false;
//		
//	}
//	
//	private Role getRoleFromCache(Object player, Association association){
//		
//		if(this.cache == null)
//			return null;
//		
//		for(Map.Entry<RoleIdent, Role> entry:cache.entrySet()){
//			
//			if(entry.getKey().player == player && entry.getKey().association.equals(association)){
//				return entry.getValue();								
//			}
//		}
//		
//		return null;
//	}
//	
//	private void removeObsoleteRoles(Association association){
//		
//		if(this.cache == null)
//			return;
//		
//		Set<RoleIdent> obsoleteEntries = new HashSet<RoleIdent>();
//		
//		for(Map.Entry<RoleIdent, Role> entry:this.cache.entrySet()){
//			
//			if(entry.getKey().association.equals(association) && entry.getKey().changed == false){
//				
//				logger.info("Remove obsolte role.");
//				
//				entry.getValue().remove();
//				
//				obsoleteEntries.add(entry.getKey());
//			}
//		}
//
//		for(RoleIdent r:obsoleteEntries)
//			this.cache.remove(r);
//		
//		// set flags back to false
//		unsetChanded();
//	}
//	
}
