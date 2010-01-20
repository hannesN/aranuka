package de.topicmapslab.aranuka.persist;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Role;
import de.topicmapslab.aranuka.annotations.Scope;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.binding.AbstractClassBinding;
import de.topicmapslab.aranuka.binding.AbstractFieldBinding;
import de.topicmapslab.aranuka.binding.AssociationBinding;
import de.topicmapslab.aranuka.binding.AssociationContainerBinding;
import de.topicmapslab.aranuka.binding.IdBinding;
import de.topicmapslab.aranuka.binding.NameBinding;
import de.topicmapslab.aranuka.binding.OccurrenceBinding;
import de.topicmapslab.aranuka.binding.RoleBinding;
import de.topicmapslab.aranuka.binding.TopicBinding;
import de.topicmapslab.aranuka.constants.IXsdDatatypes;
import de.topicmapslab.aranuka.enummerations.AssociationKind;
import de.topicmapslab.aranuka.enummerations.IdType;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.utils.ReflectionUtil;
import de.topicmapslab.aranuka.utils.TopicMapsUtils;

// creates and stores bindings, returns bindings on demand
public class BindingHandler {

	private static Logger logger = LoggerFactory.getLogger(BindingHandler.class);
	
	private Map<Class<?>, AbstractClassBinding> bindingMap;
	private Configuration config;
	

	// --[ public methods ]------------------------------------------------------------------------------
	
	public BindingHandler(Configuration config) {
		
		this.config = config;
	}
	
