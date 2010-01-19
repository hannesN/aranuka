package de.topicmapslab.aranuka.binding;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssociationBinding extends AbstractFieldBinding {

	@Override
	public String toString() {
		return "AssociationBinding [associationContainer="
				+ associationContainer + ", associationType=" + associationType
				+ ", otherPlayer=" + otherPlayer
				+ ", otherRole=" + otherRole + ", playedRole=" + playedRole
				+ "]";
	}

	private static Logger logger = LoggerFactory.getLogger(AssociationBinding.class);
	
	private String associationType;
	private String playedRole;
	private String otherRole;
	private TopicBinding otherPlayer;
	private AssociationContainerBinding associationContainer;
	private boolean persistOnCascade;
	
	// --[ public methods ]--------------------------------------------------------------------------------
	
	public AssociationBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		super(prefixMap, parent);
	}
	
	// getter and setter
	
	public String getAssociationType() {
		return associationType;
	}

	public void setAssociationType(String associationTypeIdentifier) {
		this.associationType = associationTypeIdentifier;
	}

	public String getPlayedRole() {
		return playedRole;
	}

	public void setPlayedRole(String playedRole) {
		this.playedRole = playedRole;
	}

	public String getOtherRole() {
		return otherRole;
	}

	public void setOtherRole(String otherRole) {
		this.otherRole = otherRole;
	}

	public TopicBinding getOtherPlayer() {
		return otherPlayer;
	}

	public void setOtherPlayer(TopicBinding otherPlayer) {
		this.otherPlayer = otherPlayer;
	}

	public AssociationContainerBinding getAssociationContainer() {
		return associationContainer;
	}

	public void setAssociationContainer(
			AssociationContainerBinding associationContainer) {
		this.associationContainer = associationContainer;
	}

	public boolean isPersistOnCascade() {
		return persistOnCascade;
	}

	public void setPersistOnCascade(boolean persistOnCascade) {
		this.persistOnCascade = persistOnCascade;
	}
	
	
	
	// --[ private methods ]-------------------------------------------------------------------------------
		
