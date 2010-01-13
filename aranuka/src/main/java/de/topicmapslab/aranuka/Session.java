package de.topicmapslab.aranuka;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.binding.TopicBinding;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;

public class Session {

	private static Logger logger = LoggerFactory.getLogger(Session.class);
	
	private Map<Class<?>, TopicBinding> bindingMap;
	private Configuration config;
	
	public Session(Configuration config, boolean leazyBinding) throws BadAnnotationException, ClassNotSpecifiedException{
		
		this.config = config;
		
		if(!leazyBinding){
			
			logger.info("Create bindings at the beginning.");
			createBindings(config.getClasses());
		}
		
	}
	
	private void createBindings(Set<Class<?>> classes) throws BadAnnotationException, ClassNotSpecifiedException{
		
		for(Class<?> clazz:classes)
			addBinding(clazz, createTopicBinding(clazz));
	}
	
	private TopicBinding createTopicBinding(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException {
		logger.info("Create binding for " + clazz.getName());
		
		// check if class is annotated
		
		if(!isTopicAnnotated(clazz))
			throw new BadAnnotationException("Class " + clazz.getName() + " must have an @Topic annotation.");

		// create binding for superclass
		
		Class<?> superclass = clazz.getSuperclass();
		TopicBinding superClassBinding = null;
		
		if (superclass != Object.class){
			
			if(!this.config.getClasses().contains(superclass)){
				throw new ClassNotSpecifiedException("Superclass of class " + clazz.getName() + " is not configured.");
			}
			
			if(!isTopicAnnotated(superclass))
				throw new BadAnnotationException("Superclass of class " + clazz.getName() + " must have an @Topic annotation as well.");
			
			superClassBinding = getTopicBinding(superclass);
		}
		
		Topic topicAnnotation = clazz.getAnnotation(Topic.class);
		TopicBinding binding = new TopicBinding();

		// set parent
		binding.setParent(superClassBinding);
		
		// set name
//		if (topicAnnotation.name().length() > 0) {
//			binding.setName(topicAnnotation.name());
//		} else {
//			binding.setName(clazz.getSimpleName());
//		}
		
		// set subject identifier
//		if (topicAnnotation.subject_identifier() != null) {
//			
//			for (String s : topicAnnotation.subject_identifier())
//				binding.addIdentifier(resolveURI(s));
//		}else{
//			
//			// create subject identifier
//			StringBuilder builder = new StringBuilder();
//			String nameSuffix = clazz.getName().replaceAll("\\.", "/");
//			if (config.getBaseLocator() != null) {
//				builder.append(config.getBaseLocator());
//				if (!(config.getBaseLocator().endsWith("/")))
//					builder.append("/");
//			}else
//				builder.append("base_locator:");
//			
//			builder.append(nameSuffix);
//			binding.addIdentifier(builder.toString());
//		}

		return binding;
	}

	private TopicBinding getTopicBinding(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException{
		
		if(bindingMap.get(clazz) != null)
			return bindingMap.get(clazz);
		
		TopicBinding binding = createTopicBinding(clazz);
		addBinding(clazz, binding);
		
		return binding;
	}
	
	private boolean isTopicAnnotated(Class<?> clazz){
		
		Topic topic = clazz.getAnnotation(Topic.class);
		if (topic != null)
			return true;
		
		return false;
	}
	
	private boolean isAssociationAnnotated(Class<?> clazz){
		
		AssociationContainer associationContainer = clazz.getAnnotation(AssociationContainer.class);
		if(associationContainer != null)
			return true;
		
		return false;
	}
	

	private void addBinding(Class<?> clazz, TopicBinding binding){
		
		if (bindingMap == null)
			bindingMap = new HashMap<Class<?>, TopicBinding>();
		
		bindingMap.put(clazz, binding);
		
	}
	
	private String resolveURI(String uri) {
		int idx = uri.indexOf(":");
		if (idx != -1) {

			String key = uri.substring(0, idx);
			if (this.config.getPrefixMap().get(key) != null) {
				StringBuilder builder = new StringBuilder(this.config.getPrefixMap().get(key));
				builder.append(uri.substring(idx + 1));
				return builder.toString();
			}
		}
		return uri;
	}
	
}
