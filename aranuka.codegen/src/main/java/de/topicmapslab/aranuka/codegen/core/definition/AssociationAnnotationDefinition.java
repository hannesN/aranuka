package de.topicmapslab.aranuka.codegen.core.definition;

import java.util.Set;

import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.codegen.core.definition.enumeration.AssociationKind;
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
	
	private final Topic assocType;
	private final Topic roleType;
	
	private final Set<AssocOtherPlayers> otherPlayers;

	private String containerTypeName;

	public AssociationAnnotationDefinition(Topic assocType, Topic roleType, Set<AssocOtherPlayers> otherplayers) throws POJOGenerationException {
		this.roleType = roleType;
		this.assocType = assocType;
		
		this.type = TypeUtility.getLocator(assocType).toExternalForm();
		this.role = TypeUtility.getLocator(roleType).toExternalForm();
		
		this.otherPlayers = otherplayers;		
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
		
		try {
			switch (otherPlayers.size()) {
			case 0:
				return TypeUtility.getTypeAttribute(roleType);
			case 1:
				return TypeUtility.getTypeAttribute(otherPlayers.iterator()
						.next().otherRole);
			default:
				return TypeUtility.getTypeAttribute(assocType);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getRoleType() {
		return role;
	}

	public String getAssociationType() {
		return type;
	}

	public AssociationKind getAssocKind() {
		switch (otherPlayers.size()) {
		case 0:
			return AssociationKind.UNARY;
		case 1:
			return AssociationKind.BINARY;
		default:
			return AssociationKind.NNARY;
		}
	}
	
	public Class<?> getFieldType() {
		if (getAssocKind() == AssociationKind.UNARY)
			return boolean.class;

		throw new UnsupportedOperationException();
	}

	public String getPredefinition() {
		throw new UnsupportedOperationException();
	}

	public boolean doesFieldTypeExtendsCollection() {
		return getAssocKind() != AssociationKind.UNARY;
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
	
	public void setContainerTypeName(String containerTypeName) {
		this.containerTypeName = containerTypeName;
	}
	
	public String getContainerTypeName() {
		return containerTypeName;
	}
	
	static public class AssocOtherPlayers extends FieldDefinition {
		private Topic otherRole = null;			
		private Topic otherPlayer = null;

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

		public String getAnnotation() {
			throw new UnsupportedOperationException();
		}

		public String getAnnotationAttributes() {
			throw new UnsupportedOperationException();
		}

		public String getFieldName() {
			try {
				return TypeUtility.getJavaName(otherPlayer);
			} catch (POJOGenerationException e) {
				throw new RuntimeException(e);
			}
		}

		public Class<?> getFieldType() {
			throw new UnsupportedOperationException();
		}

		public String getPredefinition() {
			throw new UnsupportedOperationException();
		}
		
	}

}
