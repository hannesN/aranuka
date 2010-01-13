package de.topicmapslab.aranuka;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinytim.mio.CTMTopicMapWriter;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.aranuka.annotations.ASSOCIATIONKIND;
import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.IDTYPE;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Role;
import de.topicmapslab.aranuka.annotations.Scope;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.binding.AbstractBinding;
import de.topicmapslab.aranuka.binding.AbstractFieldBinding;
import de.topicmapslab.aranuka.binding.AssociationBinding;
import de.topicmapslab.aranuka.binding.AssociationContainerBinding;
import de.topicmapslab.aranuka.binding.IdBinding;
import de.topicmapslab.aranuka.binding.NameBinding;
import de.topicmapslab.aranuka.binding.OccurrenceBinding;
import de.topicmapslab.aranuka.binding.RoleBinding;
import de.topicmapslab.aranuka.binding.TopicBinding;
import de.topicmapslab.aranuka.constants.IXsdDatatypes;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.utils.ReflectionUtil;
import de.topicmapslab.aranuka.utils.TopicMapsUtils;

public class Session {

	private static Logger logger = LoggerFactory.getLogger(Session.class);
	
	private Map<Class<?>, AbstractBinding> bindingMap;
	private Configuration config;
	
	private TopicMap topicMap;
	
	/**
	 * Constructor.
	 * 
	 * @param config
	 * @param leazyBinding
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	public Session(Configuration config, boolean leazyBinding) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		this.config = config;
		
		bindingMap = new HashMap<Class<?>, AbstractBinding>();
		
		if(!leazyBinding){
			
			logger.info("Create bindings at the beginning.");
			createBindings(config.getClasses());
		}
	}
	
	/**
	 * Creates all bindings. Use for non lazy binding.
	 * 
	 * @param classes
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private void createBindings(Set<Class<?>> classes) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		for(Class<?> clazz:classes){
		
			if(getTopicBinding(clazz) == null)
				if(getAssociationContainerBinding(clazz) == null)
					throw new BadAnnotationException("Class " + clazz.getName() + " must have either an @Topic or an @AssociationContainer annotation.");
		}
	}
	
	/**
	 * Creates a topic binding.
	 * 
	 * @param clazz
	 * @return
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private TopicBinding createTopicBinding(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		logger.info("Create topic binding for " + clazz.getName());
		
		// check if class is correct annotated
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

		// add binding to map
		addBinding(clazz, binding);

		// set parent
		binding.setParent(superClassBinding);
		
		// set topic name
		if (topicAnnotation.name().length() > 0)
			binding.setName(topicAnnotation.name());
		else
			binding.setName(clazz.getSimpleName());
		
		// set subject identifier
		if (topicAnnotation.subject_identifier() != null) {
			
			for (String s : topicAnnotation.subject_identifier())
				binding.addIdentifier(TopicMapsUtils.resolveURI(s, config.getPrefixMap()));
			
		}else{
			
			// create subject identifier
			binding.addIdentifier(TopicMapsUtils.generateSubjectIdentifier(clazz));
		}
		
		// create field bindings
		for (Field field : clazz.getDeclaredFields())
			createFieldBinding(binding, field, clazz);

		// check topic binding
		checkTopicBinding(binding, clazz);
		
		return binding;
	}
	
	/**
	 * Checks if no constraint for an topic binding is violated
	 * 
	 * @param binding
	 * @throws BadAnnotationException
	 */
	private void checkTopicBinding(TopicBinding binding, Class<?> clazz) throws BadAnnotationException{
		
		Map<IDTYPE,Integer> idCounter = new HashMap<IDTYPE, Integer>();
		Map<String,Integer> occCounter = new HashMap<String, Integer>();
		
		// count construct occurrences
		for(AbstractFieldBinding fb:binding.getFieldBindings())
		{
			if(fb instanceof IdBinding){
				
				int count = 0;
				if(idCounter.get(((IdBinding)fb).getIdtype()) != null)
					count = idCounter.get(((IdBinding)fb).getIdtype());
				count++;
				idCounter.put(((IdBinding)fb).getIdtype(), count);
				
			}else if(fb instanceof OccurrenceBinding){
				
				String resolvedType = TopicMapsUtils.resolveURI(((OccurrenceBinding)fb).getOccurrenceType(), config.getPrefixMap());
				
				int count = 0;
				if(occCounter.get(resolvedType) != null)
					count = occCounter.get(resolvedType);
				count++;
				occCounter.put(resolvedType, count);
				
			}
		}
		
		// check of not identifier
		if(idCounter.isEmpty())
			throw new BadAnnotationException("Topic class " + clazz.getName() + " has no identifier at all.");
		
		// check multiple identifier fields of the same type
		if(idCounter.get(IDTYPE.ITEM_IDENTIFIER) != null && idCounter.get(IDTYPE.ITEM_IDENTIFIER) > 1)
			throw new BadAnnotationException("Topic class " + clazz.getName() + " has to many item identifier.");
		if(idCounter.get(IDTYPE.SUBJECT_IDENTIFIER) != null && idCounter.get(IDTYPE.SUBJECT_IDENTIFIER) > 1)
			throw new BadAnnotationException("Topic class " + clazz.getName() + " has to many subject identifier.");
		if(idCounter.get(IDTYPE.SUBJECT_LOCATOR) != null && idCounter.get(IDTYPE.SUBJECT_LOCATOR) > 1)
			throw new BadAnnotationException("Topic class " + clazz.getName() + " has to many subject locator.");
		
		// check multiple occurrence fields of same type
		for(Map.Entry<String, Integer> entry:occCounter.entrySet())
			if(entry.getValue() > 1)
				throw new BadAnnotationException("Multiple fields annotated with occurrence type " + entry.getKey());
	}
	
