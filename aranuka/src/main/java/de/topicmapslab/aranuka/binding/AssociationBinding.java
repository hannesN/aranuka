package de.topicmapslab.aranuka.binding;

import de.topicmapslab.aranuka.annotations.ASSOCIATIONKIND;

public class AssociationBinding extends AbstractFieldBinding {

	private ASSOCIATIONKIND kind;
	private String associationType;
	private String playedRole;
	private String otherRole;
	private TopicBinding otherPlayer;
	private AssociationContainerBinding associationContainer;
	
	public AssociationBinding(AbstractBinding parent) {
		super(parent);
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
