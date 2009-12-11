package de.topicmapslab.aranuka.codegen.core.definition;

import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import annoTM.core.annotations.ASSOCKIND;
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
	private final ASSOCKIND assocKind;

	public AssociationAnnotationDefinition(final ASSOCKIND assocKind, final Topic associationType,
			final Topic roleType, final Role otherRole)
			throws POJOGenerationException {
		this.assocKind = assocKind;
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

	public AssociationAnnotationDefinition(ASSOCKIND assocKind, String assocTypeName,
			String roleName, String otherRoleName, String playerName) {
		this.type = assocTypeName;
		this.containerRole = roleName;
		this.role = otherRoleName;
		this.assocKind = assocKind;
		
		
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
		if (assocKind==ASSOCKIND.UNARY) {
			return "kind=ASSOCKIND.UNARY, container_role=\"" + containerRole + "\"";
		}
		
		return "container_role=\"" + containerRole + "\", role=\"" + role
				+ "\", type=\"" + type + "\"";
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldType() {
		if (assocKind==ASSOCKIND.UNARY)
			return "boolean";
		
		return "Set<" + fieldType + ">";
	}
	
	public String getPredefinition() {
		if (assocKind==ASSOCKIND.UNARY)
			return "";
		return " = new THashSet<" + fieldType + ">()";
	}

	public boolean doesFieldTypeExtendsCollection() {
		return assocKind!=ASSOCKIND.UNARY;
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
