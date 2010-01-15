package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Association;
import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.annotations.ASSOCIATIONKIND;
import de.topicmapslab.aranuka.exception.BadAnnotationException;

public class AssociationBinding extends AbstractTopicFieldBinding {

	private static Logger logger = LoggerFactory.getLogger(AssociationBinding.class);
	
	private ASSOCIATIONKIND kind;
	private String associationType;
	private String playedRole;
	private String otherRole;
	private TopicBinding otherPlayer;
	private AssociationContainerBinding associationContainer;
	
	private Map<AssociationIdent, Association> cache;
	
	private class AssociationIdent{
		
		Topic topic;
		Object associationObject; // either null(uniary), the counter object(binary) or the association container(nnary)
		boolean changed;
		
	}
	
	
	private void addAssociationToCache(Topic topic, Object associationObject, Association association){ 
		
		AssociationIdent a = new AssociationIdent();
		a.topic = topic;
		a.associationObject = associationObject;
		a.changed = true;
		
		if(this.cache == null)
			this.cache = new HashMap<AssociationIdent, Association>();
		
		this.cache.put(a, association);
	}
	
	private void setChanged(Association association)
	{
		if(this.cache == null)
			return;
		
		for(Map.Entry<AssociationIdent, Association> entry:cache.entrySet()){
			
			if(entry.getValue().equals(association))
				entry.getKey().changed = true;
		}
	}
	
	private void unsetChanded(){
		
		if(this.cache == null)
			return;
		
		for(Map.Entry<AssociationIdent, Association> entry:cache.entrySet())
			entry.getKey().changed = false;
		
	}
	
	private Association getAssociationFromCache(Object associationObject, Topic topic){
		
		if(this.cache == null)
			return null;
		
		for(Map.Entry<AssociationIdent, Association> entry:cache.entrySet()){
			
			if(entry.getKey().associationObject == associationObject && entry.getKey().topic.equals(topic)){
				return entry.getValue();								
			}
		}
		
		return null;
	}
	
	private void removeObsoleteAssociations(Topic topic){
		
		if(this.cache == null)
			return;
		
		Set<AssociationIdent> obsoleteEntries = new HashSet<AssociationIdent>();
		
		for(Map.Entry<AssociationIdent, Association> entry:this.cache.entrySet()){
			
			if(entry.getKey().topic.equals(topic) && entry.getKey().changed == false){
				
				logger.info("Remove obsolte association.");
				
				entry.getValue().remove();
				
				obsoleteEntries.add(entry.getKey());
			}
		}

		for(AssociationIdent a:obsoleteEntries)
			this.cache.remove(a);
		
		// set flags back to false
		unsetChanded();
	}
	
	
	
	public AssociationBinding(Map<String,String> prefixMap, AbstractBinding parent) {
		super(prefixMap, parent);
	}
	
	public void persist(Topic topic, Object topicObject) throws BadAnnotationException{

		//logger.info("Persist association field.");
		
		if(this.kind == ASSOCIATIONKIND.UNARY)
			createUnaryAssociation(topic, topicObject);
		
		else if(this.kind == ASSOCIATIONKIND.BINARY)
			createBinaryAssociation(topic, topicObject);
		
		else if(this.kind == ASSOCIATIONKIND.NNARY)
			createNnaryAssociation(topic, topicObject);
		else
			throw new RuntimeException("Association type not set!");
	
		
		// remove obsolete associations
		removeObsoleteAssociations(topic);
		
	}
	

	private void createUnaryAssociation(Topic topic, Object topicObject){
		
		if((Boolean)this.getValue(topicObject)){
			
			Association ass = getAssociationFromCache(null, topic);
			
			if(ass == null)
			{
				logger.info("Create unary association " + this.associationType);
				ass = topic.getTopicMap().createAssociation(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(associationType)), getScope(topic.getTopicMap()));
				ass.createRole(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(playedRole)), topic);
				
				// add association to cache
				addAssociationToCache(topic, null, ass);
				
			}else logger.info("Unary association already exist.");
			
			setChanged(ass);
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createBinaryAssociation(Topic topic, Object topicObject) throws BadAnnotationException{
		
		//logger.info("Create binary association " + this.associationType);
		
		if(this.getValue(topicObject) == null)
			return;
		
		if(this.isArray()){
			
			for (Object obj:(Object[])this.getValue(topicObject))
				createBinaryAssociation(topic, topicObject, obj);
			
		}else if(this.isCollection()){
				
			for (Object obj:(Collection<Object>)this.getValue(topicObject))
				createBinaryAssociation(topic, topicObject, obj);

		}else{ 
			createBinaryAssociation(topic, topicObject, this.getValue(topicObject));
		}
	}
	
	private void createBinaryAssociation(Topic topic, Object topicObject, Object otherTopicObject) throws BadAnnotationException{
		
		Association ass = getAssociationFromCache(otherTopicObject, topic);
		
		if(ass == null)
		{
			logger.info("Create new binary association.");
			
			// create other player
			Topic otherTopic = otherPlayer.persist(topic.getTopicMap(), otherTopicObject);
			ass = topic.getTopicMap().createAssociation(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(associationType)), getScope(topic.getTopicMap()));
			
			ass.createRole(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(this.playedRole)), topic);
			ass.createRole(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(this.otherRole)), otherTopic);

			// add association to cache
			addAssociationToCache(topic, otherTopicObject, ass);
			
			
		}else logger.info("Binary association already exist.");

		setChanged(ass);
		
	}
	
	private void createNnaryAssociation(Topic topic, Object topicObject) throws BadAnnotationException{
		
		if(this.getValue(topicObject) == null)
			return;
		
		Association ass = getAssociationFromCache(this.getValue(topicObject), topic);

		if(ass == null)
		{
			logger.info("Create new nnary association " + this.associationType);
			
			ass = associationContainer.persist(topic.getTopicMap(), this.getValue(topicObject), this);
			
			// add association to cache
			addAssociationToCache(topic, this.getValue(topicObject), ass);
			
		}else{
			
			logger.info("Nnary association already exist.");
			
			// invoke update of container
			ass = associationContainer.persist(topic.getTopicMap(), this.getValue(topicObject), this);
		}
		
		setChanged(ass);
	}
	

	public String getAssociationType() {
		return associationType;
	}

	public void setAssociationType(String associationTypeIdentifier) {
		this.associationType = associationTypeIdentifier;
	}

	public ASSOCIATIONKIND getKind() {
		return kind;
	}

	public void setKind(ASSOCIATIONKIND kind) {
		this.kind = kind;
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

	@Override
	public String toString() {
		return "AssociationBinding [associationContainer="
				+ associationContainer + ", associationType=" + associationType
				+ ", kind=" + kind + ", otherRole=" + otherRole
				+ ", playedRole=" + playedRole + "]";
	}

	
	
	
	
}
