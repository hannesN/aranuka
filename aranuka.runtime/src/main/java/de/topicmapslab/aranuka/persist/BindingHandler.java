/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.persist;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
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

/**
 * Class which handles the annotation bindings.
 */
public class BindingHandler {

	private static Logger logger = LoggerFactory
			.getLogger(BindingHandler.class);

	/**
	 * Map to cache already create bindings.
	 */
	private Map<Class<?>, AbstractClassBinding> bindingMap;
	/**
	 * The configuration.
	 */
	private Configuration config;

	/**
	 * Constructor.
	 * 
	 * @param config
	 *            - Configuration class.
	 */
	public BindingHandler(Configuration config) {

		this.config = config;
	}

	/**
	 * Pre-creates bindings for all configured classes. Used for non lazy
	 * binding behavior.
	 * 
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	public void createBindingsForAllClasses() throws BadAnnotationException,
			NoSuchMethodException, ClassNotSpecifiedException {

		for (Class<?> clazz : config.getClasses()) {

			if (getBindingFromCache(clazz) == null) {

				if (isTopicAnnotated(clazz)) {

					TopicBinding binding = createTopicBinding(clazz);
					addBindingToCache(clazz, binding);

				} else if (isAssociationContainerAnnotated(clazz)) {

					AssociationContainerBinding binding = createAssociationContainerBinding(clazz);
					addBindingToCache(clazz, binding);

				} else {
					throw new BadAnnotationException(
							"Class "
									+ clazz
									+ " is neither @Topic or @AssociationContainer annotated.");
				}
			}
		}
	}

	/**
	 * Returns the binding for an specific class. If the binding not exist yet,
	 * a new binding will be created.
	 * 
	 * @param clazz
	 *            - The class.
	 * @return The topic or association container binding, depending on the
	 *         class.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	public AbstractClassBinding getBinding(Class<?> clazz)
			throws BadAnnotationException, NoSuchMethodException,
			ClassNotSpecifiedException {

		if (getBindingFromCache(clazz) != null)
			return getBindingFromCache(clazz);

		AbstractClassBinding binding = null;

		// create the binding
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(clazz);

		if (isTopicAnnotated(clazz)) {

			binding = createTopicBinding(clazz);
			addBindingToCache(clazz, binding);

		} else if (isAssociationContainerAnnotated(clazz)) {

			binding = createAssociationContainerBinding(clazz);
			addBindingToCache(clazz, binding);

		} else {

			throw new BadAnnotationException(
					"Class is not @Topic or @AssociationContainer annotated.");
		}

		return binding;

	}

	/**
	 * Returns a set with all available topic bindings.
	 * 
	 * @return The set.
	 */
	public Set<TopicBinding> getAllTopicBindings() {

		if (this.bindingMap == null)
			return Collections.emptySet();

		Set<TopicBinding> result = new HashSet<TopicBinding>();

		for (Map.Entry<Class<?>, AbstractClassBinding> entry : bindingMap
				.entrySet()) {

			if (entry.getValue() instanceof TopicBinding)
				result.add((TopicBinding) entry.getValue());
		}

		return result;
	}

	/**
	 * Returns the corresponding class to a specific binding.
	 * 
	 * @param binding
	 *            - The binding.
	 * @return The class.
	 */
	public Class<?> getClassForBinding(AbstractClassBinding binding) {

		if (bindingMap == null)
			return null;

		for (Map.Entry<Class<?>, AbstractClassBinding> entry : bindingMap
				.entrySet()) {

			if (entry.getValue().equals(binding))
				return entry.getKey();
		}

		return null;
	}

