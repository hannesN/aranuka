package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Association;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.annotations.ASSOCIATIONKIND;

public class AssociationBinding extends AbstractFieldBinding {

	private static Logger logger = LoggerFactory.getLogger(AssociationBinding.class);
	
	private ASSOCIATIONKIND kind;
	private String associationType;
	private String playedRole;
	private String otherRole;
	private TopicBinding otherPlayer;
	private AssociationContainerBinding associationContainer;
	
	public AssociationBinding(Map<String,String> prefixMap, AbstractBinding parent) {
		super(prefixMap, parent);
	}
	
	public void persist(Topic topic, Object topicObject){

		logger.info("Persist association field.");
		
		if(this.kind == ASSOCIATIONKIND.UNARY)
			createUnaryAssociation(topic, topicObject);
		
		else if(this.kind == ASSOCIATIONKIND.BINARY)
			createBinaryAssociation(topic, topicObject);
		
		else if(this.kind == ASSOCIATIONKIND.NNARY)
			createNnaryAssociation(topic, topicObject);
		else
			throw new RuntimeException("Association type not set!");
		
	}
	

	private void createUnaryAssociation(Topic topic, Object topicObject){
		
		logger.info("Create unary association " + this.associationType);
		
		if((Boolean)this.getValue(topicObject)){
			
			Association ass = topic.getTopicMap().createAssociation(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(associationType)), getScope(topic.getTopicMap()));
			
			ass.createRole(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(playedRole)), topic);
			
		}
	}
	
	private void createBinaryAssociation(Topic topic, Object topicObject){
		
		logger.info("Create binary association " + this.associationType);
		
		
		if(this.isArray()){
//			for (Object obj:(Object[])this.getValue(topicObject))
//				createName(topic, obj.toString());
			
		}else if(this.isCollection()){
				
//			for (Object obj:(Collection<Object>)this.getValue(topicObject))
//				createName(topic, obj.toString());

		}else{ 

//			if(this.getValue(topicObject) != null)
//				createName(topic, this.getValue(topicObject).toString());
			
		}
		
		
	}
	
	private void createNnaryAssociation(Topic topic, Object topicObject){
		
		logger.info("Create Nnary association " + this.associationType);

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
