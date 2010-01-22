package de.topicmapslab.aranuka;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinytim.mio.CTMTopicMapWriter;

import de.topicmapslab.aranuka.binding.AbstractClassBinding;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapInconsistentException;
import de.topicmapslab.aranuka.persist.TopicMapHandler;

public class Session {

	private static Logger logger = LoggerFactory.getLogger(Session.class);
	
	private Configuration config;
	TopicMapHandler topicMapHandler;
	
	// --[ public methods ]------------------------------------------------------------------------------
	
	public Session(Configuration config, boolean leazyBinding) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		if(config == null)
			throw new RuntimeException("Config must not be null."); /// TODO change exception type
		
		this.config = config;
				
		if(!leazyBinding){
			
			logger.info("Create bindings at the beginning.");
			getTopicMapHandler().invokeBinding();
		}
		
		
		
	}

	public void persist(Object topicObject) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, IOException, TopicMapInconsistentException {
		
		getTopicMapHandler().persist(topicObject);
	}
	
	public void flushTopicMap(){
		
		CTMTopicMapWriter writer;
		try {
			
			String filename = config.getProperty(Property.FILENAME);
			logger.info("Writing to "+filename);
			writer = new CTMTopicMapWriter(new FileOutputStream(filename), config.getBaseLocator());

			Map<String, String> prefixMap = config.getPrefixMap();
			for (String key : prefixMap.keySet()) {
				writer.addPrefix(key, prefixMap.get(key));
			}
			
			String bl = config.getBaseLocator();
			for (Class<?> clazz : config.getClasses()) {
				String prefix = bl+clazz.getName().replaceAll("\\.", "/")+"/";
				writer.addPrefix(clazz.getSimpleName().toLowerCase(), prefix);
			}

			writer.write(getTopicMapHandler().getTopicMap());
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public void getAll(Class<?> clazz){ //	returns all instances of the class in the topic map
		
	}
	
	public void getBySubjectIdentifier(String si){ //	returns instance of topic with si as subject identifier
		
	}
	
	public void getBySubjectLocator(String sl){ //	returns instance of topic with si as subject locator
		
	}
	
	public void getByItemIdentifier(String si){ //	returns instance of topic with si as item identifier
		
	}
	
	public void remove(Object object){ //	removes instance from the topic map
		
	}
	
	public void count(Class<?> clazz){ 
		
	}
	
	// getter and setter
	
	// --[ private methods ]-------------------------------------------------------------------------------
	
	private TopicMapHandler getTopicMapHandler(){
		
		if(this.topicMapHandler == null)
			topicMapHandler = new TopicMapHandler(config);
		
		return topicMapHandler;
	}

//	@Deprecated
//	private AssociationContainerBinding createAssociationContainerBinding(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
//		
//		logger.info("Create association container binding for " + clazz.getName());
//		
//		// check if class is correct annotated
//		if(!isAssociationContainerAnnotated(clazz))
//			throw new BadAnnotationException("Class " + clazz.getName() + " must have an @AssociationContainer annotation.");
//		
//		// create binding for superclass
//		Class<?> superclass = clazz.getSuperclass();
//		AssociationContainerBinding superClassBinding = null;
//		
//		if (superclass != Object.class){
//			
//			if(!this.config.getClasses().contains(superclass)){
//				throw new ClassNotSpecifiedException("Superclass of class " + clazz.getName() + " is not configured.");
//			}
//			
//			if(!isTopicAnnotated(superclass))
//				throw new BadAnnotationException("Superclass of class " + clazz.getName() + " must have an @AssociationContainer annotation as well.");
//			
//			superClassBinding = getAssociationContainerBinding(superclass);
//		}
//		
//		// create new binding
//		AssociationContainerBinding binding = new AssociationContainerBinding();
//
//		// add binding to map
//		addBinding(clazz, binding);
//		
//		// set parent
//		binding.setParent(superClassBinding);
//		
//		// create field bindings
//		for (Field field : clazz.getDeclaredFields())
//			createFieldBinding(binding, field, clazz);
//
//		return binding;
//	}
//
//	@Deprecated
//	private void createUnaryAssociationBinding(TopicBinding topicBinding, Field field, Class<?> clazz,  Association associationAnnotation, String associationType) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
//
//		// check if boolean
//		
//		if(ReflectionUtil.getGenericType(field) != boolean.class)
//			throw new BadAnnotationException("Unary association " +  field.getName() + " is not of type boolean.");
//		
//		// get played role
//		String playedRole = "";
//		
//		if(associationAnnotation.played_role().length() != 0)
//			playedRole = associationAnnotation.played_role();
//		
//		if(playedRole == "")
//			throw new BadAnnotationException("Unary association " + field.getName() + " needs an played_role attribute!");
//		
//		AssociationBinding ab = new AssociationBinding(config.getPrefixMap(), topicBinding);
//		
//		//ab.setKind(ASSOCIATIONKIND.UNARY);
//		ab.setAssociationType(TopicMapsUtils.resolveURI(associationType, config.getPrefixMap()));
//		ab.setPlayedRole(TopicMapsUtils.resolveURI(playedRole, config.getPrefixMap()));
//		
//		// add scope
//		addScope(field, ab);
//		ab.setArray(field.getType().isArray());
//		ab.setCollection(ReflectionUtil.isCollection(field));
//
//		// add association to topic binding
//		topicBinding.addFieldBinding(ab);
//		
//		// create methods
//		addMethods(field, clazz, ab);
//
//	}
//	
//	@Deprecated
//	private void createBinaryAssociationBinding(TopicBinding topicBinding, Field field, Class<?> clazz,  Association associationAnnotation, String associationType) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
//		
//		// get played role
//		
//		String playedRole = "";
//		
//		if(associationAnnotation.played_role().length() != 0)
//			playedRole = associationAnnotation.played_role();
//		
//		if(playedRole == "")
//			throw new BadAnnotationException("Binary association " + field.getName() + " needs an played_role attribute!");
//		
//		// get other role
//		
//		String otherRole = "";
//		
//		if(associationAnnotation.other_role().length() != 0)
//			otherRole = associationAnnotation.played_role();
//		
//		if(otherRole == "")
//			throw new BadAnnotationException("Binary association " + field.getName() + " needs an other_role attribute!");
//		
//		// get other player
//		Class<?> otherType = ReflectionUtil.getGenericType(field);
//		
//		if(!isTopicAnnotated(otherType))
//			throw new BadAnnotationException("Counter player of binary association " + field.getName() + " needs to be an annotated topic class.");
//			
//		
//		AssociationBinding ab = new AssociationBinding(config.getPrefixMap(), topicBinding);
//		
//		//ab.setKind(ASSOCIATIONKIND.BINARY);
//		ab.setAssociationType(TopicMapsUtils.resolveURI(associationType, config.getPrefixMap()));
//		ab.setPlayedRole(TopicMapsUtils.resolveURI(playedRole, config.getPrefixMap()));
//		ab.setOtherRole(otherRole);
//		ab.setOtherPlayer(getTopicBinding(otherType));
//		
//		// add scope
//		addScope(field, ab);
//		
//		ab.setArray(field.getType().isArray());
//		ab.setCollection(ReflectionUtil.isCollection(field));
//
//		// add association to topic binding
//		topicBinding.addFieldBinding(ab);
//		
//		// create methods
//		addMethods(field, clazz, ab);
//		
//	}
//
//	@Deprecated
//	private void createNnaryAssociationBinding(TopicBinding topicBinding, Field field, Class<?> clazz,  Association associationAnnotation, String associationType) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
//		
//		// get played role
//		
//		String playedRole = "";
//		
//		if(associationAnnotation.played_role().length() != 0)
//			playedRole = associationAnnotation.played_role();
//		
//		if(playedRole == "")
//			throw new BadAnnotationException("Binary association " + field.getName() + " needs an played_role attribute!");
//		
//		
//		// get container
//		
//		Class<?> container = ReflectionUtil.getGenericType(field);
//		AssociationContainerBinding containerBinding = getAssociationContainerBinding(container);
//		
//		AssociationBinding ab = new AssociationBinding(config.getPrefixMap(), topicBinding);
//		
//		//ab.setKind(ASSOCIATIONKIND.NNARY);
//		ab.setAssociationType(TopicMapsUtils.resolveURI(associationType, config.getPrefixMap()));
//		ab.setPlayedRole(playedRole);
//		ab.setAssociationContainer(containerBinding);
//		
//		// add scope
//		addScope(field, ab);
//		
//		ab.setArray(field.getType().isArray());
//		ab.setCollection(ReflectionUtil.isCollection(field));
//
//		// add association to topic binding
//		topicBinding.addFieldBinding(ab);
//		
//		// create methods
//		addMethods(field, clazz, ab);
//		
//	}
//
//	

}
