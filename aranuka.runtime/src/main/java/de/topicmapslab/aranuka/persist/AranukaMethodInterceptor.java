/**
 * 
 */
package de.topicmapslab.aranuka.persist;

import java.util.List;

import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.binding.AbstractFieldBinding;
import de.topicmapslab.aranuka.binding.AssociationBinding;
import de.topicmapslab.aranuka.binding.IdBinding;
import de.topicmapslab.aranuka.binding.NameBinding;
import de.topicmapslab.aranuka.binding.OccurrenceBinding;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.proxy.IMethodInterceptor;

/**
 * @author Hannes Niederhausen
 *
 */
public class AranukaMethodInterceptor implements IMethodInterceptor {
	private TopicMapHandler handler;
    private Topic topic;
    private List<AbstractFieldBinding> bindings;

    public AranukaMethodInterceptor(TopicMapHandler handler, Topic topic, List<AbstractFieldBinding> bindings) {
        super();
        this.handler = handler;
        this.topic = topic;
        this.bindings = bindings;
}
    
	@Override
	public Object methodCalled(Object instance, String name, Object... param) {
		
		// we only want to intercept getters
		if ((name.startsWith("get")) || (name.startsWith("is"))) {
			for (AbstractFieldBinding b : bindings) {
				try {
					if (b.getGetter().getName().equals(name)) {
						if (b instanceof NameBinding) {
							handler.addName(topic, instance, (NameBinding) b);
						} else if (b instanceof OccurrenceBinding) {
							handler.addOccurrence(topic, instance, (OccurrenceBinding) b);
						} else if (b instanceof IdBinding) {
							handler.addIdentifier(topic, instance, (IdBinding) b);
						} else if (b instanceof AssociationBinding) {
							handler.addAssociation(topic, instance, (AssociationBinding) b);
						} else {
							return null;
						}
						this.bindings.remove(b);
						return null;
					}
				} catch (Exception e) {
					throw new RuntimeException(new AranukaException(e));
				}
			}
		}
		
		
		return null;
	}

}
