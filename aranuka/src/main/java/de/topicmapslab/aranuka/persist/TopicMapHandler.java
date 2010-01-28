package de.topicmapslab.aranuka.persist;

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

// handles interaction with the topic map, i.e. creating topics and associations, updating, etc.
public class TopicMapHandler {

	private static Logger logger = LoggerFactory.getLogger(TopicMapHandler.class);
	
	private Configuration config; // the configuration
	private BindingHandler bindingHandler; // the binding handler
	private TopicMap topicMap; // the topic map
	
	private Map<Object, Topic> topicCache;
	
	private Map<Topic, Object> objectCache;
	
	
	// --[ public methods ]------------------------------------------------------------------------------
		
	public TopicMapHandler(Configuration config, TopicMap topicMap){

		if(config == null)
			throw new RuntimeException("Configuration is null.");
		
		if(topicMap == null)
			throw new RuntimeException("Topic map is null.");
		
		this.config = config;
		this.topicMap = topicMap;
	}
	
	public void invokeBinding() throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		getBindingHandler().createBindingsForAllClasses();

	}
	
	/// TODO persist only topics or association container as well?
	public void persist(Object topicObject) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException, TopicMapInconsistentException{
		
		List<Object> topicToPersist = new ArrayList<Object>();
				
		topicToPersist.add(topicObject);
		persistTopics(topicToPersist);
	}
		
	public TopicMap getTopicMap()
	{
		if(this.topicMap == null)
			throw new RuntimeException("Topic Map is null.");
			
		return this.topicMap;
	}
	
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
	
	// used recursively
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
	
	
	// update instance from topic map
	
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
					
					Set<Locator> identifier = topic.getSubjectIdentifiers();
					addIdentifier(topic, object, idBinding, identifier);
					
				}else{
					
					/// TODO handle uhnknown idtype
				}
					
			}
		}
	}
	
	
	private void addIdentifier(Topic topic, Object object, IdBinding idBinding, Set<Locator> identifiers) throws TopicMapIOException{
		
		if(identifiers.isEmpty())
			return;
		
		Set<String> typeIdentifier = ((TopicBinding)idBinding.getParent()).getIdentifier();
			
		// create set
		Set<String> existingIds = new HashSet<String>();
		
		for(Locator identifier:identifiers){
			
			String iri = identifier.getReference();
			
			// check and remove type identifier
			for(String typeId:typeIdentifier){
				
				if(iri.startsWith(typeId + "/")){
					
					logger.info("Reduce identifier from " + iri);
					iri = iri.replace(typeId + "/", ""); // remove the start
					logger.info("to " + iri);
					break;
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

				if(idBinding.getGenericType().equals(Integer.class)){
					
					List<Integer> list = new ArrayList<Integer>();
					
					for(String id:existingIds)
						list.add(Integer.parseInt(id));
					
					idBinding.setValue(list.toArray(new Integer[list.size()]), object);
					
					
				}else{

					idBinding.setValue(existingIds.toArray(new String[existingIds.size()]), object);
				}
				
			}else{
				
				// set an collection
				if(idBinding.getGenericType().equals(Integer.class)){
					
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
	

	private void addNames(Topic topic, Object object, TopicBinding binding) throws TopicMapIOException{
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof NameBinding){
				
				NameBinding nameBinding = (NameBinding)afb;
				// get name type
				Topic nameType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(nameBinding.getNameType()));
				
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
	
	
	@SuppressWarnings("unchecked")
	private void addOccurrences(Topic topic, Object object, TopicBinding binding) throws TopicMapIOException{
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof OccurrenceBinding){
				
				OccurrenceBinding occurrenceBinding = (OccurrenceBinding)afb;
				
				// get occurrence type
				Topic occurrenceType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(occurrenceBinding.getOccurrenceType()));
				
				if(occurrenceType == null)
					continue; // get to next binding if type don't exist
				
				Set<Occurrence> occurrences = topic.getOccurrences(occurrenceType);
				
				if(occurrences.isEmpty())
					continue; // get to next binding if no occurrence of this type are found
				
				if(!occurrenceBinding.isArray() && !occurrenceBinding.isCollection()){
					
					if(occurrences.size() > 1)
						throw new TopicMapIOException("Cannot add multiple occurrences to an non container field.");
					
					occurrenceBinding.setValue(getOccurrenceValue(occurrences.iterator().next(),occurrenceBinding.getGenericType()), object);
					
				}else{
					
					Set values = new HashSet();
					
					for(Occurrence occurrence:occurrences)
						values.add(getOccurrenceValue(occurrence,occurrenceBinding.getGenericType()));

					if(occurrenceBinding.isArray()){

						occurrenceBinding.setValue(values.toArray(), object);

					}else{
						
						logger.info(occurrenceBinding.getFieldType().toString());

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
			else if(type.equals(Boolean.class))
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
	
	private void addUnaryAssociation(Topic topic, Object object, AssociationBinding associationBinding) throws TopicMapInconsistentException{
		
		// get role type
		Topic roleType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(associationBinding.getPlayedRole()));
		
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
		Topic associationType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(associationBinding.getAssociationType()));
		
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
		
		associationBinding.setValue(true, object);
		
	}
	
	private void addBinaryAssociation(Topic topic, Object object, AssociationBinding associationBinding) throws TopicMapInconsistentException, TopicMapIOException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		// get role type
		Topic roleType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(associationBinding.getPlayedRole()));
		
		if(roleType == null)
			return;
		
		Set<Role> rolesPlayed = topic.getRolesPlayed(roleType);
		
		if(rolesPlayed.isEmpty())
			return;
		
		// get association type
		Topic associationType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(associationBinding.getAssociationType()));
		
		if(associationType == null)
			return;
		
		// get counter player type
		Topic counterType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(associationBinding.getOtherRole()));
		
		if(counterType == null)
			return;
		
		// get matching roles
		Set<Role> matchingRoles = new HashSet<Role>();
		
		for(Role role:rolesPlayed){
			
			if(role.getParent().getType().equals(associationType) 
					&& role.getParent().getRoles().size() == 2
					&& TopicMapsUtils.getCounterRole(role.getParent(), role).getType().equals(counterType)){
				
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
				
				Object counterPlayer = getInstanceFromTopic(TopicMapsUtils.getCounterRole(matchingRoles.iterator().next().getParent(), role).getPlayer(), associationBinding.getOtherPlayerBinding(), counterClass);
				counterPlayers.add(counterPlayer);
			}
			
			if(associationBinding.isArray()){
				
				// is array
				associationBinding.setValue(counterPlayers.toArray(), object);
				
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
	
	private void addNnaryAssociation(Topic topic, Object object, AssociationBinding associationBinding) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		if(associationBinding.getAssociationContainerBinding() == null)
			throw new RuntimeException("An nnary association has to be defined via a association container.");
		
		// get role type
		Topic roleType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(associationBinding.getPlayedRole()));
		
		if(roleType == null)
			return;
		
		Set<Role> rolesPlayed = topic.getRolesPlayed(roleType);
		
		if(rolesPlayed.isEmpty())
			return;
		
		// get association type
		Topic associationType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(associationBinding.getAssociationType()));
		
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
				Set<Role> counterPlayers = TopicMapsUtils.getCounterPlayers(rolePlayed.getParent(), rolePlayed);
				
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
				}
			}
		}
		
		// add container to topic instance
		addContainer(object, associationBinding, counterSet);
		
	}
	
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
				binding.setValue(containerSet.toArray(), object);
				
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
	

	private void fillContainer(Object container, AssociationContainerBinding binding, Set<Role> counterPlayer) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException{
		
		if(counterPlayer.isEmpty())
			return;
		
		for(RoleBinding roleBinding:binding.getRoleBindings()){
			
			Topic roleType = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(roleBinding.getRoleType()));
			
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
	
	// --[ private methods ]-------------------------------------------------------------------------------

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
	
	
	private Topic getTopicType(TopicBinding binding) throws BadAnnotationException{
		
		if(binding.getIdentifier().isEmpty())
			throw new BadAnnotationException("Topic type has no identifier.");
		
		Topic type = null;
		
		// try to find type
		for(String identifier:binding.getIdentifier()){
			
			type = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(identifier));
			if(type != null)
				return  type;
		}
		
		type = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getIdentifier().iterator().next()));
		
		if(binding.getName() != null)
			type.createName(binding.getName());
		
		return type;
	}
	
	
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
	
	
	// modifies the topic subject identifier to represent the current java object
	// used for create new topic as well
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
	
	
	// modifies the topic names to represent the current java object
	// used for create new topic as well
	private void updateNames(Topic topic, Object topicObject, TopicBinding binding){
		
		// get new names
		Map<NameBinding,Set<String>> newNames = getNames(topicObject, binding);
		
		// get actual names
		Map<Name,Match> actualNames = addFlags(topic.getNames());
		
		// update
		for(Map.Entry<NameBinding, Set<String>> newName:newNames.entrySet()){
			
			// get type and scope for this binding/field
			Topic nameType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(newName.getKey().getNameType()));
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
	
	
	// modifies the topic occurrences to represent the current java object
	// used for create new topic as well
	private void updateOccurrences(Topic topic, Object topicObject, TopicBinding binding){
		
		// get new occurrences
		Map<OccurrenceBinding,Set<String>> newOccurrences = getOccurrences(topicObject, binding);
		
		// get actual occurrences
		Map<Occurrence,Match> actualOccurrences = addFlags(topic.getOccurrences());
		
		// update
		for(Map.Entry<OccurrenceBinding, Set<String>> newOccurrence:newOccurrences.entrySet()){
			
			// get type and scope for this binding/field
			Topic occurrenceType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(newOccurrence.getKey().getOccurrenceType()));
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
	
	
	// modifies the topic associations to represent the current java object
	// used for create new topic as well
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
		
			if(entry.getValue() == Match.BINDING){ // only binding match found but no instance
				Association obsolete = entry.getKey().getParent();
				
				if(obsolete != null){ // do an additional check
					logger.info("Remove obsolete association " + obsolete.getType());
					obsolete.remove();
				}
			}
		}
	}
	
	
	private void updateUnaryAssociation(Topic topic, AssociationBinding binding, Set<Object> associationObjects, Map<Role,Match> playedRoles){
		
		if(associationObjects.size() != 1)
			throw new RuntimeException("Unary association has more the one type."); // TODO use other exception type and better message
		
		Topic associationType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getAssociationType()));
		Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getPlayedRole()));
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

	private void updateBinaryAssociations(Topic topic, AssociationBinding binding, Set<Object> associationObjects, Map<Role, Match> playedRoles, List<Object> topicObjects) throws TopicMapInconsistentException, TopicMapIOException, BadAnnotationException{

		Topic associationType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getAssociationType()));
		Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getPlayedRole()));
		Topic otherRoleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getOtherRole()));
		Set<Topic> scope = binding.getScope(getTopicMap());

		for (Object associationObject : associationObjects) { // check each binary association

			// get counter player
			Topic counterPlayer = createTopicByIdentifier(associationObject, binding.getOtherPlayerBinding());
			counterPlayer.addType(getTopicType(binding.getOtherPlayerBinding()));
			
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
					
					if(TopicMapsUtils.getCounterRole(playedRole.getKey().getParent(), playedRole.getKey()).getPlayer().equals(counterPlayer)){ // check counter player

						found = true;
						playedRole.setValue(Match.INSTANCE);
						break;
					}
				}
			}
			
			if(!found){
				
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

	private void updateNnaryAssociations(Topic topic, AssociationBinding binding, Set<Object> associationObjects, Map<Role, Match> playedRoles, List<Object> topicObjects) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException{

		Topic associationType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getAssociationType()));
		Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getPlayedRole()));
		Set<Topic> scope = binding.getScope(getTopicMap());
		
		for (Object associationObject:associationObjects) { // check each nnary association
			
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
				
				if(binding.isPersistOnCascade()){

					addCascadingRole(associationObject, topicObjects);
					
				}
			}
		}
	}
	
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
	
	
	@SuppressWarnings("unchecked") /// TODO add recursively!!!
	private Map<Topic,Set<Topic>> getRolesFromContainer(Object associationContainerInstance) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException{
		
		Map<Topic,Set<Topic>> result = new HashMap<Topic, Set<Topic>>();
		
		AssociationContainerBinding binding = (AssociationContainerBinding)getBindingHandler().getBinding(associationContainerInstance.getClass());
		
		for(RoleBinding roleBinding:binding.getRoleBindings()){
			
			Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(roleBinding.getRoleType()));
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
	
	private Set<Topic> getRoleTypesFromContainer(AssociationContainerBinding containerBinding){
		
		Set<Topic> types = new HashSet<Topic>();
		
		for(RoleBinding roleBinding:containerBinding.getRoleBindings()){
			Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(roleBinding.getRoleType()));
			types.add(roleType);
		}
		
		return types;
	}
	
	// adds flags to an set, returns an empty map of the set was null
	private <T extends Object> Map<T, Match> addFlags(Set<T> set){
		
		Map<T, Match> map = new HashMap<T, Match>();
		
		if(set == null)
			return map;
		
		for(T obj:set)
			map.put(obj, Match.NO);

		return map;
	}
	
	
	private Topic createTopicByIdentifier(Object topicObject, TopicBinding binding) throws TopicMapIOException{
		
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
			
		if(topic == null){
			
			// create new topic
			topic = createNewTopic(subjectIdentifier, subjectLocator, itemIdentifier);
			
		}else logger.info("Topic already exist.");

		return topic;
	}
	
	
	// tries to find an existing topic by a list of subject identifiers
	private Topic getTopicBySubjectIdentifier(Set<String> subjectIdentifier){
		
		Topic topic = null;
		
		for(String si:subjectIdentifier){
			
			topic = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(si));
			
			if(topic != null)
				return topic;
		}
		
		return null;
	}
	
	
	// tries to find an existing topic by a list of subject locators
	private Topic getTopicBySubjectLocator(Set<String> subjectLocator){
		
		Topic topic = null;
		
		for(String sl:subjectLocator){
			
			topic = getTopicMap().getTopicBySubjectLocator(getTopicMap().createLocator(sl));
			
			if(topic != null)
				return topic;
		}
		
		return null;
	}
	
	
	// tries to find an existing topic by a list of item identifiers
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
	
	
	// used recursively
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
	
	
	// used recursively
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
	
	
	// used recursively
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
	
	
	// used recursively
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
				}
			}
		}
		
		
		return map;
	}
	
		
	private <T extends AbstractFieldBinding> void addValueToBindingMap(Map<T,Set<String>> map, T binding, String value){
		
		Set<String> set = map.get(binding);
		
		if(set == null)
			set = new HashSet<String>();
		
		set.add(value);
		
		map.put(binding, set);
	}
	
	
	private <T extends AbstractFieldBinding> void addValueToBindingMap(Map<T,Set<Object>> map, T binding, Object object){
		
		Set<Object> set = map.get(binding);
		
		if(set == null)
			set = new HashSet<Object>();
		
		set.add(object);
		
		map.put(binding, set);
	}
	
	
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
	
	
	// private getter and setter
	
	private void addTopicToCache(Topic topic, Object object){
		
		if(this.topicCache == null)
			this.topicCache = new HashMap<Object, Topic>();

		this.topicCache.put(object, topic);
		
	}
	
	
	private Topic getTopicFromCache(Object object){
		
		if(this.topicCache == null)
			return null;

		return this.topicCache.get(object);
	}
	
	
	private BindingHandler getBindingHandler(){
		
		if(bindingHandler == null)
			bindingHandler = new BindingHandler(this.config);
		
		return bindingHandler;
		
	}

	
	private void addObjectToCache(Object object, Topic topic){
		
		if(this.objectCache == null)
			this.objectCache = new HashMap<Topic, Object>();
		
		this.objectCache.put(topic, object);
	}
	
	private Object getObjectFromCache(Topic topic){
		
		if(this.objectCache == null)
			return null;
		
		return this.objectCache.get(topic);
	}
	
	private void clearObjectCache(){
		this.objectCache = null;
	}
	
}