	/**
	 * Returns the topic binding of an specific class. Returns null if the class
	 * is not @topic annotated.
	 * 
	 * @param clazz
	 *            - The class.
	 * @return The binding or null.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private TopicBinding getTopicBinding(Class<?> clazz)
			throws BadAnnotationException, NoSuchMethodException,
			ClassNotSpecifiedException {

		if (!isTopicAnnotated(clazz))
			return null;

		if (getBindingFromCache(clazz) != null) {
			return (TopicBinding) getBindingFromCache(clazz);
		}

		TopicBinding binding = createTopicBinding(clazz);

		return binding;
	}

	/**
	 * Creates a topic binding for a specific class. Throws
	 * BadAnnotationException if class is not @topic annotated.
	 * 
	 * @param clazz
	 *            - The class
	 * @return - The binding.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private TopicBinding createTopicBinding(Class<?> clazz)
			throws BadAnnotationException, NoSuchMethodException,
			ClassNotSpecifiedException {

		logger.info("Create topic binding for " + clazz.getName());

		if (!isTopicAnnotated(clazz))
			throw new BadAnnotationException("Class " + clazz.getName()
					+ " must have an @Topic annotation.");

		// create binding for superclass
		Class<?> superclass = clazz.getSuperclass();
		TopicBinding superClassBinding = null;

		if (superclass != Object.class) {

			if (!this.config.getClasses().contains(superclass)) {
				throw new ClassNotSpecifiedException("Superclass of class "
						+ clazz.getName() + " is not configured.");
			}

			if (!isTopicAnnotated(superclass))
				throw new BadAnnotationException("Superclass of class "
						+ clazz.getName()
						+ " must have an @Topic annotation as well.");

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
		if (topicAnnotation.subject_identifier() != null
				&& topicAnnotation.subject_identifier().length > 0) {

			for (String s : topicAnnotation.subject_identifier())
				binding.addIdentifier(TopicMapsUtils.resolveURI(s,
						config.getPrefixMap()));

		} else {

			// create subject identifier
			binding.addIdentifier(TopicMapsUtils
					.generateSubjectIdentifier(clazz));
		}

		// create field bindings
		for (Field field : clazz.getDeclaredFields())
			createFieldBinding(binding, field, clazz);

		// check topic binding
		checkTopicBinding(binding, clazz);

		return binding;
	}

	/**
	 * Check if a topic binding is valid.
	 * 
	 * @param binding
	 *            - The binding.
	 * @param clazz
	 *            - The corresponding class.
	 * @throws BadAnnotationException
	 */
	private void checkTopicBinding(TopicBinding binding, Class<?> clazz)
			throws BadAnnotationException {

		Map<IdType, Integer> idCounter = new HashMap<IdType, Integer>();
		Map<String, Integer> occCounter = new HashMap<String, Integer>();
		Map<String, Integer> nameCounter = new HashMap<String, Integer>();

		TopicBinding currBinding = binding;
		
		// count construct occurrences

		while (currBinding!=null) {
			for (AbstractFieldBinding fb : currBinding.getFieldBindings()) {
				if (fb instanceof IdBinding) {
	
					int count = 0;
					if (idCounter.get(((IdBinding) fb).getIdtype()) != null)
						count = idCounter.get(((IdBinding) fb).getIdtype());
					count++;
					idCounter.put(((IdBinding) fb).getIdtype(), count);
	
				} else if (fb instanceof OccurrenceBinding) {
	
					String resolvedType = TopicMapsUtils.resolveURI(
							((OccurrenceBinding) fb).getOccurrenceType(),
							config.getPrefixMap());
	
					int count = 0;
					if (occCounter.get(resolvedType) != null)
						count = occCounter.get(resolvedType);
					count++;
					occCounter.put(resolvedType, count);
	
				} else if (fb instanceof NameBinding) {
	
					String resolvedType = TopicMapsUtils
							.resolveURI(((NameBinding) fb).getNameType(),
									config.getPrefixMap());
	
					int count = 0;
					if (nameCounter.get(resolvedType) != null)
						count = nameCounter.get(resolvedType);
					count++;
					nameCounter.put(resolvedType, count);
				}
			}
			currBinding = currBinding.getParent();
		}
		
		// check of not identifier
		if (idCounter.isEmpty()&&((clazz.getModifiers()&Modifier.ABSTRACT)==0))
			throw new BadAnnotationException("Topic class " + clazz.getName()
					+ " has no identifier at all.");

		// check multiple identifier fields of the same type
		if (idCounter.get(IdType.ITEM_IDENTIFIER) != null
				&& idCounter.get(IdType.ITEM_IDENTIFIER) > 1)
			throw new BadAnnotationException("Topic class " + clazz.getName()
					+ " has to many item identifier.");
		if (idCounter.get(IdType.SUBJECT_IDENTIFIER) != null
				&& idCounter.get(IdType.SUBJECT_IDENTIFIER) > 1)
			throw new BadAnnotationException("Topic class " + clazz.getName()
					+ " has to many subject identifier.");
		if (idCounter.get(IdType.SUBJECT_LOCATOR) != null
				&& idCounter.get(IdType.SUBJECT_LOCATOR) > 1)
			throw new BadAnnotationException("Topic class " + clazz.getName()
					+ " has to many subject locator.");

		// check multiple occurrence fields of same type
		for (Map.Entry<String, Integer> entry : occCounter.entrySet())
			if (entry.getValue() > 1)
				throw new BadAnnotationException(
						"Multiple fields annotated with occurrence type "
								+ entry.getKey());

		// check multiple name fields of the same type
		for (Map.Entry<String, Integer> entry : nameCounter.entrySet())
			if (entry.getValue() > 1)
				throw new BadAnnotationException(
						"Multiple fields annotated with name type "
								+ entry.getKey());

	}

