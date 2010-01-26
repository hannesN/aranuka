package de.topicmapslab.aranuka.persist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.tmapi.core.TopicMapSystemFactory;
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
	
	// --[ public methods ]------------------------------------------------------------------------------
	
	public TopicMapHandler(Configuration config){

		this.config = config;
	}
	
	public TopicMapHandler(Configuration config, TopicMap topicMap){

		this.config = config;
		this.topicMap = topicMap;
	}
	
	public void invokeBinding()  throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		getBindingHandler().createBindingsForAllClasses();
		
//		// test
//		getBindingHandler().printBindings();
	}
	
	/// TODO persist only topics or association container as well?
	public void persist(Object topicObject) throws IOException, BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException, TopicMapInconsistentException {
		
		List<Object> topicToPersist = new ArrayList<Object>();
				
		topicToPersist.add(topicObject);
		persistTopics(topicToPersist);
	}
	
	
	public TopicMap getTopicMap() throws IOException /// TODO change exception type?
	{
		try{
			
			if(this.topicMap == null)
				this.topicMap = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap(this.config.getBaseLocator());
			
			return this.topicMap;
		}
		catch (Exception e) {
			throw new IOException("Could not create topic map (" + e.getMessage() + ")");
		}
	}
	
	@Deprecated
	public void setTopicMap(TopicMap topicMap) throws IOException{
		
		if(this.topicMap != null)
			throw new IOException("The session has already a topic map, possible data lost.");
		
		this.topicMap = topicMap;
		
	}
	
	
	public Set<Object> getTopicsByType(Class<?> clazz) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException, IOException{
		
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
	
	private Set<Object> getInstancesFromTopics(Set<Topic> topics, TopicBinding binding, Class<?> clazz) throws IOException{
		
		if(topics.isEmpty())
			return Collections.emptySet();
		
		Set<Object> objects = new HashSet<Object>();
		
		for(Topic topic:topics){
			
			objects.add(getInstanceFromTopic(topic, binding, clazz));
			
		}
		
		return objects;
	}
	
	// used recursively
	private Object getInstanceFromTopic(Topic topic, TopicBinding binding, Class<?> clazz) throws IOException{
		
		try{
			
			Object object = null;
			
			if(binding.getParent() != null)
				object = getInstanceFromTopic(topic, binding.getParent(), clazz);
			else object = clazz.getConstructor().newInstance();
			
			// get identifier
			
			addIdentifier(topic, object, binding);
			
			// get names
			
			// get occurrences
			
			// get associations
			
			return object;
			
		}
		catch (Exception e) {
			throw new IOException("Could not instanciate topic: " + e.getMessage()); /// TODO change exception type, maybe create new one
		}
	}
	
	private void addIdentifier(Topic topic, Object object, TopicBinding binding) throws TopicMapInconsistentException{
		
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
	
	private void addIdentifier(Topic topic, Object object, IdBinding idBinding, Set<Locator> identifiers) throws TopicMapInconsistentException{
		
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
		
//		logger.info("Field type: " + idBinding.getFieldType().toString());
//		logger.info("Generic field type: " + idBinding.getGenericType().toString());
		
		if(!idBinding.isArray() && !idBinding.isCollection()){
			
			if(existingIds.size() > 1)
				throw new TopicMapInconsistentException("Cannot add multiple identifier to an non container field.");
			
			if(idBinding.getFieldType() == int.class){
				
				idBinding.setValue(Integer.parseInt(existingIds.iterator().next()), object);
				
			}else{
				
				idBinding.setValue(existingIds.iterator().next(), object);
			}
			
		}else{
			
			
			if(idBinding.isArray()){

				if(idBinding.getGenericType() == int.class){
					
					List<Integer> list = new ArrayList<Integer>();
					
					for(String id:existingIds)
						list.add(Integer.parseInt(id));
					
					idBinding.setValue(list.toArray(new Integer[list.size()]), object);
					
					
				}else{

					idBinding.setValue(existingIds.toArray(new String[existingIds.size()]), object);
				}
				
			}else{
				
				// set an collection
				if(idBinding.getGenericType() == int.class){
					
					Collection<Integer> collection;
					
					if(idBinding.getFieldType() == Set.class){ // is set
						
						collection = new HashSet<Integer>();
						
					}else{ // is list
						
						collection = new ArrayList<Integer>();
					}
					
					for(String id:existingIds)
						collection.add(Integer.parseInt(id));
					
					idBinding.setValue(collection, object);

				}else{
					
					Collection<String> collection;
					
					if(idBinding.getFieldType() == Set.class){ // is set
						
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
	
	// --[ private methods ]-------------------------------------------------------------------------------

	private void persistTopics(List<Object> topicObjects) throws IOException, BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException, TopicMapInconsistentException {
		
		Iterator<Object> itr = topicObjects.iterator();
		
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
		    	persistTopic(topicObject, topicObjects, binding);
		    	// remove from list
		    	itr.remove();
	    	
	    	}else{
	    		
	    		updateTopic(getTopicFromCache(topicObject), topicObject, binding);
	    	}
	    }
	}
	
	private Topic persistTopic(Object topicObject, List<Object> topicObjects, TopicBinding binding) throws IOException, BadAnnotationException, TopicMapInconsistentException, NoSuchMethodException, ClassNotSpecifiedException {
		
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
		updateAssociations(newTopic, topicObject, binding);
		
		return newTopic;
	}
	
	
	private Topic getTopicType(TopicBinding binding) throws IOException, BadAnnotationException{
		
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
	
	
	private void updateTopic(Topic topic, Object topicObject, TopicBinding binding) throws IOException, BadAnnotationException, TopicMapInconsistentException, NoSuchMethodException, ClassNotSpecifiedException{
		
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
		updateAssociations(topic, topicObject, binding);
	}
	
	
	// modifies the topic subject identifier to represent the current java object
	// used for create new topic as well
	private void updateSubjectIdentifier(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
		
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
	
	
	private void updateSubjectLocator(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
		
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
	

	private void updateItemIdentifier(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
	
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
	private void updateNames(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
		
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
	private void updateOccurrences(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
		
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
	private void updateAssociations(Topic topic, Object topicObject, TopicBinding binding) throws IOException, BadAnnotationException, TopicMapInconsistentException, NoSuchMethodException, ClassNotSpecifiedException{
		
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
				
				updateBinaryAssociations(topic, newAssociation.getKey(), newAssociation.getValue(), playedRoles);
				
			}else if(newAssociation.getKey().getKind() == AssociationKind.NNARY){
				
				updateNnaryAssociations(topic, newAssociation.getKey(), newAssociation.getValue(), playedRoles);
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
	
	
	private void updateUnaryAssociation(Topic topic, AssociationBinding binding, Set<Object> associationObjects, Map<Role,Match> playedRoles) throws IOException{
		
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
				//playedRole.setValue(Match.BINDING);
				logger.info("Unary association matches binding.");
				
				if(playedRole.getKey().getParent().getScope().equals(scope)){ // check scope
				
					//logger.info("Unary association matches instance.");
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
	
	
	private void updateBinaryAssociations(Topic topic, AssociationBinding binding, Set<Object> associationObjects, Map<Role, Match> playedRoles) throws IOException, BadAnnotationException, TopicMapInconsistentException {

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
						&& TopicMapsUtils.getCounterPlayer(playedRole.getKey().getParent(), playedRole.getKey()).getType().equals(otherRoleType)){ // check counter player role

					// binding found
					playedRole.setValue(Match.BINDING);
					//logger.info("Binary association matches binding.");
					
					if(TopicMapsUtils.getCounterPlayer(playedRole.getKey().getParent(), playedRole.getKey()).getPlayer().equals(counterPlayer)){ // check counter player
					
						//logger.info("Binary association matches instance.");
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
			}
		}
	}
	

	private void updateNnaryAssociations(Topic topic, AssociationBinding binding, Set<Object> associationObjects, Map<Role, Match> playedRoles) throws IOException, ClassNotSpecifiedException, BadAnnotationException, NoSuchMethodException{

		Topic associationType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getAssociationType()));
		Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getPlayedRole()));
		Set<Topic> scope = binding.getScope(getTopicMap());
		
		for (Object associationObject:associationObjects) { // check each nnary association
			
			boolean found = false;
			
			// get roleplayer for container
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
						//logger.info("Nnary association matches binding.");
						
						// check counter player
						if(matchCounterPlayer(playedRole.getKey(), rolePlayer)){
							
							//logger.info("Nnary Association matches instance.");
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
	
	
	@SuppressWarnings("unchecked")
	private Map<Topic,Set<Topic>> getRolesFromContainer(/*Role playedRole, */Object associationContainerInstance) throws ClassNotSpecifiedException, BadAnnotationException, NoSuchMethodException, IOException{
		
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
	
	
	// adds flags to an set, returns an empty map of the set was null
	private <T extends Object> Map<T, Match> addFlags(Set<T> set){
		
		Map<T, Match> map = new HashMap<T, Match>();
		
		if(set == null)
			return map;
		
		for(T obj:set)
			map.put(obj, Match.NO);

		return map;
	}
	
	
	private Topic createTopicByIdentifier(Object topicObject, TopicBinding binding) throws IOException, BadAnnotationException {
		
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
	private Topic getTopicBySubjectIdentifier(Set<String> subjectIdentifier)throws IOException{
		
		Topic topic = null;
		
		for(String si:subjectIdentifier){
			
			topic = getTopicMap().getTopicBySubjectIdentifier(getTopicMap().createLocator(si));
			
			if(topic != null)
				return topic;
		}
		
		return null;
	}
	
	
	// tries to find an existing topic by a list of subject locators
	private Topic getTopicBySubjectLocator(Set<String> subjectLocator)throws IOException{
		
		Topic topic = null;
		
		for(String sl:subjectLocator){
			
			topic = getTopicMap().getTopicBySubjectLocator(getTopicMap().createLocator(sl));
			
			if(topic != null)
				return topic;
		}
		
		return null;
	}
	
	
	// tries to find an existing topic by a list of item identifiers
	private Topic getTopicByItemIdentifier(Set<String> itemIdentifier)throws IOException{
		
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

							addValueToBindingMap(map, (OccurrenceBinding) afb, obj.toString());
						}

					} else if (((OccurrenceBinding) afb).isCollection()) {

						for (Object obj : (Collection<Object>) ((OccurrenceBinding) afb).getValue(topicObject)) {

							addValueToBindingMap(map, (OccurrenceBinding) afb, obj.toString());
						}

					} else {

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
	
	
	private Topic createNewTopic(Set<String> subjectIdentifier, Set<String> subjectLocator, Set<String> itemIdentifier) throws IOException, BadAnnotationException{
		
		Topic topic = null;
		
		if(!subjectIdentifier.isEmpty()){
			topic = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(subjectIdentifier.iterator().next()));
			
		}else if(!subjectLocator.isEmpty()){
			topic = getTopicMap().createTopicBySubjectLocator(getTopicMap().createLocator(subjectLocator.iterator().next()));
			
		}else if(!itemIdentifier.isEmpty()){
			topic = getTopicMap().createTopicByItemIdentifier(getTopicMap().createLocator(itemIdentifier.iterator().next()));
			
		}else{
			throw new BadAnnotationException("Topic class has no identifier");
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

	
	
}
