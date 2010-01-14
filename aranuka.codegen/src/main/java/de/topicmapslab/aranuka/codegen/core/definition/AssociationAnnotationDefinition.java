package de.topicmapslab.aranuka.codegen.core.definition;

import org.tmapi.core.Role;
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

	private final String containerRole;
	private final String role;
	private final String type;
	private final String fieldName;
	private final String fieldType;
	private final ASSOCIATIONKIND ASSOCIATIONKIND;

	public AssociationAnnotationDefinition(final ASSOCIATIONKIND ASSOCIATIONKIND, final Topic associationType,
			final Topic roleType, final Role otherRole)
			throws POJOGenerationException {
		this.ASSOCIATIONKIND = ASSOCIATIONKIND;
		this.type = TypeUtility.getLocator(associationType).getReference();
		this.containerRole = TypeUtility.getLocator(roleType).getReference();
		this.role = TypeUtility.getLocator(otherRole.getType()).getReference();
		if (otherRole.getPlayer().getTypes().isEmpty()) {
			this.fieldType = "Object";
		} else {
			this.fieldType = TypeUtility.getJavaName(otherRole.getPlayer()
					.getTypes().iterator().next());
		}

		this.fieldName = this.fieldType.toLowerCase();
	}

	public AssociationAnnotationDefinition(ASSOCIATIONKIND ASSOCIATIONKIND, String assocTypeName,
			String roleName, String otherRoleName, String playerName) {
		this.type = assocTypeName;
		this.containerRole = roleName;
		this.role = otherRoleName;
		this.ASSOCIATIONKIND = ASSOCIATIONKIND;
		
		
		if (playerName.length()>0)
			fieldType = playerName;
		else
			fieldType = assocTypeName;
		

		this.fieldName = this.fieldType.toLowerCase();
	}

	public String getAnnotation() {
		return "@Association";
	}

	public String getAnnotationAttributes() {
		if (ASSOCIATIONKIND==ASSOCIATIONKIND.UNARY) {
			return "kind=ASSOCIATIONKIND.UNARY, container_role=\"" + containerRole + "\"";
		}
		
		return "container_role=\"" + containerRole + "\", role=\"" + role
				+ "\", type=\"" + type + "\"";
	}

	public String getFieldName() {
		return fieldName;
	}

	public Class<?> getFieldType() {
		throw new UnsupportedOperationException();
//		if (ASSOCIATIONKIND==ASSOCIATIONKIND.UNARY)
//			return boolean.class;
		
	}
	
	public String getPredefinition() {
		if (ASSOCIATIONKIND==ASSOCIATIONKIND.UNARY)
			return "";
		return " = new THashSet<" + fieldType + ">()";
	}

	public boolean doesFieldTypeExtendsCollection() {
		return ASSOCIATIONKIND!=ASSOCIATIONKIND.UNARY;
	}

	public String getTMQLType() {
		return "TraversalPlayers";
	}

	public String getTMQLFilterType() {
		return this.fieldType + ".class ,\"" + role + "\"";
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
}