	/**
	 * Creates a association container binding.
	 * 
	 * @param clazz
	 * @return
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private AssociationContainerBinding createAssociationContainerBinding(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		logger.info("Create association container binding for " + clazz.getName());
		
		// check if class is correct annotated
		if(!isAssociationContainerAnnotated(clazz))
			throw new BadAnnotationException("Class " + clazz.getName() + " must have an @AssociationContainer annotation.");
		
		// create binding for superclass
		Class<?> superclass = clazz.getSuperclass();
		AssociationContainerBinding superClassBinding = null;
		
		if (superclass != Object.class){
			
			if(!this.config.getClasses().contains(superclass)){
				throw new ClassNotSpecifiedException("Superclass of class " + clazz.getName() + " is not configured.");
			}
			
			if(!isTopicAnnotated(superclass))
				throw new BadAnnotationException("Superclass of class " + clazz.getName() + " must have an @AssociationContainer annotation as well.");
			
			superClassBinding = getAssociationContainerBinding(superclass);
		}
		
		// create new binding
		AssociationContainerBinding binding = new AssociationContainerBinding();

		// add binding to map
		addBinding(clazz, binding);
		
		// set parent
		binding.setParent(superClassBinding);
		
		// create field bindings
		for (Field field : clazz.getDeclaredFields())
			createFieldBinding(binding, field, clazz);

		return binding;
	}
	
	/**
	 * Creates a binding for an topic class field.
	 * 
	 * @param binding
	 * @param field
	 * @param clazz
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private void createFieldBinding(TopicBinding binding, Field field, Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		// ignore transient fields
		if(isTransient(field)){
			logger.info("Ignoring transient field: " + field.getName());
			return;
		}

		logger.info("Create binding for field: " + field.getName());
		
		// id field?
		Id id = field.getAnnotation(Id.class);
		
		if(id != null){

			createIdBinding(binding, field, clazz, id);
			
		}else{

			// name field?
			Name name = field.getAnnotation(Name.class);
			
			if(name != null){
				
				createNameBinding(binding, field, clazz, name);
				
			}else{
				
				// occurrence field?
				Occurrence occurrence = field.getAnnotation(Occurrence.class);
				
				if(occurrence != null){
					
					createOccurrenceBinding(binding, field, clazz, occurrence);
					
				}else{
					
					// association field?
					Association association = field.getAnnotation(Association.class);
					
					if(association != null){
						
						createAssociationBinding(binding, field, clazz, association);
						
					}else{
							
						throw new BadAnnotationException("Non transient field " + field.getName() + " has no valid annotaton.");
					}
				}
			}
		}
	}
	
	
	/**
	 * Creates a binding for an association class field.
	 * Actually always a role, but kept separated for future flexibility.
	 * 
	 * @param binding
	 * @param field
	 * @param clazz
	 * @throws BadAnnotationException
	 */
	private void createFieldBinding(AssociationContainerBinding binding, Field field, Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		// ignore transient fields
		if(isTransient(field)){
			logger.info("Ignoring transient field: " + field.getName());
			return;
		}

		logger.info("Create binding for field: " + field.getName());
		
		// role field?
		Role role = field.getAnnotation(Role.class);
		
		if(role != null)
			createRoleBinding(binding, field, clazz, role);
		else
			throw new BadAnnotationException("Non transient field " + field.getName() + " has no valid annotaton.");
	}
	
	
	/**
	 * Checks if a field is marked transient.
	 * 
	 * @param field
	 * @return
	 */
	private boolean isTransient(Field field){
		
		if ((field.getModifiers() & Modifier.TRANSIENT) != 0)
			return true;
		
		return false;
	}
	
	
	/**
	 * Creates an id binding.
	 * 
	 * @param topicBinding
	 * @param field
	 * @param clazz
	 * @param idAnnotation
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private void createIdBinding(TopicBinding topicBinding, Field field, Class<?> clazz, Id idAnnotation) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		IDTYPE type = idAnnotation.type();

		IdBinding ib = new IdBinding(config.getPrefixMap(), topicBinding);
		
		ib.setIdtype(type);
		
		ib.setArray(field.getType().isArray());
		ib.setCollection(ReflectionUtil.isCollection(field));

		/// add id to topic binding
		topicBinding.addFieldBinding(ib);
		
		// create methods
		addMethods(field, clazz, ib);
	}
	
	/**
	 * Creates an name binding.
	 * 
	 * @param topicBinding
	 * @param field
	 * @param clazz
	 * @param nameAnnotation
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private void createNameBinding(TopicBinding topicBinding, Field field, Class<?> clazz, Name nameAnnotation) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		NameBinding nb = new NameBinding(config.getPrefixMap(), topicBinding);
		
		String nameType = null;
		
		if (nameAnnotation.type().length() == 0)
			nameType = TopicMapsUtils.generateSubjectIdentifier(field);
		else
			nameType = nameAnnotation.type();
				
		nb.setNameType(TopicMapsUtils.resolveURI(nameType, config.getPrefixMap()));
		
		// check field type
		if(ReflectionUtil.getGenericType(field) != String.class)
			throw new BadAnnotationException("Type of name " + field.getName() + " is not String.");
		
		// add scope
		addScope(field, nb);
		nb.setArray(field.getType().isArray());
		nb.setCollection(ReflectionUtil.isCollection(field));

		// add name to topic binding
		topicBinding.addFieldBinding(nb);
		
		// create methods
		addMethods(field, clazz, nb);
	}
	
	/**
	 * Creates an occurrence binding.
	 * 
	 * @param topicBinding
	 * @param field
	 * @param clazz
	 * @param occurrenceAnnotation
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private void createOccurrenceBinding(TopicBinding topicBinding, Field field, Class<?> clazz, Occurrence occurrenceAnnotation) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		OccurrenceBinding ob = new OccurrenceBinding(config.getPrefixMap(), topicBinding);
		
		String occurrenceType = null;
		
		if (occurrenceAnnotation.type().length() == 0)
			occurrenceType = TopicMapsUtils.generateSubjectIdentifier(field);
		else
			occurrenceType = occurrenceAnnotation.type();
		
		ob.setOccurrenceType(TopicMapsUtils.resolveURI(occurrenceType, config.getPrefixMap()));
		
		// add scope
		addScope(field, ob);
		ob.setArray(field.getType().isArray());
		ob.setCollection(ReflectionUtil.isCollection(field));

		ob.setDataType(getXSDDatatype(ReflectionUtil.getGenericType(field)));
		
		// add occurrence to topic binding
		topicBinding.addFieldBinding(ob);
		
		// create methods
		addMethods(field, clazz, ob);
	}

	/**
	 * Creates an association binding.
	 * 
	 * @param topicBinding
	 * @param field
	 * @param clazz
	 * @param associationAnnotation
	 */
	private void createAssociationBinding(TopicBinding topicBinding, Field field, Class<?> clazz,  Association associationAnnotation) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		// get type
		String associationType;
		
