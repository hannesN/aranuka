package de.topicmapslab.aranuka.codegen.core.definition;

import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
import de.topicmapslab.aranuka.codegen.core.utility.TypeUtility;
import de.topicmapslab.aranuka.codegen.model.definition.FieldDefinition;

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

	public AssociationAnnotationDefinition(final Topic associationType,
			final Topic roleType, final Role otherRole)
			throws POJOGenerationException {
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

	public AssociationAnnotationDefinition(String assocTypeName,
			String roleName, String otherRoleName, String playerName) {
		this.type = assocTypeName;
		this.containerRole = roleName;
		this.role = otherRoleName;
		
		fieldType = playerName;

		this.fieldName = this.fieldType.toLowerCase();
	}

	public String getAnnotation() {
		return "@Association";
	}

	public String getAnnotationAttributes() {
		return "container_role=\"" + containerRole + "\", role=\"" + role
				+ "\", type=\"" + type + "\"";
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldType() {
		return "Set<" + fieldType + ">";
	}

	public String getPredefinition() {
		return " = new THashSet<" + fieldType + ">()";
	}

	public boolean doesFieldTypeExtendsCollection() {
		return true;
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
			return def.getFieldName().equalsIgnoreCase(getFieldName())
					&& def.getFieldType().equalsIgnoreCase(getFieldType());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getFieldName().hashCode() * 999999 + getFieldType().hashCode();
	}
}