	// pre-creates bindings for a set of classes
	public void createBindingsForAllClasses() throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		for(Class<?> clazz:config.getClasses()){
		
			if(getBindingFromCache(clazz) == null){
				
				if(isTopicAnnotated(clazz)){
					
					TopicBinding binding = createTopicBinding(clazz);
					addBindingToCache(clazz, binding);
										
				}else if(isAssociationContainerAnnotated(clazz)){
					
//					AssociationContainerBinding binding = createAssociationContainerBinding(clazz);
//					addBindingToCache(clazz, binding);
					
				}else{
					/// TODO throw exception
				}
			}
		}
	}
	
	// gets the bindings for an specific class TODO: maybe create two different methods
	public AbstractClassBinding getBinding(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		if(getBindingFromCache(clazz) != null)
			return getBindingFromCache(clazz);
		
		AbstractClassBinding binding = null;
		
		// create the binding
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(clazz);
		
		if(isTopicAnnotated(clazz)){
			
			binding = createTopicBinding(clazz);
			addBindingToCache(clazz, binding);
								
		}else if(isAssociationContainerAnnotated(clazz)){
			
			binding = createAssociationContainerBinding(clazz);
			addBindingToCache(clazz, binding);
			
		}else{
			
			throw new BadAnnotationException("Class is not @Topic or @AssociationContainer annotated.");
		}
		
		return binding;
		
	}
	
	// for debug
	
	public void printBindings(){

		if(bindingMap != null)
			for(Map.Entry<Class<?>, AbstractClassBinding> entry:bindingMap.entrySet()){
				
				System.out.println("");
				System.out.println(entry.getValue().toString());
				
			}
				
	}
	
	// --[ private methods ]-------------------------------------------------------------------------------
	
	// topic bindings
	
	private TopicBinding getTopicBinding(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {

		if(!isTopicAnnotated(clazz))
			return null;
		
		if(getBindingFromCache(clazz) != null){
				return (TopicBinding)getBindingFromCache(clazz);
		}

		TopicBinding binding = createTopicBinding(clazz);
		
		return binding;
	}
	
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

		// add binding to cache
		addBindingToCache(clazz, binding);

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
	
	private void checkTopicBinding(TopicBinding binding, Class<?> clazz) throws BadAnnotationException{
		
		Map<IdType,Integer> idCounter = new HashMap<IdType, Integer>();
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
		if(idCounter.get(IdType.ITEM_IDENTIFIER) != null && idCounter.get(IdType.ITEM_IDENTIFIER) > 1)
			throw new BadAnnotationException("Topic class " + clazz.getName() + " has to many item identifier.");
		if(idCounter.get(IdType.SUBJECT_IDENTIFIER) != null && idCounter.get(IdType.SUBJECT_IDENTIFIER) > 1)
			throw new BadAnnotationException("Topic class " + clazz.getName() + " has to many subject identifier.");
		if(idCounter.get(IdType.SUBJECT_LOCATOR) != null && idCounter.get(IdType.SUBJECT_LOCATOR) > 1)
			throw new BadAnnotationException("Topic class " + clazz.getName() + " has to many subject locator.");
		
		// check multiple occurrence fields of same type
		for(Map.Entry<String, Integer> entry:occCounter.entrySet())
			if(entry.getValue() > 1)
				throw new BadAnnotationException("Multiple fields annotated with occurrence type " + entry.getKey());
	}
		
	// association  container bindings
	
	private AssociationContainerBinding getAssociationContainerBinding(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		if(!isAssociationContainerAnnotated(clazz))
			return null;
		
		if(getBindingFromCache(clazz) != null){
				return (AssociationContainerBinding)getBindingFromCache(clazz);
		}

		AssociationContainerBinding binding = createAssociationContainerBinding(clazz);
		
		return binding;
		
	}
		
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
	addBindingToCache(clazz, binding);
	
	// set parent
	binding.setParent(superClassBinding);
	
	// create field bindings
	for (Field field : clazz.getDeclaredFields())
		createFieldBinding(binding, field, clazz);

	// TODO check association container binding?
	
	return binding;
}
		
	// --[ create binding of topic fields ]-------------------------------------------------
	
	private void createFieldBinding(TopicBinding binding, Field field, Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		// ignore transient fields
		if(isTransient(field)){
			logger.info("Ignoring transient field: " + field.getName());
			return;
		}

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
	
	private void createIdBinding(TopicBinding topicBinding, Field field, Class<?> clazz, Id idAnnotation) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		logger.info("Create id-binding for field: " + field.getName());
		
		IdType type = idAnnotation.type();

		IdBinding ib = new IdBinding(config.getPrefixMap(), topicBinding);
		
		ib.setIdtype(type);
		
		ib.setArray(field.getType().isArray());
		ib.setCollection(ReflectionUtil.isCollection(field));

		/// add id to topic binding
		topicBinding.addFieldBinding(ib);
		
		// create methods
		addMethods(field, clazz, ib);
	}
	
	private void createNameBinding(TopicBinding topicBinding, Field field, Class<?> clazz, Name nameAnnotation) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		logger.info("Create name-binding for field: " + field.getName());
		
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
	
	private void createOccurrenceBinding(TopicBinding topicBinding, Field field, Class<?> clazz, Occurrence occurrenceAnnotation) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		logger.info("Create occurrence-binding for field: " + field.getName());
		
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

	private void createAssociationBinding(TopicBinding topicBinding, Field field, Class<?> clazz,  Association associationAnnotation) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		logger.info("Create association-binding for field: " + field.getName());
		
		if (associationAnnotation.type().length() == 0)
			throw new BadAnnotationException("Association type must not be empty.");
		
		if (associationAnnotation.played_role().length() == 0)
			throw new BadAnnotationException("Played role must not be empty.");
		
		if (associationAnnotation.other_role().length() == 0)
			throw new BadAnnotationException("Other role must not be empty.");
		
		AssociationBinding ab = new AssociationBinding(config.getPrefixMap(), topicBinding);
		
		ab.setAssociationType(associationAnnotation.type());
		ab.setPlayedRole(associationAnnotation.played_role());
		ab.setOtherRole(associationAnnotation.other_role());
		
		addScope(field, ab);
		ab.setArray(field.getType().isArray());
		ab.setCollection(ReflectionUtil.isCollection(field));
		ab.setPersistOnCascade(associationAnnotation.persistOnCascade());

		if(isTopicAnnotated(ReflectionUtil.getGenericType(field))){
			
			// is binary association
			ab.setOtherPlayer(getTopicBinding((ReflectionUtil.getGenericType(field).getClass())));
			ab.setKind(AssociationKind.BINARY);
			
		}else if(isAssociationContainerAnnotated(ReflectionUtil.getGenericType(field))){
			
			// is nnary association
			// set association container binding
			ab.setAssociationContainerBinding(getAssociationContainerBinding(ReflectionUtil.getGenericType(field).getClass()));
			ab.setKind(AssociationKind.NNARY);
			
		}else if(ReflectionUtil.getGenericType(field) == boolean.class) {
			
			// is unary
			ab.setKind(AssociationKind.UNARY);
			
		}else{
			
			throw new BadAnnotationException("Unallowed association field type: " + field.getGenericType());
			
		}
		
		// add occurrence to topic binding
		topicBinding.addFieldBinding(ab);
		
		// create methods
		addMethods(field, clazz, ab);
		
	}
		
	// create fields
	
	// --[ create binding for association container fields ]---------------------------------
	
	private void createFieldBinding(AssociationContainerBinding binding, Field field, Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		// ignore transient fields
		if(isTransient(field)){
			logger.info("Ignoring transient field: " + field.getName());
			return;
		}

		// role field?
		Role role = field.getAnnotation(Role.class);
		
		if(role != null){
			
			createRoleBinding(binding, field, clazz, role);
			
		}else{
			
			throw new BadAnnotationException("Non transient field " + field.getName() + " has no valid annotaton.");
		}
	}
	
	
	private void createRoleBinding(AssociationContainerBinding associationContainerBinding, Field field, Class<?> clazz,  Role roleAnnotation) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		logger.info("Create role-binding for field: " + field.getName());
		
		RoleBinding rb = new RoleBinding(config.getPrefixMap(), associationContainerBinding);
		
		String roleType = null;
		
		if (roleAnnotation.type().length() == 0)
			roleType = "base_locator:" + field.getName();
		else
			roleType = roleAnnotation.type();
		
		rb.setRoleType(TopicMapsUtils.resolveURI(roleType, config.getPrefixMap()));
		
		Class<?> playerType = ReflectionUtil.getGenericType(field);
		
		if(!isTopicAnnotated(playerType))
			throw new BadAnnotationException("@Role annotated field in association container have to be an @Topic annotated class.");
		
		rb.setPlayerBinding(getTopicBinding(playerType)); 
		
		// add role to association container binding
		associationContainerBinding.addRoleBinding(rb);
		
		// create methods
		addMethods(field, clazz, rb);
	}
	
	
	// helper methods
	
	// helper
	
	private void addMethods(Field field, Class<?> clazz, AbstractFieldBinding fb) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		// create binding for generic type if necessary
		Class<?> type = ReflectionUtil.getGenericType(field);
		
		if (needsMapping(type)){
			
			// creates binding if not found
			if(isTopicAnnotated(type))
				getTopicBinding(type);
			
//			else if(isAssociationContainerAnnotated(type))
//				getAssociationContainerBinding(type);
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
	
	
	private boolean needsMapping(Class<?> clazz) {
		
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
	
	
	private boolean isTransient(Field field){
		
		if ((field.getModifiers() & Modifier.TRANSIENT) != 0)
			return true;
		
		return false;
	}
	
	
	private boolean isTopicAnnotated(Class<?> clazz){
		
		Topic topic = clazz.getAnnotation(Topic.class);
		if (topic != null)
			return true;
		
		return false;
	}
	

	private boolean isAssociationContainerAnnotated(Class<?> clazz){
		
		AssociationContainer associationContainer = clazz.getAnnotation(AssociationContainer.class);
		if(associationContainer != null)
			return true;
		
		return false;
	}
	
	private void addScope(Field field, AbstractFieldBinding fb){
		
		Scope scope = field.getAnnotation(Scope.class);
		
		if(scope == null)
			return;
		
		List<String> themes = Arrays.asList(scope.themes());
		
		Set<String> resolvedThemes = new HashSet<String>();
		
		for(String t:themes)
			resolvedThemes.add(TopicMapsUtils.resolveURI(t, config.getPrefixMap()));
		
		fb.setThemes(resolvedThemes);
	}
	
	
	private String getXSDDatatype(Type type) {
		
		if (type.equals(Boolean.class))
			return IXsdDatatypes.XSD_BOOLEAN;
		
		if (type.equals(Integer.class))
			return IXsdDatatypes.XSD_INTEGER;
		
		if (type.equals(Date.class))
			return IXsdDatatypes.XSD_DATE;
				
		return IXsdDatatypes.XSD_STRING;
	}
	
	
	// private getter and setter
	
	private void addBindingToCache(Class<?> clazz, AbstractClassBinding binding){
		
		if (bindingMap == null)
			bindingMap = new HashMap<Class<?>, AbstractClassBinding>();
		
		bindingMap.put(clazz, binding);
		
	}
	
	
	private AbstractClassBinding getBindingFromCache(Class<?> clazz){
		
		if(bindingMap == null)
			return null;
		
		return bindingMap.get(clazz);
		
	}
	

}
