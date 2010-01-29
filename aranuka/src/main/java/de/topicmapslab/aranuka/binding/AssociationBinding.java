package de.topicmapslab.aranuka.binding;

import java.util.Map;

import de.topicmapslab.aranuka.enummerations.AssociationKind;

/**
 * Association field binding.
 * @author christian haß
 *
 */
public class AssociationBinding extends AbstractFieldBinding {

	/**
	 * The association type.
	 */
	private String associationType;
	/**
	 * The played role type.
	 */
	private String playedRole;
	/**
	 * The role type of the counter player. Only for binary associations.
	 */
	private String otherRole;
	/**
	 * The topic binding of the counter player. Only for binary associations.
	 */
	private TopicBinding otherPlayerBinding;
	/**
	 * The binding of the association container. Only for nnary associations.
	 */
	private AssociationContainerBinding associationContainerBinding;
	/**
	 * Flag indicating the associated topic should be persisted on cascade.
	 */
	private boolean persistOnCascade;
	
	/**
	 * The kind of the association, unary, binary or nnary.
	 */
	private AssociationKind kind;
	
	// --[ public methods ]--------------------------------------------------------------------------------
	
	/**
	 * Constructor
	 */
	public AssociationBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		super(prefixMap, parent);
	}
	

	/**
	 * Returns the association type.
	 * @return The association type as string.
	 */
	public String getAssociationType() {
		return associationType;
	}

	/**
	 * Sets the association type.
	 * @param associationTypeIdentifier - The identifier of the association type.
	 */
	public void setAssociationType(String associationTypeIdentifier) {
		this.associationType = associationTypeIdentifier;
	}

	/**
	 * Returns the type of the played role.
	 * @return The type as string.
	 */
	public String getPlayedRole() {
		return playedRole;
	}

	/**
	 * Sets the type of the played role.
	 * @param playedRole - The type as string.
	 */
	public void setPlayedRole(String playedRole) {
		this.playedRole = playedRole;
	}

	/**
	 * Returns the type of the counter player role.
	 * @return - The type as string.
	 */
	public String getOtherRole() {
		return otherRole;
	}

	/**
	 * Sets the type of the counter player role.
	 * @param otherRole - The type as string.
	 */
	public void setOtherRole(String otherRole) {
		this.otherRole = otherRole;
	}

	/**
	 * Returns the binding of the counter player.
	 * @return The topic binding.
	 */
	public TopicBinding getOtherPlayerBinding() {
		return otherPlayerBinding;
	}

	/**
	 * Sets the binding of the counter player-
	 * @param otherPlayer - The topic binding.
	 */
	public void setOtherPlayerBinding(TopicBinding otherPlayer) {
		this.otherPlayerBinding = otherPlayer;
	}

	/**
	 * Returns the association container binding.
	 * @return The container binding.
	 */
	public AssociationContainerBinding getAssociationContainerBinding() {
		return associationContainerBinding;
	}

	/**
	 * Sets the association container binding.
	 * @param associationContainer - The container binding.
	 */
	public void setAssociationContainerBinding(
			AssociationContainerBinding associationContainer) {
		this.associationContainerBinding = associationContainer;
	}

	/**
	 * Returns the value of the persist on cascade flag.
	 * @return The value.
	 */
	public boolean isPersistOnCascade() {
		return persistOnCascade;
	}

	/** 
	 * Sets the persist on cascade flag.
	 * @param persistOnCascade - The value.
	 */
	public void setPersistOnCascade(boolean persistOnCascade) {
		this.persistOnCascade = persistOnCascade;
	}

	/**
	 * Returns the kind of the association.
	 * @return The kind, unary, binary or nnary.
	 */
	public AssociationKind getKind() {
		return kind;
	}

	/**
	 * Sets the kind of the association.
	 * @param kind - The kind.
	 */
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
