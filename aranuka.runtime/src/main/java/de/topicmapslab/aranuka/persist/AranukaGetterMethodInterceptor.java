/**
 * 
 */
package de.topicmapslab.aranuka.persist;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.binding.AbstractFieldBinding;
import de.topicmapslab.aranuka.binding.AssociationBinding;
import de.topicmapslab.aranuka.binding.IdBinding;
import de.topicmapslab.aranuka.binding.NameBinding;
import de.topicmapslab.aranuka.binding.OccurrenceBinding;

/**
 * @author Hannes Niederhausen
 *
 */
public class AranukaGetterMethodInterceptor implements MethodInterceptor {

	private TopicMapHandler handler;
	
	private Topic topic;
	
	private List<AbstractFieldBinding> bindings;
	
	
	
	public AranukaGetterMethodInterceptor(TopicMapHandler handler, Topic topic, List<AbstractFieldBinding> bindings) {
		super();
		this.handler = handler;
		this.topic = topic;
		this.bindings = bindings;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// we only want to intercept getters
		if ( (!method.getName().startsWith("get")) && (!method.getName().startsWith("is")) )
			return proxy.invokeSuper(obj, args);
		
		for (AbstractFieldBinding b : bindings) {
			if (b.getGetter().equals(method)) {
				return retrieveData(obj, b, proxy);
			} 
		}
		
		return proxy.invokeSuper(obj, new Object[0]);
	}


	/**
	 * @param obj
	 * @param binding 
	 * @param method
	 * @param proxy
	 * @return
	 * @throws Throwable 
	 */
	private Object retrieveData(Object obj, AbstractFieldBinding binding, MethodProxy proxy) throws Throwable {
		
		if (binding instanceof IdBinding) {
			handler.addIdentifier(topic, obj, (IdBinding) binding);
		} else if (binding instanceof NameBinding) {
			handler.addName(topic, obj, (NameBinding) binding);
		} else if (binding instanceof OccurrenceBinding) {
			handler.addOccurrence(topic, obj, (OccurrenceBinding) binding);
		} else if (binding instanceof AssociationBinding) {
			handler.addAssociation(topic, obj, (AssociationBinding) binding);
		}
		
		// remove binding so we don't call it again for this proxy
		bindings.remove(binding);
		
		return proxy.invokeSuper(obj, new Object[0]);
	}

}
