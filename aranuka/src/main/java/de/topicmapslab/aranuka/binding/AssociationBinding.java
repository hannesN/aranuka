package de.topicmapslab.aranuka.binding;

import java.util.Map;

import de.topicmapslab.aranuka.enummerations.AssociationKind;

public class AssociationBinding extends AbstractFieldBinding {

	private String associationType; // the association type as string
	private String playedRole; // the own role type
	private String otherRole; // the role type of the counter player (if no ass. container is used)
	private TopicBinding otherPlayerBinding; // the topic binding of the counter player (if no ass. container is used)
	private AssociationContainerBinding associationContainerBinding; // binding of the ass. container
	private boolean persistOnCascade; // persist counter player completely if this topic is persisted
	
	private AssociationKind kind;
	
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

	public TopicBinding getOtherPlayerBinding() {
		return otherPlayerBinding;
	}

	public void setOtherPlayerBinding(TopicBinding otherPlayer) {
		this.otherPlayerBinding = otherPlayer;
	}

	public AssociationContainerBinding getAssociationContainerBinding() {
		return associationContainerBinding;
	}

	public void setAssociationContainerBinding(
			AssociationContainerBinding associationContainer) {
		this.associationContainerBinding = associationContainer;
	}

	public boolean isPersistOnCascade() {
		return persistOnCascade;
	}

	public void setPersistOnCascade(boolean persistOnCascade) {
		this.persistOnCascade = persistOnCascade;
	}

	public AssociationKind getKind() {
		return kind;
	}

	public void setKind(AssociationKind kind) {
		this.kind = kind;
	}
	
	@Override
	public String toString() {
		return "AssociationBinding [associationContainer="
				+ associationContainerBinding + ", associationType=" + associationType
				+ ", otherPlayer=" + otherPlayerBinding
				+ ", otherRole=" + otherRole + ", playedRole=" + playedRole
				+ "]";
	}
}
