package de.topicmapslab.aranuka.codegen.core.factory;

import gnu.trove.THashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

import annoTM.core.annotations.ASSOCKIND;

import de.topicmapslab.aranuka.codegen.core.definition.AssociationAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.exception.POJOGenerationException;
import de.topicmapslab.aranuka.codegen.core.utility.TypeUtility;

/**
 * 
 * @author Sven Krosse
 *
 */
public class AssociationAnnotationFactory implements IAssociationAnnotationFactory {

	private final Map<Topic, Set<AssociationAnnotationDefinition>> associationsAnnotationsDefinitions = new HashMap<Topic, Set<AssociationAnnotationDefinition>>();

	private final TopicMap topicMap;

	public AssociationAnnotationFactory(final TopicMap topicMap)
			throws POJOGenerationException {
		this.topicMap = topicMap;
		init(topicMap);

	}

	private void init(final TopicMap topicMap) throws POJOGenerationException {

		// TODO replace by using TMQL
		TypeInstanceIndex index = topicMap.getIndex(TypeInstanceIndex.class);
		/*
		 * iterate over all association types
		 */
		for (Topic associationType : index.getAssociationTypes()) {
			/*
			 * iterate over all association items
			 */
			for (Association association : index
					.getAssociations(associationType)) {
				/*
				 * iterate over all role
				 */
				Role[] roles = association.getRoles().toArray(new Role[0]);
				for (int i = 0; i < roles.length; i++) {
					Role r1 = roles[i];

					if (r1.getPlayer().getTypes().isEmpty()) {
						Logger.getLogger(getClass()).warn(
								"Ignore Role: "
										+ TypeUtility
												.getLocator(r1.getPlayer())
												.getReference());
						continue;
					}

					Set<AssociationAnnotationDefinition> setR1 = associationsAnnotationsDefinitions
							.get(r1.getPlayer().getTypes().iterator().next());
					if (setR1 == null) {
						setR1 = new THashSet<AssociationAnnotationDefinition>();
					}
					for (int j = i + 1; j < roles.length; j++) {
						Role r2 = roles[j];
						if (r2.getPlayer().getTypes().isEmpty()) {
							Logger.getLogger(getClass()).warn(
									"Ignore Role: "
											+ TypeUtility.getLocator(
													r2.getPlayer())
													.getReference());
							continue;
						}

						Set<AssociationAnnotationDefinition> setR2 = associationsAnnotationsDefinitions
								.get(r2.getPlayer().getTypes().iterator()
										.next());
						if (setR2 == null) {
							setR2 = new THashSet<AssociationAnnotationDefinition>();
						}

						AssociationAnnotationDefinition aadR1 = new AssociationAnnotationDefinition(ASSOCKIND.BINARY,
								associationType, r1.getPlayer().getTypes()
										.iterator().next(), r2);
						setR1.add(aadR1);

						AssociationAnnotationDefinition aadR2 = new AssociationAnnotationDefinition(ASSOCKIND.BINARY,
								associationType, r2.getPlayer().getTypes()
										.iterator().next(), r1);
						setR2.add(aadR2);

						associationsAnnotationsDefinitions.put(r2.getPlayer()
								.getTypes().iterator().next(), setR2);
					}
					associationsAnnotationsDefinitions.put(r1.getPlayer()
							.getTypes().iterator().next(), setR1);
				}
			}
		}
	}

	public TopicMap getTopicMap() {
		return topicMap;
	}

	/* (non-Javadoc)
	 * @see de.topicmapslab.aranuka.codegen.core.factory.IAssociationAnnotationFactory#getAssociationAnnotationDefinitions(org.tmapi.core.Topic)
	 */
	public Set<AssociationAnnotationDefinition> getAssociationAnnotationDefinitions(
			final Topic roleType) {
		if (associationsAnnotationsDefinitions.containsKey(roleType)) {
			return associationsAnnotationsDefinitions.get(roleType);
		}
		return new THashSet<AssociationAnnotationDefinition>();
	}

}
