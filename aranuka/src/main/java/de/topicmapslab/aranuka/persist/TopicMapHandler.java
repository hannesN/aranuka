
package de.topicmapslab.aranuka.persist;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.binding.AbstractFieldBinding;
import de.topicmapslab.aranuka.binding.AssociationBinding;
import de.topicmapslab.aranuka.binding.AssociationContainerBinding;
import de.topicmapslab.aranuka.binding.IdBinding;
import de.topicmapslab.aranuka.binding.NameBinding;
import de.topicmapslab.aranuka.binding.OccurrenceBinding;
import de.topicmapslab.aranuka.binding.RoleBinding;
import de.topicmapslab.aranuka.binding.TopicBinding;
import de.topicmapslab.aranuka.enummerations.AssociationKind;
import de.topicmapslab.aranuka.enummerations.IdType;
import de.topicmapslab.aranuka.enummerations.Match;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapIOException;
import de.topicmapslab.aranuka.exception.TopicMapInconsistentException;
import de.topicmapslab.aranuka.utils.ReflectionUtil;
import de.topicmapslab.aranuka.utils.TopicMapsUtils;


/**
 * Class which encapsulates interactions with the topic map.
 * @author Christian Haß
 *
 */
public class TopicMapHandler {

	private static Logger logger = LoggerFactory.getLogger(TopicMapHandler.class);
	
	/**
	 * The configuration.
	 */
	private Configuration config;
	/**
	 * Instance of the binding handler.
	 */
	private BindingHandler bindingHandler;
	/**
	 * The topic map.
	 */
	private TopicMap topicMap;
	
	/**
	 * Cache for already created topic objects.
	 */
	private Map<Object, Topic> topicCache;
	/**
	 * Temporary cache for objects created for existing topics.
	 */
	private Map<Topic, Object> objectCache;
	
	private Map<Association,AssociationBinding> associationCache;
	
	
	
	/**
	 * Constructor.
	 * @param config - The configuration.
	 * @param topicMap - The topic map. 
	 */
	public TopicMapHandler(Configuration config, TopicMap topicMap){

		if(config == null)
			throw new RuntimeException("Configuration is null.");
		
		if(topicMap == null)
			throw new RuntimeException("Topic map is null.");
		
		this.config = config;
		this.topicMap = topicMap;
	}
	
	/**
	 * Invokes binding creation for all configured classes (non lazy binding).
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	public void invokeBinding() throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		getBindingHandler().createBindingsForAllClasses();

	}

	/**
	 * Persists an specific object in the topic map. Creates all identifier, names, occurrences and associations. 
	 * Associated topics will be created as simple topics with only one identifier, except the corresponding association 
	 * annotation has the persistOnCascade flag set.
	 * @param topicObject - The object representing the topic.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 */
	public void persist(Object topicObject) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException, TopicMapInconsistentException{
		
		List<Object> topicToPersist = new ArrayList<Object>();
				
		topicToPersist.add(topicObject);
		persistTopics(topicToPersist);
	}
	
	/**
	 * Returns the topic map.
	 * @return The topic map.
	 */
	public TopicMap getTopicMap()
	{
		if(this.topicMap == null)
			throw new RuntimeException("Topic Map is null.");
			
		return this.topicMap;
	}
	
	/**
	 * Returns objects for topics of a specific type.
	 * @param clazz - The class representing the topic type.
	 * @return Set of objects.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	public Set<Object> getTopicsByType(Class<?> clazz) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		TopicBinding binding = null;
		
		try{
			// return empty set if anything goes wrong, i.e. clazz is bad annotated or cast goes wrong... etc....
			binding = (TopicBinding)getBindingHandler().getBinding(clazz);
		}
		catch (Exception e) {
			return Collections.emptySet();
		}

		Topic type = null;
		
		for(String id:binding.getIdentifier()){
			
			type = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(id));
			if(type != null)
				break;
		}
		
		if(type == null)
			return Collections.emptySet();
		
		// type exist, get instances
		TypeInstanceIndex index = getTopicMap().getIndex(TypeInstanceIndex.class);
		
		Set<Topic> topicInstances = new HashSet<Topic>(index.getTopics(type));
		
		Set<Object> instances = getInstancesFromTopics(topicInstances, binding, clazz);
		
		return instances;
	}
	
	/**
	 * Returns an object for an specific topic identified by an subject identifier.
	 * @param si - The subject identifier as string.
	 * @return The object or null if not found.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	public Object getObjectBySubjectIdentifier(String si) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		Topic topic = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(si));
		
		if(topic == null)
			return null;
		
		return getObject(topic);
	}
	
	/**
	 * Returns an object for an specific topic identified by an subject locator.
	 * @param sl - The subject locator as string.
	 * @return The object or null if not found.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	public Object getObjectBySubjectLocator(String sl) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
	
		Topic topic = getTopicMap().getTopicBySubjectLocator(getTopicMap().createLocator(sl));
		
		if(topic == null)
			return null;
		
		return getObject(topic);
	}
	
	/**
	 * Returns an object for an specific topic identified by an item identifier.
	 * @param ii - The item identifier as string.
	 * @return The object or null if not found or the identifier identifies an non topic construct.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	public Object getObjectByItemIdentifier(String ii) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		Construct construct = getTopicMap().getConstructByItemIdentifier(getTopicMap().createLocator(ii));
		
		if(construct == null)
			return null;
		
		Topic topic = null;
		
		try{
			topic = (Topic)construct;
		}
		catch (Exception e) {
			return null;
		}
		
		return getObject(topic);
		
	}
	
	/**
	 * Removes an topic specified by an object from the topic map.
	 * @param object - The object.
	 * @return True in case an topic was removed, otherwise false.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * TODO Check behavior of the topic map engine wrt. cascading delete.
	 */
	public boolean removeTopic(Object object) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		
		Topic topic = getTopic(object);
		
		if(topic != null){
			
			topic.remove();
			return true;
		}
				