	/**
	 * Returns the association container binding for a specific class. Creates a
	 * new binding if not existing.
	 * 
	 * @param clazz
	 *            - The class.
	 * @return The binding or null if the class is not @AssociationContainer
	 *         annotated
	 * @throws ClassNotSpecifiedException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 */
	private AssociationContainerBinding getAssociationContainerBinding(
			Class<?> clazz) throws ClassNotSpecifiedException,
			BadAnnotationException, NoSuchMethodException {

		if (!isAssociationContainerAnnotated(clazz))
			return null;

		if (getBindingFromCache(clazz) != null) {
			return (AssociationContainerBinding) getBindingFromCache(clazz);
		}

		AssociationContainerBinding binding = createAssociationContainerBinding(clazz);

		return binding;

	}

	/**
	 * Creates a new association container binding for an specific class. Throws
	 * a BadAnnotationException if the class is not @AssociationContainer
	 * annotated.
	 * 
	 * @param clazz
	 *            - The class.
	 * @return The new binding.
	 * @throws ClassNotSpecifiedException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 */
	private AssociationContainerBinding createAssociationContainerBinding(
			Class<?> clazz) throws ClassNotSpecifiedException,
			BadAnnotationException, NoSuchMethodException {

		logger.info("Create association container binding for "
				+ clazz.getName());

		// check if class is correct annotated
		if (!isAssociationContainerAnnotated(clazz))
			throw new BadAnnotationException("Class " + clazz.getName()
					+ " must have an @AssociationContainer annotation.");

		// create binding for superclass
		Class<?> superclass = clazz.getSuperclass();
		AssociationContainerBinding superClassBinding = null;

		if (superclass != Object.class) {

			if (!this.config.getClasses().contains(superclass)) {
				throw new ClassNotSpecifiedException("Superclass of class "
						+ clazz.getName() + " is not configured.");
			}

			if (!isTopicAnnotated(superclass))
				throw new BadAnnotationException(
						"Superclass of class "
								+ clazz.getName()
								+ " must have an @AssociationContainer annotation as well.");

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

		// check fields
		if (binding.getRoleBindings().isEmpty())
			throw new BadAnnotationException("The association container class "
					+ clazz + " has no fields.");

		return binding;
	}

	/**
	 * Creates a binding for an topic field.
	 * 
	 * @param binding
	 *            - The corresponding topic binding.
	 * @param field
	 *            - The field.
	 * @param clazz
	 *            - The class to which the field belongs.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private void createFieldBinding(TopicBinding binding, Field field,
			Class<?> clazz) throws BadAnnotationException,
			NoSuchMethodException, ClassNotSpecifiedException {

		// ignore transient fields
		if (isTransient(field)) {
			logger.info("Ignoring transient field: " + field.getName());
			return;
		}

		// id field?
		Id id = field.getAnnotation(Id.class);

		if (id != null) {

			createIdBinding(binding, field, clazz, id);

		} else {

			// name field?
			Name name = field.getAnnotation(Name.class);

			if (name != null) {

				createNameBinding(binding, field, clazz, name);

			} else {

				// occurrence field?
				Occurrence occurrence = field.getAnnotation(Occurrence.class);

				if (occurrence != null) {

					createOccurrenceBinding(binding, field, clazz, occurrence);

				} else {

					// association field?
					Association association = field
							.getAnnotation(Association.class);

					if (association != null) {

						createAssociationBinding(binding, field, clazz,
								association);

					} else {

						createOccurrenceBinding(binding, field, clazz, null);
					}
				}
			}
		}
	}

	/**
	 * Creates the binding for an @Id annotated field.
	 * 
	 * @param topicBinding
	 *            - The corresponding topic binding.
	 * @param field
	 *            - The field.
	 * @param clazz
	 *            - The class to which the field belongs.
	 * @param idAnnotation
	 *            - The annotation object.
	 * @throws NoSuchMethodException
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 */
	private void createIdBinding(TopicBinding topicBinding, Field field,
			Class<?> clazz, Id idAnnotation) throws NoSuchMethodException,
			BadAnnotationException, ClassNotSpecifiedException {

		logger.info("Create id-binding for field: " + field.getName());

		IdType type = idAnnotation.type();

		IdBinding ib = new IdBinding(config.getPrefixMap(), topicBinding);

		ib.setIdtype(type);

		ib.setArray(field.getType().isArray());
		ib.setCollection(ReflectionUtil.isCollection(field));
		ib.setParameterisedType(ReflectionUtil.getGenericType(field));

		// check if we a String
		if ((field.getType() != String.class) && idAnnotation.autogenerate()) {
			throw new BadAnnotationException(
					"Only String attributes may be generated.");
		}
		ib.setAutogenerate(idAnnotation.autogenerate());

		// / add id to topic binding
		topicBinding.addFieldBinding(ib);

		// create methods
		addMethods(field, clazz, ib);
	}

	/**
	 * Creates the binding for an @Name annotated field.
	 * 
	 * @param topicBinding
	 *            - The corresponding topic binding.
	 * @param field
	 *            - The field.
	 * @param clazz
	 *            - The class to which the field belongs.
	 * @param nameAnnotation
	 *            - The annotation object.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private void createNameBinding(TopicBinding topicBinding, Field field,
			Class<?> clazz, Name nameAnnotation) throws BadAnnotationException,
			NoSuchMethodException, ClassNotSpecifiedException {

		logger.info("Create name-binding for field: " + field.getName());

		NameBinding nb = new NameBinding(config.getPrefixMap(), topicBinding);

		String nameType = null;

		if (nameAnnotation.type().length() == 0)
			nameType = TopicMapsUtils.generateSubjectIdentifier(field);
		else
			nameType = nameAnnotation.type();

		nb.setNameType(TopicMapsUtils.resolveURI(nameType,
				config.getPrefixMap()));

		// check field type
		if (ReflectionUtil.getGenericType(field) != String.class)
			throw new BadAnnotationException("@name annotated field "
					+ field.getName() + " has to be of type 'String'.");

		// add scope
		addScope(field, nb);
		nb.setArray(field.getType().isArray());
		nb.setCollection(ReflectionUtil.isCollection(field));
		nb.setParameterisedType(ReflectionUtil.getGenericType(field));

		// add name to topic binding
		topicBinding.addFieldBinding(nb);

		// create methods
		addMethods(field, clazz, nb);
	}

	/**
	 * Creates the binding for an @Occurrence annotated field.
	 * 
	 * @param topicBinding
	 *            - The corresponding topic binding.
	 * @param field
	 *            - The field.
	 * @param clazz
	 *            - The class to which the field belongs.
	 * @param occurrenceAnnotation
	 *            - The annotation object.
	 * @throws NoSuchMethodException
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 */
	private void createOccurrenceBinding(TopicBinding topicBinding,
			Field field, Class<?> clazz, Occurrence occurrenceAnnotation)
			throws NoSuchMethodException, BadAnnotationException,
			ClassNotSpecifiedException {

		logger.info("Create occurrence-binding for field: " + field.getName());

		OccurrenceBinding ob = new OccurrenceBinding(config.getPrefixMap(),
				topicBinding);

		String occurrenceType = null;

		if (occurrenceAnnotation == null
				|| occurrenceAnnotation.type().length() == 0)
			occurrenceType = TopicMapsUtils.generateSubjectIdentifier(field);
		else
			occurrenceType = occurrenceAnnotation.type();

		ob.setOccurrenceType(TopicMapsUtils.resolveURI(occurrenceType,
				config.getPrefixMap()));

		// add scope
		addScope(field, ob);
		ob.setArray(field.getType().isArray());
		ob.setCollection(ReflectionUtil.isCollection(field));
		ob.setParameterisedType(ReflectionUtil.getGenericType(field));

		String dt = occurrenceAnnotation.datatype();
		if (dt.length() == 0)
			dt = getXSDDatatype(ReflectionUtil.getGenericType(field));
		else {
			if (dt.startsWith("xsd:"))
				dt = "http://www.w3.org/2001/XMLSchema#" + dt.substring(4);
		}
		ob.setDataType(dt);
		
		// add occurrence to topic binding
		topicBinding.addFieldBinding(ob);

		// create methods
		addMethods(field, clazz, ob);
	}

	/**
	 * Creates the binding for an @Association annotated field.
	 * 
	 * @param topicBinding
	 *            - The corresponding topic binding.
	 * @param field
	 *            - The field.
	 * @param clazz
	 *            - The class to which the field belongs.
	 * @param associationAnnotation
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private void createAssociationBinding(TopicBinding topicBinding,
			Field field, Class<?> clazz, Association associationAnnotation)
			throws BadAnnotationException, NoSuchMethodException,
			ClassNotSpecifiedException {

		logger.info("Create association-binding for field: " + field.getName());

		if (associationAnnotation.type().length() == 0)
			throw new BadAnnotationException(
					"Association type must not be empty.");

		if (associationAnnotation.played_role().length() == 0)
			throw new BadAnnotationException("Played role must not be empty.");

		if (associationAnnotation.other_role().length() == 0)
			throw new BadAnnotationException("Other role must not be empty.");

		AssociationBinding ab = new AssociationBinding(config.getPrefixMap(),
				topicBinding);

		ab.setAssociationType(associationAnnotation.type());
		ab.setPlayedRole(associationAnnotation.played_role());
		ab.setOtherRole(associationAnnotation.other_role());

		addScope(field, ab);
		ab.setArray(field.getType().isArray());
		ab.setCollection(ReflectionUtil.isCollection(field));
		ab.setPersistOnCascade(associationAnnotation.persistOnCascade());

		Class<?> genericType = ReflectionUtil.getGenericType(field);

		if (isTopicAnnotated(genericType)) {

			// is binary association
			ab.setOtherPlayerBinding(getTopicBinding(genericType));
			ab.setKind(AssociationKind.BINARY);

		} else if (isAssociationContainerAnnotated(genericType)) {

			// is nnary association
			// set association container binding
			AssociationContainerBinding binding = getAssociationContainerBinding(genericType);

			ab.setAssociationContainerBinding(binding);
			ab.setKind(AssociationKind.NNARY);

		} else if (genericType == boolean.class) {

			// is unary
			ab.setKind(AssociationKind.UNARY);

		} else {

			throw new BadAnnotationException(
					"Unallowed association field type: " + genericType);

		}

		ab.setParameterisedType(genericType);

		// add occurrence to topic binding
		topicBinding.addFieldBinding(ab);

		// create methods
		addMethods(field, clazz, ab);

	}

	/**
	 * Creates an field binding for an association container field.
	 * 
	 * @param binding
	 *            - The corresponding association container binding.
	 * @param field
	 *            - The field.
	 * @param clazz
	 *            - The class to which the field belongs.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private void createFieldBinding(AssociationContainerBinding binding,
			Field field, Class<?> clazz) throws BadAnnotationException,
			NoSuchMethodException, ClassNotSpecifiedException {

		// ignore transient fields
		if (isTransient(field)) {
			logger.info("Ignoring transient field: " + field.getName());
			return;
		}

		// role field?
		Role role = field.getAnnotation(Role.class);

		if (role != null) {

			createRoleBinding(binding, field, clazz, role);

		} else if (!field.isSynthetic()) { // catch synthetic fields, i.e. the
											// funny this$0 which exist when
											// using nested classes
			throw new BadAnnotationException(
					"Non transient field "
							+ field.getName()
							+ " of an association container class has to be @Role annotated.");
		}
	}

	/**
	 * Creates the binding for an @Role annotated field.
	 * 
	 * @param associationContainerBinding
	 *            - The corresponding association container binding.
	 * @param field
	 *            - The field.
	 * @param clazz
	 *            - The class to which the field belongs.
	 * @param roleAnnotation
	 *            - The annotation object.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private void createRoleBinding(
			AssociationContainerBinding associationContainerBinding,
			Field field, Class<?> clazz, Role roleAnnotation)
			throws BadAnnotationException, NoSuchMethodException,
			ClassNotSpecifiedException {

		logger.info("Create role-binding for field: " + field.getName());

		RoleBinding rb = new RoleBinding(config.getPrefixMap(),
				associationContainerBinding);

		String roleType = null;

		if (roleAnnotation.type().length() == 0)
			roleType = TopicMapsUtils.generateSubjectIdentifier(field);
		else
			roleType = roleAnnotation.type();

		rb.setRoleType(TopicMapsUtils.resolveURI(roleType,
				config.getPrefixMap()));

		Class<?> playerType = ReflectionUtil.getGenericType(field);

		if (!isTopicAnnotated(playerType))
			throw new BadAnnotationException(
					"@Role annotated field in association container have to be an @Topic annotated class.");

		rb.setPlayerBinding(getTopicBinding(playerType));

		// add role to association container binding
		associationContainerBinding.addRoleBinding(rb);

		// create methods
		addMethods(field, clazz, rb);
	}

	/**
	 * Adds getter and setter methods to an field binding.
	 * 
	 * @param field
	 *            - The field.
	 * @param clazz
	 *            - The class to which the field belongs.
	 * @param fb
	 *            - The field binding.
	 * @throws NoSuchMethodException
	 * @throws BadAnnotationException
	 * @throws ClassNotSpecifiedException
	 */
	private void addMethods(Field field, Class<?> clazz, AbstractFieldBinding fb)
			throws NoSuchMethodException, BadAnnotationException,
			ClassNotSpecifiedException {

		// create binding for generic type if necessary
		Class<?> type = ReflectionUtil.getGenericType(field);

		if (needsMapping(type)) {

			// creates binding if not found
			if (isTopicAnnotated(type))
				getTopicBinding(type);

			else if (isAssociationContainerAnnotated(type))
				getAssociationContainerBinding(type);
		}

		String fieldName = field.getName().substring(0, 1).toUpperCase()
				+ field.getName().substring(1);

		// generate expected method names
		Method setter = clazz.getMethod("set" + fieldName, field.getType());
		Method getter = null;

		try {

			getter = clazz.getMethod("get" + fieldName);

		} catch (NoSuchMethodException e) {
		} // catch exception since for boolean values getter starting with "is"
			// are as well possible

		if (getter == null
				&& ReflectionUtil.getGenericType(field) == boolean.class)
			getter = clazz.getMethod("is" + fieldName);

		if (setter == null)
			throw new NoSuchMethodException("Set method for field "
					+ field.getName()
					+ " don't exist or not java.bean compatible.");

		if (getter == null)
			throw new NoSuchMethodException("Get method for field "
					+ field.getName()
					+ " don't exist or not java.bean compatible.");

		fb.setArray(field.getType().isArray());
		fb.setCollection(ReflectionUtil.isCollection(field));

		fb.setSetter(setter);
		fb.setGetter(getter);

	}

	/**
	 * Checks if a class needs a binding mapping.
	 * 
	 * @param clazz
	 *            - The class.
	 * @return True or false.
	 */
	private boolean needsMapping(Class<?> clazz) {

		Class<?> checkClazz = clazz;

		if (clazz.isArray())
			checkClazz = clazz.getComponentType();

		if (checkClazz.isPrimitive())
			return false;

		if (checkClazz.getName().startsWith("java.lang"))
			return false;

		if (checkClazz.getName().startsWith("java.util"))
			return false;

		return true;
	}

	/**
	 * Checks if a field is transient.
	 * 
	 * @param field
	 *            - The field.
	 * @return True or false.
	 */
	private boolean isTransient(Field field) {

		if ((field.getModifiers() & Modifier.TRANSIENT) != 0)
			return true;

		return false;
	}

	/**
	 * Checks if a class is @Topic annotated.
	 * 
	 * @param clazz
	 *            - The class.
	 * @return True or false.
	 */
	private boolean isTopicAnnotated(Class<?> clazz) {

		Topic topic = clazz.getAnnotation(Topic.class);
		if (topic != null)
			return true;

		return false;
	}

	/**
	 * Checks if a class is @AssociationContainer annotated.
	 * 
	 * @param clazz
	 *            - The class.
	 * @return True or false.
	 */
	private boolean isAssociationContainerAnnotated(Class<?> clazz) {

		AssociationContainer associationContainer = clazz
				.getAnnotation(AssociationContainer.class);
		if (associationContainer != null)
			return true;

		return false;
	}

	/**
	 * Adds a scope to an field binding.
	 * 
	 * @param field
	 *            - The field.
	 * @param fb
	 *            - The field binding.
	 */
	private void addScope(Field field, AbstractFieldBinding fb) {

		Scope scope = field.getAnnotation(Scope.class);

		if (scope == null)
			return;

		List<String> themes = Arrays.asList(scope.themes());

		Set<String> resolvedThemes = new HashSet<String>();

		for (String t : themes)
			resolvedThemes.add(TopicMapsUtils.resolveURI(t,
					config.getPrefixMap()));

		fb.setThemes(resolvedThemes);
	}

	/**
	 * Gets the XSD data type for an specific type.
	 * 
	 * @param type
	 *            - The type.
	 * @return The subject identifier of the XSD data type as string.
	 */
	private String getXSDDatatype(Type type) {

		if ((type.equals(Boolean.class)) || (type.equals(boolean.class)))
			return IXsdDatatypes.XSD_BOOLEAN;

		if ((type.equals(Integer.class)) || (type.equals(int.class)))
			return IXsdDatatypes.XSD_INTEGER;

		if (type.equals(Date.class))
			return IXsdDatatypes.XSD_DATE;

		if ((type.equals(Float.class)) || (type.equals(float.class)))
			return IXsdDatatypes.XSD_FLOAT;

		if ((type.equals(Double.class)) || (type.equals(double.class)))
			return IXsdDatatypes.XSD_DOUBLE;

		if ((type.equals(Long.class)) || (type.equals(long.class)))
			return IXsdDatatypes.XSD_LONG;

		return IXsdDatatypes.XSD_STRING;
	}

	/**
	 * Adds a binding to the cache.
	 * 
	 * @param clazz
	 *            - The class to which the binding belongs.
	 * @param binding
	 *            - The binding.
	 */
	private void addBindingToCache(Class<?> clazz, AbstractClassBinding binding) {

		if (bindingMap == null)
			bindingMap = new HashMap<Class<?>, AbstractClassBinding>();

		bindingMap.put(clazz, binding);

	}

	/**
	 * Retrieves a binding from the cache.
	 * 
	 * @param clazz
	 *            - The class to which the binding belongs.
	 * @return The binding or null if not existing.
	 */
	private AbstractClassBinding getBindingFromCache(Class<?> clazz) {

		if (bindingMap == null)
			return null;

		return bindingMap.get(clazz);

	}
}
