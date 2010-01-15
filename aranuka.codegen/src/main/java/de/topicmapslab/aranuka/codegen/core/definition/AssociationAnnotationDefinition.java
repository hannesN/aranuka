package de.topicmapslab.aranuka.codegen.core.definition;

import java.util.Set;

import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.annotations.ASSOCIATIONKIND;
import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
import de.topicmapslab.aranuka.codegen.core.util.TypeUtility;

/**
 * 
 * @author Sven Krosse
 * 
 */
public class AssociationAnnotationDefinition extends FieldDefinition {

	private final String role;
	private final String type;
	private final String fieldName;
	
	private final ASSOCIATIONKIND assocKind;
	private final Set<AssocOtherPlayers> otherPlayers;
	

	public AssociationAnnotationDefinition(Topic assocType, Topic roleType, Set<AssocOtherPlayers> otherplayers) throws POJOGenerationException {
		this.type = TypeUtility.getLocator(assocType).toExternalForm();
		
		this.role = TypeUtility.getLocator(roleType).toExternalForm();
		
		this.otherPlayers = otherplayers;
		
		switch (otherplayers.size()) {
		case 0:
			assocKind = ASSOCIATIONKIND.UNARY;
			break;
		case 1:
			assocKind = ASSOCIATIONKIND.BINARY;
			break;
		default:
			assocKind = ASSOCIATIONKIND.NNARY;
		}
		
		fieldName = TypeUtility.getTypeAttribute(roleType);
		
	}

	public String getAnnotation() {
		throw new UnsupportedOperationException();
	}

	public Set<AssocOtherPlayers> getOtherPlayers() {
		return otherPlayers;
	}
	
	public String getAnnotationAttributes() {
		throw new UnsupportedOperationException();
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getRoleType() {
		return role;
	}

	public String getAssociationType() {
		return type;
	}

	public ASSOCIATIONKIND getAssocKind() {
		return assocKind;
	}
	
	public Class<?> getFieldType() {
		if (assocKind == ASSOCIATIONKIND.UNARY)
			return boolean.class;

		throw new UnsupportedOperationException();
	}

	public String getPredefinition() {
		throw new UnsupportedOperationException();
	}

	public boolean doesFieldTypeExtendsCollection() {
		return assocKind != ASSOCIATIONKIND.UNARY;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AssociationAnnotationDefinition) {
			AssociationAnnotationDefinition def = (AssociationAnnotationDefinition) obj;
			return def.getFieldName().equalsIgnoreCase(getFieldName());

		}
		return false;
	}

	@Override
	public int hashCode() {
		return getFieldName().hashCode() * 999999;
	}
	
	static public class AssocOtherPlayers {
		Topic otherRole = null;			
		Topic otherPlayer = null;
		public AssocOtherPlayers(Topic otherRole, Topic otherPlayer) {
			super();
			this.otherRole = otherRole;
			this.otherPlayer = otherPlayer;
		}
		
		public Topic getPlayer() {
			return otherPlayer;
		}
		
		public Topic getRole() {
			return otherRole;
		}
		
	}

}