		return false;
	}
	
	/**
	 * Returns the topic specified by an specific object.
	 * @param object - The object.
	 * @return The topic or null if not found.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private Topic getTopic(Object object) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		Topic topic = getTopicFromCache(object);
		
		if(topic != null)
			return topic;
		
		TopicBinding binding = (TopicBinding)getBindingHandler().getBinding(object.getClass());

		return getTopicByIdentifier(object, binding);
	}
	

	/**
	 * Returns an object for an specific topic.
	 * @param topic - The topic.
	 * @return The object or null if not found.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private Object getObject(Topic topic) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		TopicBinding binding = getTopicBinding(topic);
		
		if(binding == null)
			return null;
		
		Class<?> clazz = getBindingHandler().getClassForBinding(binding);
		
		if(clazz == null)
			return null;
		
		Object obj =  getInstanceFromTopic(topic, binding, clazz);
		
		return obj;
	}
	
	/**
	 * Returns the topic binding for an specific topic.
	 * @param topic - The topic.
	 * @return The binding or null if not found.
	 * TODO maybe invoke binding creation for all registered classes
	 */
	private TopicBinding getTopicBinding(Topic topic){
		
		if(topic == null)
			return null;
		
		if(topic.getTypes().isEmpty()) 
			return null; // cannot instantiate types
		
		Set<TopicBinding> bindings = getBindingHandler().getAllTopicBindings();
		
		for(Topic type:topic.getTypes()){
			
			Set<Locator> identifiers = type.getSubjectIdentifiers();
			
			for(Locator identifier:identifiers){
				
				for(TopicBinding binding:bindings){

					if(binding.getIdentifier().contains(identifier.getReference()))
						return binding;
				}
			}
		}

		return null;
	}
	
	/**
	 * Persists a set of objects in the topic map.
	 * @param topicObjects - The objects.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 */
	private void persistTopics(List<Object> topicObjects) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException, TopicMapInconsistentException{

		List<Object> toInstanciateTopicObjects = topicObjects;
		List<Object> cascadingTopicObjects = new ArrayList<Object>();
		
		do{
		
			Iterator<Object> itr = toInstanciateTopicObjects.iterator();
			
		    while(itr.hasNext()){
		    	
		    	Object topicObject = itr.next();
		    	
		    	// get binding
		    	TopicBinding binding = (TopicBinding)getBindingHandler().getBinding(topicObject.getClass());
		    	
		    	if(getTopicFromCache(topicObject) == null)
		    	{
			    	// check
			    	if(!this.config.getClasses().contains(topicObject.getClass()))
			    		throw new ClassNotSpecifiedException("The class " + topicObject.getClass().getName() + " is not registered.");
			    	
			    	// create topic
			    	persistTopic(topicObject, cascadingTopicObjects, binding);
		    	
		    	}else{
		    		
		    		updateTopic(getTopicFromCache(topicObject), topicObject, binding, cascadingTopicObjects);
		    	}
		    }
		    
		    toInstanciateTopicObjects = new ArrayList<Object>(cascadingTopicObjects);
		    cascadingTopicObjects.clear();
	    
		}while(!toInstanciateTopicObjects.isEmpty());
 
	}
	
	/**
	 * Persists a specific object in the topic map.
	 * @param topicObject - The object.
	 * @param topicObjects - Set where objects which needs as well to be persisted, i.e. used the persistOnCascade flag, are added.
	 * @param binding - The topic binding.
	 * @return The topic.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 */
	private Topic persistTopic(Object topicObject, List<Object> topicObjects, TopicBinding binding) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException, TopicMapInconsistentException{
		
		logger.info("Persist topic object " + topicObject);
		
		Topic newTopic = createTopicByIdentifier(topicObject, binding);

		// add topic to cache
		addTopicToCache(newTopic, topicObject);
		
		// set topic type
		newTopic.addType(getTopicType(binding));
		
		// update identifier
		updateSubjectIdentifier(newTopic, topicObject, binding); 
		updateSubjectLocator(newTopic, topicObject, binding);
		updateItemIdentifier(newTopic, topicObject, binding);
		
		// update names
		updateNames(newTopic, topicObject, binding);
		
		// update occurrences
		updateOccurrences(newTopic, topicObject, binding);
		
		// update associations
		updateAssociations(newTopic, topicObject, binding, topicObjects);
		
		return newTopic;
	}
	
	/**
	 * Returns the topic type represented by an specific topic binding. Creates a new topic type if not existing.
	 * @param binding - The topic binding.
	 * @return The topic type.
	 * @throws BadAnnotationException
	 */
	private Topic getTopicType(TopicBinding binding) throws BadAnnotationException{
		
		if(binding.getIdentifier().isEmpty())
			throw new BadAnnotationException("Topic type has no identifier.");
		
		Topic type = null;
		
		// try to find type
		for(String identifier:binding.getIdentifier()){
			
			type = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(identifier,this.config.getPrefixMap())));
			if(type != null)
				return  type;
		}
		
		type = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(binding.getIdentifier().iterator().next(),this.config.getPrefixMap())));
		
		if(binding.getName() != null)
			type.createName(binding.getName());
		
		return type;
	}
	
	/**
	 * Updates an already existing topic corresponding to an specific object.
	 * @param topic - The topic.
	 * @param topicObject - The object.
	 * @param binding - The topic binding.
	 * @param topicObjects - Set where objects which needs as well to be persisted, i.e. used the persistOnCascade flag, are added.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 */
	private void updateTopic(Topic topic, Object topicObject, TopicBinding binding, List<Object> topicObjects) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException, TopicMapInconsistentException{
		
		logger.info("Update existing topic " + topicObject);
		
		// update identifier
		updateSubjectIdentifier(topic, topicObject, binding); 
		updateSubjectLocator(topic, topicObject, binding);
		updateItemIdentifier(topic, topicObject, binding);
		
		// update names
		updateNames(topic, topicObject, binding);
		// update occurrences
		updateOccurrences(topic, topicObject, binding);
		// update associations
		updateAssociations(topic, topicObject, binding, topicObjects);
	}
	
	
	/**
	 * Updates the subject identifier of an specific topic according the an specific object.
	 * @param topic - The topic.
	 * @param topicObject - The object.
	 * @param binding - The topic binding.
	 */
	private void updateSubjectIdentifier(Topic topic, Object topicObject, TopicBinding binding){
		
		Set<String> newSubjectIdentifier = getIdentifier(topicObject, binding, IdType.SUBJECT_IDENTIFIER);
		
		Map<Locator,Match> actualSubjectIdentifier = addFlags(topic.getSubjectIdentifiers());
		
		for(String si:newSubjectIdentifier){
			
			boolean found = false;
			
			for(Map.Entry<Locator, Match> entry:actualSubjectIdentifier.entrySet()){
	
				if(entry.getKey().toExternalForm().equals(si)){
					entry.setValue(Match.INSTANCE); // set to found
					found = true;
					break;
				}
			}
			
			if(!found){
				logger.info("Add new subject identifier " + si);
				// add new identifier
				topic.addSubjectIdentifier(getTopicMap().createLocator(si));
			}
			
		}
		
		// remove obsolete identifier
		for(Map.Entry<Locator, Match> entry:actualSubjectIdentifier.entrySet()){
			
			if(entry.getValue() != Match.INSTANCE){
				logger.info("Remove obsolete subject identifier " + entry.getKey().toExternalForm());
				topic.removeSubjectIdentifier(entry.getKey());
			}
		}
	}
	
	/**
	 * Updates the subject locators of an specific topic according to an object.
	 * @param topic - The topic.
	 * @param topicObject - The object.
	 * @param binding - The topic binding.
	 */
	private void updateSubjectLocator(Topic topic, Object topicObject, TopicBinding binding){
		
		Set<String> newSubjectLocator = getIdentifier(topicObject, binding, IdType.SUBJECT_LOCATOR);
		
		Map<Locator,Match> actualSubjectLocator = addFlags(topic.getSubjectLocators());
		
		for(String sl:newSubjectLocator){
			
			boolean found = false;
			
			for(Map.Entry<Locator, Match> entry:actualSubjectLocator.entrySet()){
	
				if(entry.getKey().toExternalForm().equals(sl)){
					entry.setValue(Match.INSTANCE); // set to found
					found = true;
					break;
				}
			}
			
			if(!found){
				logger.info("Add new subject locator " + sl);
				// add new identifier
				topic.addSubjectLocator((getTopicMap().createLocator(sl)));
			}
			
		}
		
		// remove obsolete identifier
		for(Map.Entry<Locator, Match> entry:actualSubjectLocator.entrySet()){
			
			if(entry.getValue() != Match.INSTANCE){
				logger.info("Remove obsolete subject locator " + entry.getKey().toExternalForm());
				topic.removeSubjectLocator(entry.getKey());
			}
		}
	}
	
	/**
	 * Updates the item identifiers of an specific topic according to an object.
	 * @param topic - The topic.
	 * @param topicObject - The object.
	 * @param binding - The topic binding.
	 */
	private void updateItemIdentifier(Topic topic, Object topicObject, TopicBinding binding){
	
		Set<String> newItemIdentifier = getIdentifier(topicObject, binding, IdType.ITEM_IDENTIFIER);
		
		Map<Locator,Match> actualItemIdentifier = addFlags(topic.getItemIdentifiers());
		
		for(String ii:newItemIdentifier){
			
			boolean found = false;
			
			for(Map.Entry<Locator, Match> entry:actualItemIdentifier.entrySet()){
	
				if(entry.getKey().toExternalForm().equals(ii)){
					
					entry.setValue(Match.INSTANCE); // set to found
					found = true;
					break;
				}
			}
			
			if(!found){
				logger.info("Add new Item identifier " + ii);
				// add new identifier
				topic.addItemIdentifier(getTopicMap().createLocator(ii));
			}
			
		}
		
		// remove obsolete identifier
		for(Map.Entry<Locator, Match> entry:actualItemIdentifier.entrySet()){
			
			if(entry.getValue() != Match.INSTANCE){
				logger.info("Remove obsolete item identifier " + entry.getKey().toExternalForm());
				topic.removeItemIdentifier(entry.getKey());
			}
		}
	}
	
	/**
	 * Updates the names of an specific topic according to an object.
	 * @param topic - The topic.
	 * @param topicObject - The object.
	 * @param binding - The topic binding.
	 */
	private void updateNames(Topic topic, Object topicObject, TopicBinding binding){
		
		// get new names
		Map<NameBinding,Set<String>> newNames = getNames(topicObject, binding);
		
		// get actual names
		Map<Name,Match> actualNames = addFlags(topic.getNames());
		
		// update
		for(Map.Entry<NameBinding, Set<String>> newName:newNames.entrySet()){
			
			// get type and scope for this binding/field
			Topic nameType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(newName.getKey().getNameType(),this.config.getPrefixMap())));
			Set<Topic> scope = newName.getKey().getScope(getTopicMap());
			
			
			for(String name:newName.getValue()){
				
				boolean found = false;
				
				for (Map.Entry<Name, Match> actualName : actualNames.entrySet()) {

					if (actualName.getKey().getValue().equals(name)) { // compare value

						if (actualName.getKey().getType().equals(nameType)) { // compare type

							if (scope.isEmpty() || actualName.getKey().getScope().equals(scope)) { // compare scope
	
								found = true;
								actualName.setValue(Match.INSTANCE);
								break;
							}
						}
					}
				}
				
				if(!found){
					
					logger.info("Add new name " + name);
					topic.createName(nameType, name, scope);
				}
			}
		}
		
		// remove obsolete names
		for(Map.Entry<Name, Match> entry:actualNames.entrySet()){

			if(entry.getValue() != Match.INSTANCE){
				logger.info("Remove obsolete name " + entry.getKey().getValue());
				entry.getKey().remove();
			}
		}
	}
	
	/**
	 * Updates the occurrences of an specific topic according to an object.
	 * @param topic - The topic.
	 * @param topicObject - The object.
	 * @param binding - The topic binding.
	 */
	private void updateOccurrences(Topic topic, Object topicObject, TopicBinding binding){
		
		// get new occurrences
		Map<OccurrenceBinding,Set<String>> newOccurrences = getOccurrences(topicObject, binding);
		
		// get actual occurrences
		Map<Occurrence,Match> actualOccurrences = addFlags(topic.getOccurrences());
		
		// update
		for(Map.Entry<OccurrenceBinding, Set<String>> newOccurrence:newOccurrences.entrySet()){
			
			// get type and scope for this binding/field
			Topic occurrenceType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(newOccurrence.getKey().getOccurrenceType(),this.config.getPrefixMap())));
			Set<Topic> scope = newOccurrence.getKey().getScope(getTopicMap());

			for(String value:newOccurrence.getValue()){
				
				boolean found = false;
				
				for (Map.Entry<Occurrence, Match> actualOccurrence : actualOccurrences.entrySet()) {

					if (actualOccurrence.getKey().getValue().equals(value)) { // compare value

						if (actualOccurrence.getKey().getType().equals(occurrenceType)) { // compare type

							if (scope.isEmpty() || actualOccurrence.getKey().getScope().equals(scope)) { // compare scope
	
								found = true;
								actualOccurrence.setValue(Match.INSTANCE);
								break;
							}
						}
					}
				}
				
				if(!found){
					
					logger.info("Add new occurrence " + value); 
					topic.createOccurrence(occurrenceType, value, scope);
					
				}
			}
		}
		
		// remove obsolete occurrences
		for(Map.Entry<Occurrence, Match> entry:actualOccurrences.entrySet()){

			if(entry.getValue() != Match.INSTANCE){
				logger.info("Remove obsolete occurrence " + entry.getKey().getValue());
				entry.getKey().remove();
			}
		}
		
	}
	
	/**
	 * Updates the associations in which an specific topic playes a role according to an object.
	 * @param topic - The topic.
	 * @param topicObject - The object.
	 * @param binding - The topic binding.
	 * @param topicObjects - Set where objects which needs as well to be persisted, i.e. used the persistOnCascade flag, are added.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 */
	private void updateAssociations(Topic topic, Object topicObject, TopicBinding binding, List<Object> topicObjects) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException, TopicMapInconsistentException{
		
		// get new associations
		Map<AssociationBinding, Set<Object>> newAssociations = getAssociations(topicObject, binding);
		
		if(newAssociations.isEmpty())
			return;
		
		// get existing associartions, i.e. played roles
		Map<Role,Match> playedRoles = addFlags(topic.getRolesPlayed());

		for(Map.Entry<AssociationBinding, Set<Object>> newAssociation:newAssociations.entrySet()){

			if(newAssociation.getKey().getKind() == AssociationKind.UNARY)
			{
				updateUnaryAssociation(topic, newAssociation.getKey(), newAssociation.getValue(), playedRoles);

			}else if(newAssociation.getKey().getKind() == AssociationKind.BINARY){
				
				updateBinaryAssociations(topic, newAssociation.getKey(), newAssociation.getValue(), playedRoles, topicObjects);
				
			}else if(newAssociation.getKey().getKind() == AssociationKind.NNARY){
				
				updateNnaryAssociations(topic, newAssociation.getKey(), newAssociation.getValue(), playedRoles, topicObjects);
			}
		}
		
		// remove obsolete roles
		for(Map.Entry<Role, Match> entry:playedRoles.entrySet()){
		
			Association ass = entry.getKey().getParent();
			if(ass != null){
				
				if(entry.getValue() == Match.BINDING){ // only binding match found but no instance
					
					logger.info("Remove obsolete association " + ass.getType());
					ass.remove();
					
				}else if(entry.getValue() == Match.NO){ 
					
					// find association  in cache and remove if it belongs to this topic
					
					if(this.associationCache.get(ass) != null)
					{
						if(binding.getFieldBindings().contains(this.associationCache.get(ass))){ 
							// if the binding belongs to this topic, the association was created by this topic
							removeAssociationFromCache(ass);
							
							logger.info("Remove obsolete association " + ass.getType());
							ass.remove();
						}
						
					}
				}
			}
		}
	}
	
	/**
	 * Updates a number of specific unary associations.
	 * @param topic - The topic which playes a role in the associations.
	 * @param binding - The association binding.
	 * @param associationObjects - The association objects, i.e. the boolean values.
	 * @param playedRoles - The roles played by the topic.
	 */
	private void updateUnaryAssociation(Topic topic, AssociationBinding binding, Set<Object> associationObjects, Map<Role,Match> playedRoles){
		
		if(associationObjects.size() != 1)
			throw new RuntimeException("Unary association has more the one type."); // TODO use other exception type and better message
		
		Topic associationType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(binding.getAssociationType(), this.config.getPrefixMap())));
		Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(binding.getPlayedRole(),this.config.getPrefixMap())));
		Set<Topic> scope = binding.getScope(getTopicMap());
		
		boolean value = (Boolean)associationObjects.iterator().next();
		
		// try to find the association
		Role role = null;
		
		for(Map.Entry<Role, Match> playedRole:playedRoles.entrySet()){
			
			if(playedRole.getValue() != Match.INSTANCE // ignore roles which are already flagged true
					&& playedRole.getKey().getParent().getRoles().size() == 1 // is unary association
					&& playedRole.getKey().getType().equals(roleType) // check role type
					&& playedRole.getKey().getParent().getType().equals(associationType)){ // check association type
				
				// binding found
				logger.info("Unary association matches binding.");
				
				if(playedRole.getKey().getParent().getScope().equals(scope)){ // check scope
				
					role = playedRole.getKey();
					playedRole.setValue(Match.INSTANCE);
					break;
				
				}
			}
		}
		
		if(role != null){
			
			if(value == false){
				logger.info("Remove unary association " + role.getParent());
				role.getParent().remove();
			}
			
		}else{
			
			if(value == true){
				logger.info("Creare new unary association " + associationType);
				Association ass = getTopicMap().createAssociation(associationType, scope);
				ass.createRole(roleType, topic);
			}
		}
	}

	/**
	 * Updates a number of specific binary associations.
	 * @param topic - The topic which playes a role in the association.
	 * @param binding - The association binding.
	 * @param associationObjects - The association objects, i.e. the objects representing the counter player topics.
	 * @param playedRoles - The roles played by the topic.
	 * @param topicObjects - Set where objects which needs as well to be persisted, i.e. used the persistOnCascade flag, are added.
	 * @throws TopicMapInconsistentException
	 * @throws TopicMapIOException
	 * @throws BadAnnotationException
	 */
	private void updateBinaryAssociations(Topic topic, AssociationBinding binding, Set<Object> associationObjects, Map<Role, Match> playedRoles, List<Object> topicObjects) throws TopicMapInconsistentException, TopicMapIOException, BadAnnotationException{

		Topic associationType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(binding.getAssociationType(), this.config.getPrefixMap())));
		Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(binding.getPlayedRole(), this.config.getPrefixMap())));
		Topic otherRoleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(binding.getOtherRole(),this.config.getPrefixMap())));
		Set<Topic> scope = binding.getScope(getTopicMap());

		/// TODO dirty hack to integrate remove of binary associations, improve
		for (Object associationObject : associationObjects) { // check each binary association
			
			Topic counterPlayer = null;
			
			if(associationObject != null){
				counterPlayer = createTopicByIdentifier(associationObject, binding.getOtherPlayerBinding());
				counterPlayer.addType(getTopicType(binding.getOtherPlayerBinding()));
			}
			
			boolean found = false;

			for (Map.Entry<Role, Match> playedRole : playedRoles.entrySet()) {

				if(playedRole.getValue() != Match.INSTANCE  // ignore roles which are already flagged true
						&& playedRole.getKey().getParent().getRoles().size() == 2  	// check if the association is binary
						&& playedRole.getKey().getType().equals(roleType) 	// check role type
						&& playedRole.getKey().getParent().getType().equals(associationType) // check association type
						&& playedRole.getKey().getParent().getScope().equals(scope) // check scope
						&& TopicMapsUtils.getCounterRole(playedRole.getKey().getParent(), playedRole.getKey()).getType().equals(otherRoleType)){ // check counter player role

					// binding found
					playedRole.setValue(Match.BINDING);
					
					if(counterPlayer != null)
						if(TopicMapsUtils.getCounterRole(playedRole.getKey().getParent(), playedRole.getKey()).getPlayer().equals(counterPlayer)){ // check counter player

							found = true;
							playedRole.setValue(Match.INSTANCE);
							break;
						}
				}
			}
			
			if(!found && counterPlayer != null){
				
				logger.info("Create new binary association " + associationType);
				
				Association ass = getTopicMap().createAssociation(associationType, scope);
				
				ass.createRole(roleType, topic);
				ass.createRole(otherRoleType, counterPlayer);
				
				if(binding.isPersistOnCascade()){
					
					topicObjects.add(associationObject);
					logger.info("Persist/Update " + associationObject + " on cascade.");
				}
			}
		}
	}

	/**
	 * Updates a number of specific nnary associations.
	 * @param topic - The topic which playes a role in the associations.
	 * @param binding - The association binding.
	 * @param associationObjects - The association objects, i.e. the association container objects.
	 * @param playedRoles - The roles played by the topics.
	 * @param topicObjects - Set where objects which needs as well to be persisted, i.e. used the persistOnCascade flag, are added.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapIOException
	 */
	private void updateNnaryAssociations(Topic topic, AssociationBinding binding, Set<Object> associationObjects, Map<Role, Match> playedRoles, List<Object> topicObjects) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException{

		Topic associationType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(binding.getAssociationType(),this.config.getPrefixMap())));
		Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(binding.getPlayedRole(),this.config.getPrefixMap())));
		Set<Topic> scope = binding.getScope(getTopicMap());
		
		for (Object associationObject:associationObjects) { // check each nnary association
			
			if(associationObject == null)
				continue; // skip when the association object is null

			// TODO try to use the cache here as well, but check if there are problems when the same association was already created by another topic 
			
			boolean found = false;
			
			// get role-player for container
			Map<Topic,Set<Topic>> containerRolePlayer = getRolesFromContainer(associationObject);
			
			for (Map.Entry<Role, Match> playedRole : playedRoles.entrySet()) {
				
				if(playedRole.getValue() != Match.INSTANCE  // ignore roles which are already flagged true
						&& playedRole.getKey().getType().equals(roleType) 	// check role type
						&& playedRole.getKey().getParent().getType().equals(associationType)){ // check association type

					// add own role to rolePlayer
					Map<Topic,Set<Topic>> rolePlayer = containerRolePlayer;
					Set<Topic> ownTypePlayer = null;
					
					if(rolePlayer.get(playedRole.getKey().getType()) != null)
						ownTypePlayer = rolePlayer.get(playedRole.getKey().getType());
					else ownTypePlayer = new HashSet<Topic>();
					
					ownTypePlayer.add(playedRole.getKey().getPlayer());
					rolePlayer.put(playedRole.getKey().getType(), ownTypePlayer);
					
					// check counter player roles
					if(matchCounterRoleTypes( playedRole.getKey(), rolePlayer)){
						
						playedRole.setValue(Match.BINDING);
					
						// check counter player
						if(matchCounterPlayer(playedRole.getKey(), rolePlayer)){
							
							found = true;
							playedRole.setValue(Match.INSTANCE);
							break;
						}
					}
				}
			}
			
			if(!found){
				
				// create new association
				logger.info("Create new nnary association " + associationType);
				
				Association ass = getTopicMap().createAssociation(associationType, scope);
				
				// add own player
				ass.createRole(roleType, topic);
				
				// add player from container
				for (Map.Entry<Topic, Set<Topic>> rolePlayer : containerRolePlayer.entrySet()) {
					
					for(Topic player:rolePlayer.getValue()){
						logger.info("Create role " +  player + " with type " + rolePlayer.getKey());
						ass.createRole(rolePlayer.getKey(), player);
					}
				}
				
				// add association to cache
				addAssociationToCache(ass, binding);
		
				if(binding.isPersistOnCascade()){

					addCascadingRole(associationObject, topicObjects);
					
				}
			}
		}
	}
	
	/**
	 * Adds an object to an set of objects which		
		 to be persisted as well.
	 * @param associationContainer - The association container in which the role is specified.
	 * @param topicObjects - The set if objects.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	@SuppressWarnings("unchecked")
	private void addCascadingRole(Object associationContainer, List<Object> topicObjects) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		AssociationContainerBinding binding = (AssociationContainerBinding)getBindingHandler().getBinding(associationContainer.getClass());
		
		for(RoleBinding roleBinding:binding.getRoleBindings()){
			
			// get the objects
			
			if(roleBinding.isArray()){
				
				Object[] objects = (Object[])roleBinding.getValue(associationContainer);
				
				for(Object obj:objects){
					topicObjects.add(obj);
					logger.info("Persist/Update " + obj + " on cascade.");
				}

			}else if(roleBinding.isCollection()){
				
				Collection<Object> objects = (Collection<Object>)roleBinding.getValue(associationContainer);
				
				for(Object obj:objects){
					topicObjects.add(obj);
					logger.info("Persist/Update " + obj + " on cascade.");
				}
				
			}else{
				
				topicObjects.add(roleBinding.getValue(associationContainer));
				logger.info("Persist/Update " + roleBinding.getValue(associationContainer) + " on cascade.");
			}
		}
	}
	
	/**
	 * Checks if a set if role types an player matches a specific association.
	 * @param playedRole - The role specifying the association.
	 * @param rolePlayers - Map if role types and player.
	 * @return True in case the association maches, otherwise false.
	 */
	private boolean matchCounterRoleTypes(Role playedRole, Map<Topic,Set<Topic>> rolePlayers){
		
		Set<Topic> existingRolesTypes = playedRole.getParent().getRoleTypes();
		
		if(existingRolesTypes.size() != rolePlayers.keySet().size())
			return false;
		
		for(Topic newRoleType:rolePlayers.keySet()){
			
			if(!existingRolesTypes.contains(newRoleType))
				return false;
		}

		return true;
	}

	/**
	 * Checks if a number of topics matches the counter player in an specific association.
	 * @param playedRole - The role specifying the association.
	 * @param rolePlayers - Map of role types and player.
	 * @return
	 */
	private boolean matchCounterPlayer(Role playedRole, Map<Topic,Set<Topic>> rolePlayers){
		
		Association association = playedRole.getParent();
		
		for (Map.Entry<Topic, Set<Topic>> rolePlayer : rolePlayers.entrySet()) {
		
			Set<Role> existingRoles = association.getRoles(rolePlayer.getKey()); // get player of type
			
			if(existingRoles.size() != rolePlayer.getValue().size())
				return false;
			
			for(Role existingRole:existingRoles){
				
				if(!rolePlayer.getValue().contains(existingRole.getPlayer()))
					return false;
				
			}
		}
		
		return true;
	}
	
	/**
	 * Gets all role types and the related player from an association container object.
	 * @param associationContainerInstance - The object.
	 * @return - A map containing the role types and corresponding player.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapIOException
	 * TODO add recursive call to get roles from superclasses as well.
	 */
	@SuppressWarnings("unchecked")
	private Map<Topic,Set<Topic>> getRolesFromContainer(Object associationContainerInstance) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException{
		
		Map<Topic,Set<Topic>> result = new HashMap<Topic, Set<Topic>>();
		
		AssociationContainerBinding binding = (AssociationContainerBinding)getBindingHandler().getBinding(associationContainerInstance.getClass());
		
		for(RoleBinding roleBinding:binding.getRoleBindings()){
			
			Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(roleBinding.getRoleType(),this.config.getPrefixMap())));
			Set<Topic> player = null;
			if(result.get(roleType) == null)
				player = new HashSet<Topic>();
			else player = result.get(roleType);
			
			if(roleBinding.getValue(associationContainerInstance) != null){
				
				if (roleBinding.isArray()){
	
					for (Object obj : (Object[]) roleBinding.getValue(associationContainerInstance)){
						
						Topic topic = createTopicByIdentifier(obj, roleBinding.getPlayerBinding());
						topic.addType(getTopicType(roleBinding.getPlayerBinding()));
						player.add(topic);
					}
		
				}else if (roleBinding.isCollection()){
					
					for (Object obj : (Collection<Object>) roleBinding.getValue(associationContainerInstance)){
							
						Topic topic = createTopicByIdentifier(obj, roleBinding.getPlayerBinding());
						topic.addType(getTopicType(roleBinding.getPlayerBinding()));
						player.add(topic);
					}
	
				}else{
					
					Topic topic = createTopicByIdentifier(roleBinding.getValue(associationContainerInstance), roleBinding.getPlayerBinding());
					topic.addType(getTopicType(roleBinding.getPlayerBinding()));
					player.add(topic);
				}
			}
			
			result.put(roleType, player);
		}
		
		return result;
	}
	
	/**
	 * Creates objects for a set of topics of an specific type.
	 * @param topics - Set of topics.
	 * @param binding - The topic binding.
	 * @param clazz - The class of the new objects.
	 * @return A set of objects representing the topics.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private Set<Object> getInstancesFromTopics(Set<Topic> topics, TopicBinding binding, Class<?> clazz) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		if(topics.isEmpty())
			return Collections.emptySet();
				
		Set<Object> objects = new HashSet<Object>();
		
		for(Topic topic:topics){
			
			objects.add(getInstanceFromTopic(topic, binding, clazz));
			
		}
		
		clearObjectCache(); // free object cache at end of session
		
		return objects;
	}
	
	/**
	 * Creates an object representing a specific topic.
	 * @param topic - The topic.
	 * @param binding - The binding.
	 * @param clazz - The class type of the new object.
	 * @return The object.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private Object getInstanceFromTopic(Topic topic, TopicBinding binding, Class<?> clazz) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{

		Object object = getObjectFromCache(topic);

		if(object != null) 
			return object;
		
		if(binding.getParent() != null){
			
			object = getInstanceFromTopic(topic, binding.getParent(), clazz);
		
		}else{
			
			try{
				object = clazz.getConstructor().newInstance();
			}
			catch (Exception e){
				throw new TopicMapIOException("Cannot instanciate new object: " + e.getMessage());
			}
		}
		
		addObjectToCache(object, topic);

		// add identifier
		
		addIdentifier(topic, object, binding);
		
		// add names
		addNames(topic, object, binding);
		
		// add occurrences
		addOccurrences(topic, object, binding);
		
		// add associations
		addAssociations(topic, object, binding);
		
		return object;

	}
	
	
	/**
	 * Adds identifier to an newly created object.
	 * @param topic - The corresponding topic.
	 * @param object - The object.
	 * @param binding - The topic binding.
	 * @throws TopicMapIOException
	 */
	private void addIdentifier(Topic topic, Object object, TopicBinding binding) throws TopicMapIOException{
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof IdBinding){
				
				IdBinding idBinding = (IdBinding)afb;
				
				if(idBinding.getIdtype() == IdType.ITEM_IDENTIFIER){
					
					Set<Locator> identifier = topic.getItemIdentifiers();
					addIdentifier(topic, object, idBinding, identifier);
					
				}else if(idBinding.getIdtype() == IdType.SUBJECT_IDENTIFIER){
					
					Set<Locator> identifier = topic.getSubjectIdentifiers();
					addIdentifier(topic, object, idBinding, identifier);
					
				}else if(idBinding.getIdtype() == IdType.SUBJECT_LOCATOR){
					
					Set<Locator> identifier = topic.getSubjectLocators();
					addIdentifier(topic, object, idBinding, identifier);
					
				}else{
					
					/// TODO handle uhnknown idtype
				}
					
			}
		}
	}
	
	/**
	 * Adds identifier to an object.
	 * @param topic - The corresponding topic.
	 * @param object - The object.
	 * @param idBinding - The id binding.
	 * @param identifiers - Set of identifier.
	 * @throws TopicMapIOException
	 */
	private void addIdentifier(Topic topic, Object object, IdBinding idBinding, Set<Locator> identifiers) throws TopicMapIOException{
		
		if(identifiers.isEmpty())
			return;
		
		Set<String> typeIdentifier = ((TopicBinding)idBinding.getParent()).getIdentifier();
			
		// create set
		Set<String> existingIds = new HashSet<String>();
		
		for(Locator identifier:identifiers){
			
			String iri = identifier.getReference();
			
			if(!idBinding.getParameterisedType().equals(String.class)){
				
				// check and remove type identifier to be able to cast the id to an non string datatype
				for(String typeId:typeIdentifier){
					
					if(iri.startsWith(typeId + "/")){
						
						logger.info("Reduce identifier from " + iri);
						iri = iri.replace(typeId + "/", ""); // remove the start
						logger.info("to " + iri);
						break;
					}
				}
			}
			
			existingIds.add(iri);
		}
		
		// add ids to field

		if(!idBinding.isArray() && !idBinding.isCollection()){
			
			if(existingIds.size() > 1)
				throw new TopicMapIOException("Cannot add multiple identifier to an non container field.");
			
			if(idBinding.getFieldType() == int.class){
				
				idBinding.setValue(Integer.parseInt(existingIds.iterator().next()), object);
				
			}else{
				
				idBinding.setValue(existingIds.iterator().next(), object);
			}
			
		}else{
			
			
			if(idBinding.isArray()){

				if(idBinding.getParameterisedType().equals(Integer.class)){
					
					List<Integer> list = new ArrayList<Integer>();
					
					for(String id:existingIds)
						list.add(Integer.parseInt(id));
					
					idBinding.setValue(list.toArray(new Integer[list.size()]), object);
					
					
				}else{

					idBinding.setValue(existingIds.toArray(new String[existingIds.size()]), object);
				}
				
			}else{
				
				// set an collection
				if(idBinding.getParameterisedType().equals(Integer.class)){
					
					Collection<Integer> collection;
					
					if(((ParameterizedType)idBinding.getFieldType()).getRawType().equals(Set.class)){ // is set
						
						collection = new HashSet<Integer>();
						
					}else{ // is list
						
						collection = new ArrayList<Integer>();
					}
					
					for(String id:existingIds)
						collection.add(Integer.parseInt(id));
					
					idBinding.setValue(collection, object);

				}else{
					
					Collection<String> collection;
					
					if(((ParameterizedType)idBinding.getFieldType()).getRawType().equals(Set.class)){ // is set
						
						collection = new HashSet<String>();
						
					}else{ // is list
						
						collection = new ArrayList<String>();
					}
					
					for(String id:existingIds)
						collection.add(id);
					
					idBinding.setValue(collection, object);
					
				}
			}
		}
	}
	
	/**
	 * Add names to an object.
	 * @param topic - The corresponding topic.
	 * @param object - The object.
	 * @param binding - The topic binding.
	 * @throws TopicMapIOException
	 */
	private void addNames(Topic topic, Object object, TopicBinding binding) throws TopicMapIOException{
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof NameBinding){
				
				NameBinding nameBinding = (NameBinding)afb;
				// get name type
				Topic nameType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(nameBinding.getNameType(),this.config.getPrefixMap())));
				
				if(nameType == null)
					continue; // get to next binding if type don't exist
				
				Set<Name> names = topic.getNames(nameType);
				
				if(names.isEmpty())
					continue; // get to next binding if no names of this type are found
				
				Set<String> existingNames = new HashSet<String>();
				
				for(Name name:names)
					existingNames.add(name.getValue());
				
				if(!nameBinding.isArray() && !nameBinding.isCollection()){
					
					if(existingNames.size() > 1)
						throw new TopicMapIOException("Cannot add multiple names to an non container field.");
					
					nameBinding.setValue(existingNames.iterator().next(), object);
					
				}else{
					
					if(nameBinding.isArray()){
						
						nameBinding.setValue(existingNames.toArray(new String[existingNames.size()]), object);
						
					}else{
						
						Collection<String> collection;
						
						if(((ParameterizedType)nameBinding.getFieldType()).getRawType().equals(Set.class)){ // is set
							
							collection = new HashSet<String>();
							
						}else{ // is list
							
							collection = new ArrayList<String>();
						}
						
						for(String name:existingNames)
							collection.add(name);
						
						nameBinding.setValue(collection, object);
						
					}
				}
			}
		}
	}
	
	/**
	 * Adds occurrences to an new object.
	 * @param topic - The corresponding topic.
	 * @param object - The object.
	 * @param binding - The topic binding.
	 * @throws TopicMapIOException
	 */
	@SuppressWarnings("unchecked")
	private void addOccurrences(Topic topic, Object object, TopicBinding binding) throws TopicMapIOException{
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof OccurrenceBinding){
				
				OccurrenceBinding occurrenceBinding = (OccurrenceBinding)afb;
				
				// get occurrence type
				Topic occurrenceType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(occurrenceBinding.getOccurrenceType(),this.config.getPrefixMap())));
				
				if(occurrenceType == null)
					continue; // get to next binding if type don't exist
				
				Set<Occurrence> occurrences = topic.getOccurrences(occurrenceType);
				
				if(occurrences.isEmpty())
					continue; // get to next binding if no occurrence of this type are found
				
				if(!occurrenceBinding.isArray() && !occurrenceBinding.isCollection()){
					
					if(occurrences.size() > 1)
						throw new TopicMapIOException("Cannot add multiple occurrences to an non container field.");
					
					occurrenceBinding.setValue(getOccurrenceValue(occurrences.iterator().next(),occurrenceBinding.getParameterisedType()), object);
					
				}else{
					
					Set values = new HashSet();
					
					for(Occurrence occurrence:occurrences)
						values.add(getOccurrenceValue(occurrence,occurrenceBinding.getParameterisedType()));

					if(occurrenceBinding.isArray()){
						
						Object[] tmp = (Object[])Array.newInstance((Class<?>)occurrenceBinding.getParameterisedType(), values.size());
						values.toArray(tmp);
						occurrenceBinding.setValue(tmp, object);

					}else{
						
						if(((ParameterizedType)occurrenceBinding.getFieldType()).getRawType().equals(Set.class)){ // is set
							
							occurrenceBinding.setValue(values, object);
							
						}else{ // is list
							
							List list = new ArrayList(values);
							occurrenceBinding.setValue(list, object);
							
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Gets the value of an specific occurrence.
	 * @param occurrence - The occurrence.
	 * @param type - The type in which the value needs to be casted.
	 * @return - The value as object.
	 * @throws TopicMapIOException
	 */
	private Object getOccurrenceValue(Occurrence occurrence, Type type) throws TopicMapIOException{

		try{
			
			if(type.equals(int.class))
				return Integer.parseInt(occurrence.getValue());
			else if(type.equals(float.class))
				return Float.parseFloat(occurrence.getValue());
			else if(type.equals(double.class))
				return Double.parseDouble(occurrence.getValue());
			else if(type.equals(Date.class))
				return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(occurrence.getValue());
			else if(type.equals(boolean.class))
				return Boolean.valueOf(occurrence.getValue());
			else if(type.equals(String.class))
				return occurrence.getValue();
			else 
				throw new RuntimeException("Unexspected datatype " + type);
		}
		catch (ParseException e) {
			throw new TopicMapIOException("Occurrence value cannot be parst to date type.");
		}
	}
	
	/**
	 * Adds associations to an new object.
	 * @param topic - The corresponding topic.
	 * @param object - The object.
	 * @param binding - The topic binding.
	 * @throws TopicMapInconsistentException
	 * @throws TopicMapIOException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private void addAssociations(Topic topic, Object object, TopicBinding binding) throws TopicMapInconsistentException, TopicMapIOException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof AssociationBinding){
				
				AssociationBinding associationBinding = (AssociationBinding)afb;

				if(associationBinding.getKind() == AssociationKind.UNARY){
						
					addUnaryAssociation(topic, object, associationBinding);
					
				}else if(associationBinding.getKind() == AssociationKind.BINARY){
						
					addBinaryAssociation(topic, object, associationBinding);
						
				}else{
		
					addNnaryAssociation(topic, object, associationBinding);

				}
			}
		}
	}
	
	/**
	 * Adds unnary associations to an new object.
	 * @param topic - The corresponding topic.
	 * @param object - The object.
	 * @param associationBinding - The association binding.
	 * @throws TopicMapInconsistentException
	 */
	private void addUnaryAssociation(Topic topic, Object object, AssociationBinding associationBinding) throws TopicMapInconsistentException{
		
		// get role type
		Topic roleType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(associationBinding.getPlayedRole(),this.config.getPrefixMap())));
		
		if(roleType == null){
			
			associationBinding.setValue(false, object);
			return;
		}
		
		Set<Role> rolesPlayed = topic.getRolesPlayed(roleType);
		
		if(rolesPlayed.isEmpty()){
			associationBinding.setValue(false, object);
			return;
		}
		
		// get association type
		Topic associationType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(associationBinding.getAssociationType(),this.config.getPrefixMap())));
		
		if(associationType == null){
			associationBinding.setValue(false, object);
			return;
		}
		
		// get matching roles
		Set<Role> matchingRoles = new HashSet<Role>();
		
		for(Role role:rolesPlayed){
			
			if(role.getParent().getType().equals(associationType) && role.getParent().getRoles().size() == 1)
				matchingRoles.add(role);
		}
		
		if(matchingRoles.size() > 1)
			throw new TopicMapInconsistentException("Topic playes more the one time in an unary association of type " + associationBinding.getAssociationType());
		
		if(matchingRoles.size() > 0)
			associationBinding.setValue(true, object);
		
	}
	
	/**
	 * Adds binary associations to an new object.
	 * @param topic - The corresponding topic.
	 * @param object - The object.
	 * @param associationBinding - The association binding.
	 * @throws TopicMapInconsistentException
	 * @throws TopicMapIOException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private void addBinaryAssociation(Topic topic, Object object, AssociationBinding associationBinding) throws TopicMapInconsistentException, TopicMapIOException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		// get role type
		Topic ownRoleType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(associationBinding.getPlayedRole(),this.config.getPrefixMap())));
		
		if(ownRoleType == null)
			return;
		
		Set<Role> rolesPlayed = topic.getRolesPlayed(ownRoleType);
		
		if(rolesPlayed.isEmpty())
			return;
		
		// get association type
		Topic associationType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(associationBinding.getAssociationType(),this.config.getPrefixMap())));
		
		if(associationType == null)
			return;
		
		// get counter player type
		Topic counterPlayerRoleType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(associationBinding.getOtherRole(),this.config.getPrefixMap())));
		
		if(counterPlayerRoleType == null)
			return;
		
		Topic counterPlayerType = getTopicType(associationBinding.getOtherPlayerBinding());
		
		if(counterPlayerType == null)
			return;
		
		// get matching roles
		Set<Role> matchingRoles = new HashSet<Role>();
		
		for(Role role:rolesPlayed){
			
			if(role.getParent().getType().equals(associationType) 
					&& role.getParent().getRoles().size() == 2
					&& TopicMapsUtils.getCounterRole(role.getParent(), role).getType().equals(counterPlayerRoleType)
					&& TopicMapsUtils.getCounterRole(role.getParent(), role).getPlayer().getTypes().contains(counterPlayerType)){
				
				matchingRoles.add(role);
			}
		}
		
		if(matchingRoles.isEmpty())
			return;
		
		// get class type of counter player
		Class<?> counterClass = getBindingHandler().getClassForBinding(associationBinding.getOtherPlayerBinding());
		
		if(counterClass == null)
			throw new TopicMapIOException("Unable to resolve counter player type ");
		
		if(!associationBinding.isArray() && !associationBinding.isCollection()){
			
			if(matchingRoles.size() > 1)
				throw new TopicMapIOException("Cannot add multiple association to an non container field.");

			Object counterPlayer = getInstanceFromTopic(TopicMapsUtils.getCounterRole(matchingRoles.iterator().next().getParent(), matchingRoles.iterator().next()).getPlayer(), associationBinding.getOtherPlayerBinding(), counterClass);
			
			associationBinding.setValue(counterPlayer, object);
			
		}else{
			
			Set<Object> counterPlayers = new HashSet<Object>();
			
			for(Role role:matchingRoles){
				
				Object counterPlayer = getInstanceFromTopic(TopicMapsUtils.getCounterRole(role.getParent(), role).getPlayer(), associationBinding.getOtherPlayerBinding(), counterClass);
				counterPlayers.add(counterPlayer);
			}
			
			if(associationBinding.isArray()){
				
				// is array
				Object[] tmp = (Object[])Array.newInstance((Class<?>)associationBinding.getParameterisedType(), counterPlayers.size());
				counterPlayers.toArray(tmp);
				associationBinding.setValue(tmp, object);
				
	//			associationBinding.setValue(counterPlayers.toArray(), object);
				
			}else{
				
				// is collection
				if(((ParameterizedType)associationBinding.getFieldType()).getRawType().equals(Set.class)){ 
					
					// is set
					associationBinding.setValue(counterPlayers, object);
					
				}else{
					
					// is list
					List<Object> list = new ArrayList<Object>(counterPlayers);
					associationBinding.setValue(list, object);
					
				}
			}
		}
	}
	
	/**
	 * Adds nnary associations to an new object.
	 * @param topic - The corresponding topic.
	 * @param object - The object.
	 * @param associationBinding - The association binding.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private void addNnaryAssociation(Topic topic, Object object, AssociationBinding associationBinding) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		if(associationBinding.getAssociationContainerBinding() == null)
			throw new RuntimeException("An nnary association has to be defined via a association container.");
		
		// get role type
		Topic roleType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(associationBinding.getPlayedRole(),this.config.getPrefixMap())));
		
		if(roleType == null)
			return;
		
		Set<Role> rolesPlayed = topic.getRolesPlayed(roleType);
		
		if(rolesPlayed.isEmpty())
			return;
		
		// get association type
		Topic associationType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(associationBinding.getAssociationType(),this.config.getPrefixMap())));
		
		if(associationType == null)
			return;
		
		// check if the counter player roles match
		
		Set<Topic> containerRoles = getRoleTypesFromContainer(associationBinding.getAssociationContainerBinding());
		
		Type fieldType = associationBinding.getFieldType();
		Class<?> containerClass = ReflectionUtil.getGenericType(fieldType);

		logger.info("Container class is " + containerClass);
		
		Set<Object> counterSet = new HashSet<Object>();
		
		for(Role rolePlayed:rolesPlayed)
		{
			if(rolePlayed.getParent().getType().equals(associationType)){ // skip those where the roletype matches but the association don't
				
				// get counter player
				Set<Role> counterPlayers = TopicMapsUtils.getCounterRoles(rolePlayed.getParent(), rolePlayed);
				
				// check if binding covers all counterplayer
				
				boolean isCovered = true;
				
				for(Role counterPlayer:counterPlayers){
					if(!containerRoles.contains(counterPlayer.getType())){
						isCovered = false;
						break;
					}
				}
				
				if(isCovered){
	
					// create association container
					Object container = null; 
					
					try{
						container = containerClass.getConstructor().newInstance();
					}
					catch (Exception e){
						throw new TopicMapIOException("Cannot instanciate new object: " + e.getMessage());
					}
					
					// add roles to container
					fillContainer(container, associationBinding.getAssociationContainerBinding(), counterPlayers);
					
					// add container to set
					counterSet.add(container);
					
					// add association to cache
					addAssociationToCache(rolePlayed.getParent(), associationBinding); // TODO verify!
				}
			}
		}
		
		// add container to topic instance
		addContainer(object, associationBinding, counterSet);
		
	}
	
	/**
	 * Adds a number of association container objects to an topic object.
	 * @param object - The topic object.
	 * @param binding - The association binding.
	 * @param containerSet - Set of association container.
	 * @throws TopicMapIOException
	 */
	private void addContainer(Object object, AssociationBinding binding, Set<Object> containerSet) throws TopicMapIOException{
		
		if(containerSet.isEmpty())
			return;
		
		if(!binding.isArray() && !binding.isCollection()){
			
			if(containerSet.size() > 1)
				throw new TopicMapIOException("Cannot add multiple instances to an non container field.");
		
			binding.setValue(containerSet.iterator().next(), object);
			
		}else{
			
			if(binding.isArray())
			{
				
				Object[] tmp = (Object[])Array.newInstance((Class<?>)binding.getParameterisedType(), containerSet.size());
				containerSet.toArray(tmp);
				binding.setValue(tmp, object);
				
				//binding.setValue(containerSet.toArray(), object);
				
			}else{
				
				if(((ParameterizedType)binding.getFieldType()).getRawType().equals(Set.class)){ // is set
					
					binding.setValue(containerSet, object);
					
				}else{
					
					List<Object> list = new ArrayList<Object>(containerSet);
					binding.setValue(list, object);
				}
			}
		}
	}
	
	/**
	 * Fills an association container object according to an set of roles.
	 * @param container - The container object.
	 * @param binding - The association container binding.
	 * @param counterPlayer - Set of roles.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 */
	private void fillContainer(Object container, AssociationContainerBinding binding, Set<Role> counterPlayer) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		if(counterPlayer.isEmpty())
			return;
		
		for(RoleBinding roleBinding:binding.getRoleBindings()){
			
			Topic roleType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(roleBinding.getRoleType(),this.config.getPrefixMap())));
			
			Set<Topic> counterTopics = new HashSet<Topic>();
			
			for(Role counter:counterPlayer){
				
				if(counter.getType().equals(roleType)){
					
					counterTopics.add(counter.getPlayer());
				}
			}

			// create instances
			
			Set<Object> counterObjects = new HashSet<Object>();
			Class<?> playerClass = ReflectionUtil.getGenericType(roleBinding.getFieldType());
			
			logger.info("Add instances of class " + playerClass + " to countainer...");
			
			for(Topic topic:counterTopics){
				
				Object obj = getInstanceFromTopic(topic,(TopicBinding)getBindingHandler().getBinding(playerClass), playerClass);
				counterObjects.add(obj);
			}
			
			// add the new objects to the container field
			addObjectsToContainerField(container, counterObjects, roleBinding);

		}
	}
	
	/**
	 * Adds a number of objects to an role field.
	 * @param container - The association container to which the role field belongs.
	 * @param objects - Set of objects.
	 * @param roleBinding - The role binding.
	 * @throws TopicMapIOException
	 */
	private void addObjectsToContainerField(Object container, Set<Object> objects, RoleBinding roleBinding) throws TopicMapIOException{
		
		if(objects.isEmpty())
			return;
		
		if(!roleBinding.isArray() && !roleBinding.isCollection()){
			
			if(objects.size() > 1)
				throw new TopicMapIOException("Cannot add multiple instances to an non container field.");
		
			roleBinding.setValue(objects.iterator().next(), container);
			
		}else{
			
			if(roleBinding.isArray())
			{
				roleBinding.setValue(objects.toArray(), container);
				
			}else{
				
				if(((ParameterizedType)roleBinding.getFieldType()).getRawType().equals(Set.class)){ // is set
					
					roleBinding.setValue(objects, container);
					
				}else{
					
					List<Object> list = new ArrayList<Object>(objects);
					roleBinding.setValue(list, container);
				}
			}
		}
		
	}
	

	/**
	 * Returns a set of role types occurring in an association container.
	 * @param containerBinding - The association container.
	 * @return Set of role types.
	 */
	private Set<Topic> getRoleTypesFromContainer(AssociationContainerBinding containerBinding){
		
		Set<Topic> types = new HashSet<Topic>();
		
		for(RoleBinding roleBinding:containerBinding.getRoleBindings()){
			Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(TopicMapsUtils.resolveURI(roleBinding.getRoleType(),this.config.getPrefixMap())));
			types.add(roleType);
		}
		
		return types;
	}
	
	/**
	 * Adds an flag to an set.
	 * @param <T> - Template for the set parameter
	 * @param set - The set.
	 * @return A map where each element of the set has an additional boolean value.
	 */
	private <T extends Object> Map<T, Match> addFlags(Set<T> set){
		
		Map<T, Match> map = new HashMap<T, Match>();
		
		if(set == null)
			return map;
		
		for(T obj:set)
			map.put(obj, Match.NO);

		return map;
	}
	
	/**
	 * Creates a new topic by an identifier. 
	 * @param topicObject - The object representing the new topic.
	 * @param binding - The topic binding.
	 * @return The new topic.
	 * @throws TopicMapIOException
	 */
	private Topic createTopicByIdentifier(Object topicObject, TopicBinding binding) throws TopicMapIOException{

		Topic topic = getTopicByIdentifier(topicObject, binding);
			
		if(topic == null){
			
			// get subject identifier
			Set<String> subjectIdentifier = getIdentifier(topicObject, binding, IdType.SUBJECT_IDENTIFIER);
			// get subject locator
			Set<String> subjectLocator = getIdentifier(topicObject, binding, IdType.SUBJECT_LOCATOR);
			// get item identifier
			Set<String> itemIdentifier = getIdentifier(topicObject, binding, IdType.ITEM_IDENTIFIER);
			
			// create new topic
			topic = createNewTopic(subjectIdentifier, subjectLocator, itemIdentifier);
			
		}else logger.info("Topic already exist.");

		return topic;
	}
	
	/**
	 * Gets a topic by an identifier specified by an topic object.
	 * @param topicObject - The topic object.
	 * @param binding - The topic binding.
	 * @return The topic or null if not found.
	 */
	private Topic getTopicByIdentifier(Object topicObject, TopicBinding binding){
		
		// get subject identifier
		Set<String> subjectIdentifier = getIdentifier(topicObject, binding, IdType.SUBJECT_IDENTIFIER);
		// get subject locator
		Set<String> subjectLocator = getIdentifier(topicObject, binding, IdType.SUBJECT_LOCATOR);
		// get item identifier
		Set<String> itemIdentifier = getIdentifier(topicObject, binding, IdType.ITEM_IDENTIFIER);
		
		Topic topic = null;

		// try to find the topic
		
		topic = getTopicBySubjectIdentifier(subjectIdentifier);
		
		if(topic == null)
			topic = getTopicBySubjectLocator(subjectLocator);
			
		if(topic == null)
			topic = getTopicByItemIdentifier(itemIdentifier);
		
		return topic;
		
	}
	
	
	/**
	 * Gets a topic specified by one of a number of subject identifier.
	 * @param subjectIdentifier - Set of subject identifier.
	 * @return The topic or null if not found.
	 */
	private Topic getTopicBySubjectIdentifier(Set<String> subjectIdentifier){
		
		Topic topic = null;
		
		for(String si:subjectIdentifier){
			
			topic = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(si));
			
			if(topic != null)
				return topic;
		}
		
		return null;
	}
	
	
	/**
	 * Gets a topic specified by one of a number of subject locator.
	 * @param subjectLocator - Set of subject locator.
	 * @return The topic or null if not found.
	 */
	private Topic getTopicBySubjectLocator(Set<String> subjectLocator){
		
		Topic topic = null;
		
		for(String sl:subjectLocator){
			
			topic = getTopicMap().getTopicBySubjectLocator(getTopicMap().createLocator(sl));
			
			if(topic != null)
				return topic;
		}
		
		return null;
	}
	
	
	/**
	 * Gets a topic specified by one of a number of item identifier.
	 * @param itemIdentifier - Set of item identifier.
	 * @return The topic or null if not found.
	 */
	private Topic getTopicByItemIdentifier(Set<String> itemIdentifier){
		
		Construct construct = null;
		
		for(String ii:itemIdentifier){
			
						
			construct = getTopicMap().getConstructByItemIdentifier(getTopicMap().createLocator(ii));

			if(construct != null){
				
				if(!(construct instanceof Topic))
					throw new RuntimeException("The item identifier " + ii + " is already used by a non topic construct.");
				
				return (Topic)construct;
				
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets a set if identifier of an specific type from an topic object.
	 * @param topicObject - The topic object.
	 * @param binding - The topic binding.
	 * @param type - The identifier type.
	 * @return A set of identifier as strings.
	 */
	@SuppressWarnings("unchecked")
	private Set<String> getIdentifier(Object topicObject, TopicBinding binding, IdType type){
		
		Set<String> identifier = null;
				
		if(binding.getParent() != null)
			identifier = getIdentifier(topicObject, binding.getParent(), type);
		else identifier = new HashSet<String>();

		// create base locator
		//String baseLocator = TopicMapsUtils.resolveURI("base_locator:", this.config.getPrefixMap()) + topicObject.getClass().getName().replaceAll("\\.", "/") + "/";

		String typeLocator = binding.getIdentifier().iterator().next(); // get first
		
		
		// add all subject identifier
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof IdBinding && ((IdBinding)afb).getIdtype() == type){
				
				if(afb.getValue(topicObject) != null){ // ignore empty values

					if(((IdBinding)afb).isArray()){
					
						for (Object obj:(Object[])((IdBinding)afb).getValue(topicObject)){
							
							if (obj instanceof String){
								identifier.add(obj.toString());
							}else{
								//identifier.add(baseLocator + obj.toString());
								identifier.add(typeLocator + "/" + obj.toString());
							}
						}
					
					}else if(((IdBinding)afb).isCollection()){
						
						for (Object obj:(Collection<Object>)((IdBinding)afb).getValue(topicObject)){
							
							if (obj instanceof String){
								identifier.add(obj.toString());
							}else{
								//identifier.add(baseLocator + obj.toString());
								identifier.add(typeLocator + "/" + obj.toString());
							}
						}
		
					}else{
						if (((IdBinding)afb).getValue(topicObject) instanceof String){
							identifier.add(((IdBinding)afb).getValue(topicObject).toString());
						}else{
							//identifier.add(baseLocator + ((IdBinding)afb).getValue(topicObject).toString());
							identifier.add(typeLocator + "/" + ((IdBinding)afb).getValue(topicObject).toString());
						}
					}
				}
			}
		}
		
		return identifier;
	}
	
	
	/**
	 * Gets the names specified in an topic object.
	 * @param topicObject - The topic object.
	 * @param binding - The topic binding.
	 * @return A map using the name binding as key and a set of strings for the corresponding names.
	 */
	@SuppressWarnings("unchecked")
	private Map<NameBinding, Set<String>> getNames(Object topicObject, TopicBinding binding){
		
		Map<NameBinding, Set<String>> map = null;
		
		if(binding.getParent() != null)
			map = getNames(topicObject, binding.getParent());
		else map = new HashMap<NameBinding, Set<String>>();
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof NameBinding){
				
				if(((NameBinding)afb).getValue(topicObject) != null){
					
					if(((NameBinding)afb).isArray()){
						
						for (Object obj:(Object[])((NameBinding)afb).getValue(topicObject)){
						
							addValueToBindingMap(map, (NameBinding)afb, obj.toString());
						}
					
					}else if(((NameBinding)afb).isCollection()){
						
						for (Object obj:(Collection<Object>)((NameBinding)afb).getValue(topicObject)){
							
							addValueToBindingMap(map, (NameBinding)afb, obj.toString());
						}
		
					}else{
						
						addValueToBindingMap(map, (NameBinding)afb, ((NameBinding)afb).getValue(topicObject).toString());
					}
				}
			}
		}
		
		return map;
	}
	
	
	/**
	 * Gets the occurrences from an specific topic object.
	 * @param topicObject - The topic object.
	 * @param binding - The topic binding.
	 * @return A map using the occurrence binding as key and a set of strings for the corresponding values.
	 */
	@SuppressWarnings("unchecked")
	private Map<OccurrenceBinding, Set<String>> getOccurrences(Object topicObject, TopicBinding binding){
		
		Map<OccurrenceBinding, Set<String>> map = null;
		
		if(binding.getParent() != null)
			map = getOccurrences(topicObject, binding.getParent());
		else map = new HashMap<OccurrenceBinding, Set<String>>();
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
					
			if (afb instanceof OccurrenceBinding) {

				if (((OccurrenceBinding) afb).getValue(topicObject) != null) {

					if (((OccurrenceBinding) afb).isArray()) {

						for (Object obj : (Object[]) ((OccurrenceBinding) afb).getValue(topicObject)) {

							if(obj instanceof Date)
								addValueToBindingMap(map, (OccurrenceBinding) afb, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(obj));
							else
								addValueToBindingMap(map, (OccurrenceBinding) afb, obj.toString());
						}

					} else if (((OccurrenceBinding) afb).isCollection()) {

						for (Object obj : (Collection<Object>) ((OccurrenceBinding) afb).getValue(topicObject)) {

							if(obj instanceof Date)
								addValueToBindingMap(map, (OccurrenceBinding) afb, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(obj));
							else
								addValueToBindingMap(map, (OccurrenceBinding) afb, obj.toString());
						}

					} else {

						if(((OccurrenceBinding) afb).getValue(topicObject) instanceof Date)
							addValueToBindingMap(map, (OccurrenceBinding) afb, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(((OccurrenceBinding) afb).getValue(topicObject)));
						else
							addValueToBindingMap(map, (OccurrenceBinding) afb,((OccurrenceBinding) afb).getValue(topicObject).toString());

					}
				}
			}
		}
		
		return map;
	}
	
	
	/**
	 * Gets the associations from an specific topic object.
	 * @param topicObject - The topic object.
	 * @param binding - The topic binding.
	 * @return A map using the association binding as key and a set of objects as values.
	 */
	@SuppressWarnings("unchecked")
	private Map<AssociationBinding, Set<Object>> getAssociations(Object topicObject, TopicBinding binding){
		
		Map<AssociationBinding, Set<Object>> map = null;
		
		if(binding.getParent() != null)
			map = getAssociations(topicObject, binding.getParent());
		else map = new HashMap<AssociationBinding, Set<Object>>();
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof AssociationBinding){
				
				if(((AssociationBinding)afb).getValue(topicObject) != null){
					
					if(((AssociationBinding)afb).getValue(topicObject) instanceof Boolean){
						
						addValueToBindingMap(map, (AssociationBinding) afb, ((AssociationBinding)afb).getValue(topicObject));
						
					}else{
						
						if(((AssociationBinding) afb).isArray()){

							for(Object obj : (Object[]) ((AssociationBinding) afb).getValue(topicObject)) {

								addValueToBindingMap(map, (AssociationBinding) afb, obj);
							}

						}else if(((AssociationBinding) afb).isCollection()) {

							for(Object obj : (Collection<Object>) ((AssociationBinding) afb).getValue(topicObject)) {

								addValueToBindingMap(map, (AssociationBinding) afb, obj);
							}

						}else{

							addValueToBindingMap(map, (AssociationBinding) afb,((AssociationBinding) afb).getValue(topicObject));
						}
					}
					
				}else{
					addValueToBindingMap(map, (AssociationBinding) afb,null);
				}
			}
		}
		
		
		return map;
	}
	
	/**
	 * Adds a new string value to an map using field bindings as key. 
	 * @param <T> - Template for the field binding.
	 * @param map - The map.
	 * @param binding - The binding, i.e. key.
	 * @param value - The value.
	 */
	private <T extends AbstractFieldBinding> void addValueToBindingMap(Map<T,Set<String>> map, T binding, String value){
		
		Set<String> set = map.get(binding);
		
		if(set == null)
			set = new HashSet<String>();
		
		set.add(value);
		
		map.put(binding, set);
	}
	
	/**
	 * Adds a new object to an map using field bindings as key. 
	 * @param <T> - Template for the field binding.
	 * @param map - The map.
	 * @param binding - The binding, i.e. key.
	 * @param object - The object.
	 */
	private <T extends AbstractFieldBinding> void addValueToBindingMap(Map<T,Set<Object>> map, T binding, Object object){
		
		Set<Object> set = map.get(binding);
		
		if(set == null)
			set = new HashSet<Object>();
		
		set.add(object);
		
		map.put(binding, set);
	}
	
	/**
	 * Creates a new topic whether by an subject identifier, an subject locator or an item identifier.
	 * @param subjectIdentifier - Set of available subject identifiers as string.
	 * @param subjectLocator - Set of available subject locators as string.
	 * @param itemIdentifier - Set of available item identifiers as string.
	 * @return The topic.
	 * @throws TopicMapIOException
	 */
	private Topic createNewTopic(Set<String> subjectIdentifier, Set<String> subjectLocator, Set<String> itemIdentifier) throws TopicMapIOException{
		
		Topic topic = null;
		
		if(!subjectIdentifier.isEmpty()){
			topic = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(subjectIdentifier.iterator().next()));
			
		}else if(!subjectLocator.isEmpty()){
			topic = getTopicMap().createTopicBySubjectLocator(getTopicMap().createLocator(subjectLocator.iterator().next()));
			
		}else if(!itemIdentifier.isEmpty()){
			topic = getTopicMap().createTopicByItemIdentifier(getTopicMap().createLocator(itemIdentifier.iterator().next()));
			
		}else{
			throw new TopicMapIOException("Cannot create an new topic without an identifier.");
		}
				
		return topic;
	}
		
	/**
	 * Adds a topic to the topic cache.
	 * @param topic - The topic.
	 * @param object - The object representing the topic.
	 */
	private void addTopicToCache(Topic topic, Object object){
		
		if(this.topicCache == null)
			this.topicCache = new HashMap<Object, Topic>();

		this.topicCache.put(object, topic);
		
	}
	
	/**
	 * Retrieves an topic from the cache.
	 * @param object - The object representing the topic.
	 * @return The topic or null if not found.
	 */
	private Topic getTopicFromCache(Object object){
		
		if(this.topicCache == null)
			return null;

		return this.topicCache.get(object);
	}
	
	/**
	 * Returns the binding handler. Creates a new one if not exist.
	 * @return The binding handler.
	 */
	private BindingHandler getBindingHandler(){
		
		if(bindingHandler == null)
			bindingHandler = new BindingHandler(this.config);
		
		return bindingHandler;
		
	}

	/**
	 * Adds an object to the temporary object cache.
	 * @param object - The object.
	 * @param topic - The topic for which the object is created.
	 */
	private void addObjectToCache(Object object, Topic topic){
		
		if(this.objectCache == null)
			this.objectCache = new HashMap<Topic, Object>();
		
		this.objectCache.put(topic, object);
	}
	
	/**
	 * Retrieves an object from the temporary object cache.
	 * @param topic - The topic for which the object should be created.
	 * @return -The object or null if not found.
	 */
	private Object getObjectFromCache(Topic topic){
		
		if(this.objectCache == null)
			return null;
		
		return this.objectCache.get(topic);
	}
	
	/**
	 * Clears the temporary object cache.
	 */
	private void clearObjectCache(){
		this.objectCache = null;
	}
	
	private void addAssociationToCache(Association association, AssociationBinding binding){
		
		if(this.associationCache == null)
			this.associationCache = new HashMap<Association, AssociationBinding>();
		
		this.associationCache.put(association, binding);
		
	}
	
	private void removeAssociationFromCache(Association association){
		
		if(this.associationCache == null)
			return;
		
		this.associationCache.remove(association);
	
	}
	
	
	
}
