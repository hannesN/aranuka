package de.topicmapslab.aranuka;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.utils.ReflectionUtil;

public class Session {

	private static Logger logger = LoggerFactory.getLogger(Session.class);
	
	private Map<Class<?>, AbstractBinding> bindingMap;
	private Configuration config;
	
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
		
			if(isTopicAnnotated(clazz))
				getTopicBinding(clazz);

			else if(isAssociationContainerAnnotated(clazz))
				getAssociationContainerBinding(clazz);
			
			else throw new BadAnnotationException("Class " + clazz.getName() + " must have either an @Topic or an @AssociationContainer annotation.");
			
		}
		
		// test
		
		System.out.println(bindingMap);
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
				binding.addIdentifier(resolveURI(s));
			
		}else{
			
			// create subject identifier
			binding.addIdentifier(generateSubjectIdentifier(clazz));
		}
		
		// create field bindings
		for (Field field : clazz.getDeclaredFields())
			createFieldBinding(binding, field, clazz);

		// check topic binding
		
		if(binding.getIdBindings().isEmpty())
			throw new BadAnnotationException("Topic class " + clazz.getName() + " has no identifier at all.");

		return binding;
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
		
		// check if topic already has an id of this type
		List<IdBinding> idBindings = topicBinding.getIdBindings();
		
		for(IdBinding ib:idBindings)
			if(ib.getIdtype() == type)
				throw new BadAnnotationException("Only one id per type allowed for each class.");
				
		IdBinding ib = new IdBinding(topicBinding);
		
		ib.setIdtype(type);
		
		// add id to topic binding
		topicBinding.addIdBinding(ib);
		
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
		
		NameBinding nb = new NameBinding(topicBinding);
		
		String nameType = null;
		
		if (nameAnnotation.type().length() == 0)
			nameType = generateSubjectIdentifier(field);
		else
			nameType = nameAnnotation.type();
				
		nb.setNameType(resolveURI(nameType));
		
		// check field type
		if(ReflectionUtil.getGenericType(field) != String.class)
			throw new BadAnnotationException("Type of name " + field.getName() + " is not String.");
		
		// add scope
		addScope(field, nb);
		
		// add name to topic binding
		topicBinding.addNameBinding(nb);
		
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
		
		OccurrenceBinding ob = new OccurrenceBinding(topicBinding);
		
		String occurrenceType = null;
		
		if (occurrenceAnnotation.type().length() == 0)
			occurrenceType = generateSubjectIdentifier(field);
		else
			occurrenceType = occurrenceAnnotation.type();
		
		ob.setOccurrenceType(resolveURI(occurrenceType));
		
		// add scope
		addScope(field, ob);
		
		// add occurrence to topic binding
		topicBinding.addOccurrenceBinding(ob);
		
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
			associationType = generateSubjectIdentifier(field);
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
		
		AssociationBinding ab = new AssociationBinding(topicBinding);
		
		ab.setAssociationType(resolveURI(associationType));
		ab.setPlayedRole(resolveURI(playedRole));
		
		// add scope
		addScope(field, ab);
		
		// add association to topic binding
		topicBinding.addAssociationBinding(ab);
		
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
			
		
		AssociationBinding ab = new AssociationBinding(topicBinding);
		
		ab.setAssociationType(resolveURI(associationType));
		ab.setPlayedRole(resolveURI(playedRole));
		ab.setOtherRole(otherRole);
		ab.setOtherPlayer(getTopicBinding(otherType));
		
		// add scope
		addScope(field, ab);
		
		// add association to topic binding
		topicBinding.addAssociationBinding(ab);
		
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
		
		AssociationBinding ab = new AssociationBinding(topicBinding);
		
		ab.setAssociationType(resolveURI(associationType));
		ab.setAssociationContainer(containerBinding);
		
		// add scope
		addScope(field, ab);
		
		// add association to topic binding
		topicBinding.addAssociationBinding(ab);
		
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
		
		RoleBinding rb = new RoleBinding(associationContainerBinding);
		
		String roleType = null;
		
		if (roleAnnotation.type().length() == 0)
			roleType = "base_locator:" + field.getName();
		else
			roleType = roleAnnotation.type();
		
		rb.setRoleType(resolveURI(roleType));
		
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
		
		if(bindingMap.get(clazz) != null)
			return (TopicBinding)bindingMap.get(clazz);
		
		TopicBinding binding = createTopicBinding(clazz);
		//addBinding(clazz, binding);
		
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
		
		if(bindingMap.get(clazz) != null)
			return (AssociationContainerBinding)bindingMap.get(clazz);
		
		AssociationContainerBinding binding = createAssociationContainerBinding(clazz);
		//addBinding(clazz, binding);
		
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
	 * Resolves an uri.
	 * 
	 * @param uri
	 * @return
	 */
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
	
	private String generateSubjectIdentifier(Class<?> clazz){

		StringBuilder builder = new StringBuilder();
		String nameSuffix = clazz.getName().replaceAll("\\.", "/");

		builder.append("base_locator:");
		
		builder.append(nameSuffix);
		return builder.toString();
	}
	
	private String generateSubjectIdentifier(Field field){
		
		String identifier = generateSubjectIdentifier(field.getDeclaringClass());
		
		identifier+= "/" + field.getName();
		return identifier;
	}
	
	private void addScope(Field field, AbstractFieldBinding fb){
		
		Scope scope = field.getAnnotation(Scope.class);
		
		if(scope == null)
			return;
		
		List<String> themes = Arrays.asList(scope.themes());
		
		List<String> resolvedThemes = new ArrayList<String>();
		
		for(String t:themes)
			resolvedThemes.add(resolveURI(t));
		
		fb.setThemes(resolvedThemes);
	}
	
}