//	private void createUnaryAssociation(Topic topic, Object topicObject){
//		
//		if((Boolean)this.getValue(topicObject)){
//			
//			Association ass = getAssociationFromCache(null, topic);
//			
//			if(ass == null)
//			{
//				logger.info("Create unary association " + this.associationType);
//				ass = topic.getTopicMap().createAssociation(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(associationType)), getScope(topic.getTopicMap()));
//				ass.createRole(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(playedRole)), topic);
//				
//				// add association to cache
//				addAssociationToCache(topic, null, ass);
//				
//			}else logger.info("Unary association already exist.");
//			
//			setChanged(ass);
//			
//		}
//	}
//	
//	@SuppressWarnings("unchecked")
//	private void createBinaryAssociation(Topic topic, Object topicObject) throws BadAnnotationException{
//		
//		//logger.info("Create binary association " + this.associationType);
//		
//		if(this.getValue(topicObject) == null)
//			return;
//		
//		if(this.isArray()){
//			
//			for (Object obj:(Object[])this.getValue(topicObject))
//				createBinaryAssociation(topic, topicObject, obj);
//			
//		}else if(this.isCollection()){
//				
//			for (Object obj:(Collection<Object>)this.getValue(topicObject))
//				createBinaryAssociation(topic, topicObject, obj);
//
//		}else{ 
//			createBinaryAssociation(topic, topicObject, this.getValue(topicObject));
//		}
//	}
//	
//	private void createBinaryAssociation(Topic topic, Object topicObject, Object otherTopicObject) throws BadAnnotationException{
//		
//		Association ass = getAssociationFromCache(otherTopicObject, topic);
//		
//		if(ass == null)
//		{
//			logger.info("Create new binary association.");
//			
//			// create other player
//			Topic otherTopic = otherPlayer.persist(topic.getTopicMap(), otherTopicObject);
//			ass = topic.getTopicMap().createAssociation(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(associationType)), getScope(topic.getTopicMap()));
//			
//			ass.createRole(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(this.playedRole)), topic);
//			ass.createRole(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(this.otherRole)), otherTopic);
//
//			// add association to cache
//			addAssociationToCache(topic, otherTopicObject, ass);
//			
//			
//		}else logger.info("Binary association already exist.");
//
//		setChanged(ass);
//		
//	}
//	
//	private void createNnaryAssociation(Topic topic, Object topicObject) throws BadAnnotationException{
//		
//		if(this.getValue(topicObject) == null)
//			return;
//		
//		Association ass = getAssociationFromCache(this.getValue(topicObject), topic);
//
//		if(ass == null)
//		{
//			logger.info("Create new nnary association " + this.associationType);
//			
//			ass = associationContainer.persist(topic.getTopicMap(), this.getValue(topicObject), this);
//
//			/// add own role as player to the association if not existing
//			addPlayer(ass, topic);
//			
//			// add association to cache
//			addAssociationToCache(topic, this.getValue(topicObject), ass);
//			
//		}else{
//			
//			logger.info("Nnary association already exist.");
//			
//			// invoke update of container /// TODO how to do it best?
//			ass = associationContainer.persist(topic.getTopicMap(), this.getValue(topicObject), this);
//		}
//		
//		setChanged(ass);
//	}
//	
//	private void addAssociationToCache(Topic topic, Object associationObject, Association association){ 
//		
//		AssociationIdent a = new AssociationIdent();
//		a.topic = topic;
//		a.associationObject = associationObject;
//		a.changed = true;
//		
//		if(this.cache == null)
//			this.cache = new HashMap<AssociationIdent, Association>();
//		
//		this.cache.put(a, association);
//	}
//	
//	private void setChanged(Association association)
//	{
//		if(this.cache == null)
//			return;
//		
//		for(Map.Entry<AssociationIdent, Association> entry:cache.entrySet()){
//			
//			if(entry.getValue().equals(association))
//				entry.getKey().changed = true;
//		}
//	}
//	
//	private void unsetChanded(){
//		
//		if(this.cache == null)
//			return;
//		
//		for(Map.Entry<AssociationIdent, Association> entry:cache.entrySet())
//			entry.getKey().changed = false;
//		
//	}
//	
//	private Association getAssociationFromCache(Object associationObject, Topic topic){
//		
//		if(this.cache == null)
//			return null;
//		
//		for(Map.Entry<AssociationIdent, Association> entry:cache.entrySet()){
//			
//			if(entry.getKey().associationObject == associationObject && entry.getKey().topic.equals(topic)){
//				return entry.getValue();								
//			}
//		}
//		
//		return null;
//	}
//	
//	private void removeObsoleteAssociations(Topic topic){
//		
//		if(this.cache == null)
//			return;
//		
//		Set<AssociationIdent> obsoleteEntries = new HashSet<AssociationIdent>();
//		
//		for(Map.Entry<AssociationIdent, Association> entry:this.cache.entrySet()){
//			
//			if(entry.getKey().topic.equals(topic) && entry.getKey().changed == false){
//				
//				logger.info("Remove obsolte association.");
//				
//				entry.getValue().remove();
//				
//				obsoleteEntries.add(entry.getKey());
//			}
//		}
//
//		for(AssociationIdent a:obsoleteEntries)
//			this.cache.remove(a);
//		
//		// set flags back to false
//		unsetChanded();
//	}
//
//	private void addPlayer(Association association, Topic player){
//	
//	Topic roleType = association.getTopicMap().getTopicBySubjectIdentifier(association.getTopicMap().createLocator(this.playedRole));
//	
//	if(roleType != null){
//		
//		Set<Role> roles = association.getRoles(roleType);
//		
//		for(Role role:roles){
//			if(role.getPlayer().equals(player))
//				return;
//		}
//	
//	}
//	
//	// add owner role
//	association.createRole(association.getTopicMap().createTopicBySubjectIdentifier(association.getTopicMap().createLocator(this.playedRole)), player);
//}

}