		if (associationAnnotation.type().length() == 0)
			associationType = TopicMapsUtils.generateSubjectIdentifier(field);
		else
			associationType = associationAnnotation.type();
	
		// association kind
		ASSOCIATIONKIND kind = associationAnnotation.kind();
		
		if(kind == ASSOCIATIONKIND.UNARY)
			createUnaryAssociationBinding(topicBinding, field, clazz, associationAnnotation, associationType);
		
		else if(kind == ASSOCIATIONKIND.BINARY)
			createBinaryAssociationBinding(topicBinding, field, clazz, associationAnnotation, associationType);
		
		else if(kind == ASSOCIATIONKIND.NNARY)
			createNnaryAssociationBinding(topicBinding, field, clazz, associationAnnotation, associationType);
	}
	
	/**
	 * Create binding for an unary association.
	 * 
	 * @param topicBinding
	 * @param field
	 * @param clazz
	 * @param associationAnnotation
	 * @param associationType
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private void createUnaryAssociationBinding(TopicBinding topicBinding, Field field, Class<?> clazz,  Association associationAnnotation, String associationType) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{

		// check if boolean
		
		if(ReflectionUtil.getGenericType(field) != boolean.class)
			throw new BadAnnotationException("Unary association " +  field.getName() + " is not of type boolean.");
		
		// get played role
		String playedRole = "";
		
		if(associationAnnotation.played_role().length() != 0)
			playedRole = associationAnnotation.played_role();
		
		if(playedRole == "")
			throw new BadAnnotationException("Unary association " + field.getName() + " needs an played_role attribute!");
		
		AssociationBinding ab = new AssociationBinding(config.getPrefixMap(), topicBinding);
		
		ab.setKind(ASSOCIATIONKIND.UNARY);
		ab.setAssociationType(TopicMapsUtils.resolveURI(associationType, config.getPrefixMap()));
		ab.setPlayedRole(TopicMapsUtils.resolveURI(playedRole, config.getPrefixMap()));
		
		// add scope
		addScope(field, ab);
		ab.setArray(field.getType().isArray());
		ab.setCollection(ReflectionUtil.isCollection(field));

		// add association to topic binding
		topicBinding.addFieldBinding(ab);
		
		// create methods
		addMethods(field, clazz, ab);

	}
	
	/**
	 * Creates binding for an binary association.
	 * 
	 * @param topicBinding
	 * @param field
	 * @param clazz
	 * @param associationAnnotation
	 * @param associationType
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private void createBinaryAssociationBinding(TopicBinding topicBinding, Field field, Class<?> clazz,  Association associationAnnotation, String associationType) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		// get played role
		
		String playedRole = "";
		
		if(associationAnnotation.played_role().length() != 0)
			playedRole = associationAnnotation.played_role();
		
		if(playedRole == "")
			throw new BadAnnotationException("Binary association " + field.getName() + " needs an played_role attribute!");
		
		// get other role
		
		String otherRole = "";
		
		if(associationAnnotation.other_role().length() != 0)
			otherRole = associationAnnotation.played_role();
		
		if(otherRole == "")
			throw new BadAnnotationException("Binary association " + field.getName() + " needs an other_role attribute!");
		
		// get other player
		Class<?> otherType = ReflectionUtil.getGenericType(field);
		
		if(!isTopicAnnotated(otherType))
			throw new BadAnnotationException("Counter player of binary association " + field.getName() + " needs to be an annotated topic class.");
			
		
		AssociationBinding ab = new AssociationBinding(config.getPrefixMap(), topicBinding);
		
		ab.setKind(ASSOCIATIONKIND.BINARY);
		ab.setAssociationType(TopicMapsUtils.resolveURI(associationType, config.getPrefixMap()));
		ab.setPlayedRole(TopicMapsUtils.resolveURI(playedRole, config.getPrefixMap()));
		ab.setOtherRole(otherRole);
		ab.setOtherPlayer(getTopicBinding(otherType));
		
		// add scope
		addScope(field, ab);
		
		ab.setArray(field.getType().isArray());
		ab.setCollection(ReflectionUtil.isCollection(field));

		// add association to topic binding
		topicBinding.addFieldBinding(ab);
		
		// create methods
		addMethods(field, clazz, ab);
		
	}

	/**
	 * Creates binding for an n-nary association.
	 * 
	 * @param topicBinding
	 * @param field
	 * @param clazz
	 * @param associationAnnotation
	 * @param associationType
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private void createNnaryAssociationBinding(TopicBinding topicBinding, Field field, Class<?> clazz,  Association associationAnnotation, String associationType) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		// get container
		
		Class<?> container = ReflectionUtil.getGenericType(field);
		AssociationContainerBinding containerBinding = getAssociationContainerBinding(container);
		
		AssociationBinding ab = new AssociationBinding(config.getPrefixMap(), topicBinding);
		
		ab.setKind(ASSOCIATIONKIND.NNARY);
		ab.setAssociationType(TopicMapsUtils.resolveURI(associationType, config.getPrefixMap()));
		ab.setAssociationContainer(containerBinding);
		
		// add scope
		addScope(field, ab);
		
		ab.setArray(field.getType().isArray());
		ab.setCollection(ReflectionUtil.isCollection(field));

		// add association to topic binding
		topicBinding.addFieldBinding(ab);
		
		// create methods
		addMethods(field, clazz, ab);
		
	}
	
	/**
	 * Creates an role binding.
	 * 
	 * @param associationContainerBinding
	 * @param field
	 * @param clazz
	 * @param roleAnnotation
	 */
	private void createRoleBinding(AssociationContainerBinding associationContainerBinding, Field field, Class<?> clazz,  Role roleAnnotation) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		RoleBinding rb = new RoleBinding(config.getPrefixMap(), associationContainerBinding);
		
		String roleType = null;
		
		if (roleAnnotation.type().length() == 0)
			roleType = "base_locator:" + field.getName();
		else
			roleType = roleAnnotation.type();
		
		rb.setRoleType(TopicMapsUtils.resolveURI(roleType, config.getPrefixMap()));
		
		// add role to association container binding
		associationContainerBinding.addRoleBinding(rb);
		
		// create methods
		addMethods(field, clazz, rb);
	}
	
	
	/**
	 * Adds getter and setter methods to the field binding.
	 * 
	 * @param field
	 * @param clazz
	 * @param fb
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private void addMethods(Field field, Class<?> clazz, AbstractFieldBinding fb) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		// create binding for generic type if necessary
		Class<?> type = ReflectionUtil.getGenericType(field);
		
		if (needMapping(type)){
			
			// creates binding if not found
			if(isTopicAnnotated(type))
				getTopicBinding(type);
			
			else if(isAssociationContainerAnnotated(type))
				getAssociationContainerBinding(type);
		}
			
		
		String fieldName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

		// generate expected method names
		Method setter = clazz.getMethod("set" + fieldName, field.getType());
		Method getter = null;
		
		try{
			
			getter = clazz.getMethod("get" + fieldName);
			
		}catch(NoSuchMethodException e){} // catch exception since for boolean values getter starting with "is" are as well possible
		
		if(getter == null && ReflectionUtil.getGenericType(field) == boolean.class)
			getter = clazz.getMethod("is" + fieldName);

		if(setter == null)
			throw new NoSuchMethodException("Set method for field " + field.getName() + " don't exist or not java.bean compatible.");

		if(getter == null)
			throw new NoSuchMethodException("Get method for field " + field.getName() + " don't exist or not java.bean compatible.");
		
		fb.setArray(field.getType().isArray());
		fb.setCollection(ReflectionUtil.isCollection(field));
		
		fb.setSetter(setter);
		fb.setGetter(getter);

	}
	
	
	/**
	 * Returns an topic binding for an specific class. If the binding not exists, it will be created (lazy binding).
	 *  
	 * @param clazz
	 * @return
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private TopicBinding getTopicBinding(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {

		if(!isTopicAnnotated(clazz))
			return null;
		
		if(bindingMap.get(clazz) != null){
				return (TopicBinding)bindingMap.get(clazz);
		}

		TopicBinding binding = createTopicBinding(clazz);
		
		return binding;
	}
	
	
	/**
	 * Returns a association container binding for an specific class. If the binding not exists, it will be created (lazy binding).
	 * @param clazz
	 * @return
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 */
	private AssociationContainerBinding getAssociationContainerBinding(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		if(!isAssociationContainerAnnotated(clazz))
			return null;
		
		if(bindingMap.get(clazz) != null)
			return (AssociationContainerBinding)bindingMap.get(clazz);
		
		AssociationContainerBinding binding = createAssociationContainerBinding(clazz);
		
		return binding;
	}
	
	
	/**
	 * Checks if a class is annotated with @Topic.
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean isTopicAnnotated(Class<?> clazz){
		
		Topic topic = clazz.getAnnotation(Topic.class);
		if (topic != null)
			return true;
		
		return false;
	}
	
	
	/**
	 * Checks if a class is annotated with @AssociationContainer
	 * @param clazz
	 * @return
	 */
	private boolean isAssociationContainerAnnotated(Class<?> clazz){
		
		AssociationContainer associationContainer = clazz.getAnnotation(AssociationContainer.class);
		if(associationContainer != null)
			return true;
		
		return false;
	}
	

	/**
	 * Adds a binding to the binding map.
	 * 
	 * @param clazz
	 * @param binding
	 */
	private void addBinding(Class<?> clazz, AbstractBinding binding){
		
		if (bindingMap == null)
			bindingMap = new HashMap<Class<?>, AbstractBinding>();
		
		bindingMap.put(clazz, binding);
		
	}
	

	
	/**
	 * Checks if it is necessary to map a class.
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean needMapping(Class<?> clazz) {
		
		Class<?> checkClazz = clazz;
		
		if(clazz.isArray())
			checkClazz = clazz.getComponentType();

		if (checkClazz.isPrimitive())
			return false;

		if (checkClazz.getName().startsWith("java.lang"))
			return false;

		if (checkClazz.getName().startsWith("java.util"))
			return false;

		return true;
	}
	

	private void addScope(Field field, AbstractFieldBinding fb){
		
		Scope scope = field.getAnnotation(Scope.class);
		
		if(scope == null)
			return;
		
		List<String> themes = Arrays.asList(scope.themes());
		
		List<String> resolvedThemes = new ArrayList<String>();
		
		for(String t:themes)
			resolvedThemes.add(TopicMapsUtils.resolveURI(t, config.getPrefixMap()));
		
		fb.setThemes(resolvedThemes);
	}
	

	public void printBindings(){

		for(Map.Entry<Class<?>, AbstractBinding> entry:bindingMap.entrySet())
			System.out.println(entry.getValue().toString());
	}
	

	/**
	 * Returns the topic map object. Creates a new one if not exist.
	 * 
	 * @return
	 * @throws IOException
	 */
	private TopicMap getTopicMap() throws IOException
	{
		try
		{
			if(topicMap == null)
				topicMap = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap(config.getBaseLocator());
			
			return topicMap;
		}
		catch (Exception e) {
			throw new IOException("Could not create topic map (" + e.getMessage() + ")");
		}
	}
	
	
	public void persist(Object topicObject) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, IOException {

		// get the binding
		TopicBinding topicBinding = getTopicBinding(topicObject.getClass());

		/// TODO maybe catch other exception an re-throw

		if(topicBinding == null)
			throw new BadAnnotationException("Object cannot be persisted.");
		
		topicBinding.persist(getTopicMap(), topicObject);
		
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

			writer.write(topicMap);
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	

	public String getXSDDatatype(Type type) {
		
		if (type.equals(Boolean.class))
			return IXsdDatatypes.XSD_BOOLEAN;
		
		if (type.equals(Integer.class))
			return IXsdDatatypes.XSD_INTEGER;
		
		if (type.equals(Date.class))
			return IXsdDatatypes.XSD_DATE;
				
		return IXsdDatatypes.XSD_STRING;
	}
	
}
