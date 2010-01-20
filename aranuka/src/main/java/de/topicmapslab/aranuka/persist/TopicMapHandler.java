package de.topicmapslab.aranuka.persist;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
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
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.binding.AbstractClassBinding;
import de.topicmapslab.aranuka.binding.AbstractFieldBinding;
import de.topicmapslab.aranuka.binding.AssociationBinding;
import de.topicmapslab.aranuka.binding.IdBinding;
import de.topicmapslab.aranuka.binding.NameBinding;
import de.topicmapslab.aranuka.binding.OccurrenceBinding;
import de.topicmapslab.aranuka.binding.TopicBinding;
import de.topicmapslab.aranuka.enummerations.AssociationKind;
import de.topicmapslab.aranuka.enummerations.IdType;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.utils.TopicMapsUtils;

// handles interaction with the topic map, i.e. creating topics and associations, updating, etc.
public class TopicMapHandler {

	private static Logger logger = LoggerFactory.getLogger(TopicMapHandler.class);
	
	private Configuration config; // the configuration
	private BindingHandler bindingHandler; // the binding handler
	private TopicMap topicMap; // the topic map
	
	private Map<Object, Topic> topicCache; /// TODO fix cache
	
	// --[ public methods ]------------------------------------------------------------------------------
	
	public TopicMapHandler(Configuration config){

		this.config = config;
	}
	
	public void invokeBinding()  throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		getBindingHandler().createBindingsForAllClasses();
		
//		// test
//		getBindingHandler().printBindings();
	}
	
	/// TODO persist only topics or association container as well?
	public void persist(Object topicObject) throws IOException, BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
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
	
	// --[ private methods ]-------------------------------------------------------------------------------

	private void persistTopics(List<Object> topicObjects) throws IOException, BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException {
		
		Iterator<Object> itr = topicObjects.iterator();
		
	    while(itr.hasNext()){
	    	
	    	Object topicObject = itr.next();
	    	
	    	// get binding
	    	TopicBinding binding = (TopicBinding)getBindingHandler().getBinding(topicObject.getClass());
	    	
	    	if(getTopicFromCache(topicObjects) == null)
	    	{
		    	// check
		    	if(!this.config.getClasses().contains(topicObject.getClass()))
		    		throw new ClassNotSpecifiedException("The class " + topicObject.getClass().getName() + " is not registered.");
		    	
		    	// create topic
		    	persistTopic(topicObject, topicObjects, binding);
		    	// remove from list
		    	itr.remove();
	    	
	    	}else{
	    		
	    		updateTopic(getTopicFromCache(topicObjects), topicObject, binding);
	    	}
	    }
	}
	
	private Topic persistTopic(Object topicObject, List<Object> topicObjects, TopicBinding binding) throws IOException, BadAnnotationException {
		
		logger.info("Create new topic for " + topicObject);
		
		Topic newTopic = createTopicByIdentifier(topicObject, binding);

		// add topic to cache
		addTopicToCache(newTopic, topicObject);
		
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
	
	private void updateTopic(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
		
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
		
		Map<Locator,Boolean> actualSubjectIdentifier = addFlaggs(topic.getSubjectIdentifiers());
		
		for(String si:newSubjectIdentifier){
			
			boolean found = false;
			
			for(Map.Entry<Locator, Boolean> entry:actualSubjectIdentifier.entrySet()){
	
				if(entry.getKey().toExternalForm() == si){
					entry.setValue(true); // set to found
					found = true;
					break;
				}
			}
			
			if(!found){
				// add new identifier
				topic.addSubjectIdentifier(getTopicMap().createLocator(si));
			}
			
		}
		
		// remove obsolete identifier
		for(Map.Entry<Locator, Boolean> entry:actualSubjectIdentifier.entrySet()){
			
			if(!entry.getValue())
				topic.removeSubjectIdentifier(entry.getKey());
		}
	}
	
	private void updateSubjectLocator(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
		
		Set<String> newSubjectLocator = getIdentifier(topicObject, binding, IdType.SUBJECT_LOCATOR);
		
		Map<Locator,Boolean> actualSubjectLocator = addFlaggs(topic.getSubjectLocators());
		
		for(String sl:newSubjectLocator){
			
			boolean found = false;
			
			for(Map.Entry<Locator, Boolean> entry:actualSubjectLocator.entrySet()){
	
				if(entry.getKey().toExternalForm() == sl){
					entry.setValue(true); // set to found
					found = true;
					break;
				}
			}
			
			if(!found){
				// add new identifier
				topic.addSubjectLocator((getTopicMap().createLocator(sl)));
			}
			
		}
		
		// remove obsolete identifier
		for(Map.Entry<Locator, Boolean> entry:actualSubjectLocator.entrySet()){
			
			if(!entry.getValue())
				topic.removeSubjectLocator(entry.getKey());
		}
	}

	private void updateItemIdentifier(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
	
		Set<String> newItemIdentifier = getIdentifier(topicObject, binding, IdType.ITEM_IDENTIFIER);
		
		Map<Locator,Boolean> actualItemIdentifier = addFlaggs(topic.getItemIdentifiers());
		
		for(String ii:newItemIdentifier){
			
			boolean found = false;
			
			for(Map.Entry<Locator, Boolean> entry:actualItemIdentifier.entrySet()){
	
				if(entry.getKey().toExternalForm() == ii){
					entry.setValue(true); // set to found
					found = true;
					break;
				}
			}
			
			if(!found){
				// add new identifier
				topic.addItemIdentifier(getTopicMap().createLocator(ii));
			}
			
		}
		
		// remove obsolete identifier
		for(Map.Entry<Locator, Boolean> entry:actualItemIdentifier.entrySet()){
			
			if(!entry.getValue())
				topic.removeItemIdentifier(entry.getKey());
		}
	}
	
	// modifies the topic names to represent the current java object
	// used for create new topic as well
	private void updateNames(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
		
		// get new names
		Map<NameBinding,Set<String>> newNames = getNames(topicObject, binding);
		
		// get actual names
		Map<Name,Boolean> actualNames = addFlaggs(topic.getNames());
		
		// update
		for(Map.Entry<NameBinding, Set<String>> newName:newNames.entrySet()){
			
			// get type and scope for this binding/field
			Topic nameType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(newName.getKey().getNameType()));
			Set<Topic> scope = newName.getKey().getScope(getTopicMap());
			
			
			for(String name:newName.getValue()){
				
				boolean found = false;
				
				for (Map.Entry<Name, Boolean> actualName : actualNames
						.entrySet()) {

					if (actualName.getKey().getValue().equals(name)) { // compare value

						if (actualName.getKey().getType().equals(nameType)) { // compare type

							if (scope.isEmpty() || actualName.getKey().getScope().equals(scope)) { // compare scope
	
								found = true;
								actualName.setValue(true);
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
		for(Map.Entry<Name, Boolean> entry:actualNames.entrySet()){

			if(!entry.getValue()){
				logger.info("Remove obsolete name " + entry.getKey().getValue());
				entry.getKey().remove();
			}
		}
		
	}
	
	// modifies the topic occurrences to represent the current java object
	// used for create new topic as well
	private void updateOccurrences(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
		
		// get new names
		Map<OccurrenceBinding,Set<String>> newOccurrences = getOccurrences(topicObject, binding);
		
		// get actual names
		Map<Occurrence,Boolean> actualOccurrences = addFlaggs(topic.getOccurrences());
		
		// update
		for(Map.Entry<OccurrenceBinding, Set<String>> newOccurrence:newOccurrences.entrySet()){
			
			// get type and scope for this binding/field
			Topic occurrenceType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(newOccurrence.getKey().getOccurrenceType()));
			Set<Topic> scope = newOccurrence.getKey().getScope(getTopicMap());

			for(String value:newOccurrence.getValue()){
				
				boolean found = false;
				
				for (Map.Entry<Occurrence, Boolean> actualOccurrence : actualOccurrences.entrySet()) {

					if (actualOccurrence.getKey().getValue().equals(value)) { // compare value

						if (actualOccurrence.getKey().getType().equals(occurrenceType)) { // compare type

							if (scope.isEmpty() || actualOccurrence.getKey().getScope().equals(scope)) { // compare scope
	
								found = true;
								actualOccurrence.setValue(true);
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
		
		// remove obsolete names
		for(Map.Entry<Occurrence, Boolean> entry:actualOccurrences.entrySet()){

			if(!entry.getValue()){
				logger.info("Remove obsolete occurrence " + entry.getKey().getValue());
				entry.getKey().remove();
			}
		}
		
	}
	
	// modifies the topic associations to represent the current java object
	// used for create new topic as well
	private void updateAssociations(Topic topic, Object topicObject, TopicBinding binding) throws IOException{
		
		// get new associations
		Map<AssociationBinding, Set<Object>> newAssociations = getAssociations(topicObject, binding);
		
		// get existing associartions, i.e. played roles
		Map<Role,Boolean> playedRoles = addFlaggs(topic.getRolesPlayed());

		for(Map.Entry<AssociationBinding, Set<Object>> newAssociation:newAssociations.entrySet()){

			boolean found = false;
			
			// each played role represents one association
			for(Map.Entry<Role, Boolean> playedRole:playedRoles.entrySet()){
				
				if(isAssociation(playedRole.getKey(), newAssociation.getKey(), newAssociation.getValue())){
					found = true;
					playedRole.setValue(true);
					break;
				}
			}
			
			if(!found){
				
				// create new association
				logger.info("Add new association of type " + newAssociation.getKey().getAssociationType());
				createAssociation(topic, newAssociation.getKey(), newAssociation.getValue());
			}
			
		}
		
		// remove obsolete associations
		// remove the complete association
		for(Map.Entry<Role, Boolean> entry:playedRoles.entrySet()){

			if(!entry.getValue()){
				logger.info("Remove obsolete association " + entry.getKey().getParent());
				entry.getKey().getParent().remove();
			}
		}
		
	}
	
	private boolean isAssociation(Role playedRole, AssociationBinding binding, Set<Object> associationObject) throws IOException{
		
		/// TODO do some magic here
		
		// get type and scope for this binding/field
		Topic associationType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getAssociationType()));
		Topic roleType = getTopicMap().createTopicBySubjectIdentifier(getTopicMap().createLocator(binding.getPlayedRole()));
		Set<Topic> scope = binding.getScope(getTopicMap());
		
		// first do some general checks
		
		// check own role type
		if(!playedRole.getType().equals(roleType))
			return false;
		
		// check association type
		if(!playedRole.getParent().getType().equals(associationType))
			return false;
		
		// check scope
		if(!playedRole.getParent().getScope().equals(scope))
			return false;
				
		if(binding.getKind() == AssociationKind.UNARY){
			
			if(playedRole.getParent().getRoles().size() != 1)
				return false;
			
			else return true;
			
			
		}else if(binding.getKind() == AssociationKind.BINARY){
			
			/// TODO
			
		}else if(binding.getKind() == AssociationKind.NNARY){
			
			/// TODO
			
		}
		
		return false;
	}
	
	
	
	private void createAssociation(Topic topic, AssociationBinding binding, Set<Object> associationObject){
		
	}

	
	// adds flaggs to an set, returns an empty map of the set was null
	private <T extends Object> Map<T, Boolean> addFlaggs(Set<T> set){
		
		Map<T, Boolean> map = new HashMap<T, Boolean>();
		
		if(set == null)
			return map;
		
		for(T obj:set)
			map.put(obj, false);

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
		String baseLocator = TopicMapsUtils.resolveURI("base_locator:", this.config.getPrefixMap()) + topicObject.getClass().getName().replaceAll("\\.", "/") + "/";
		
		// add all subject identifier
		
		for(AbstractFieldBinding afb:binding.getFieldBindings()){
			
			if(afb instanceof IdBinding && ((IdBinding)afb).getIdtype() == type){
				
				if(afb.getValue(topicObject) != null){ // ignore empty values

					if(((IdBinding)afb).isArray()){
					
						for (Object obj:(Object[])((IdBinding)afb).getValue(topicObject)){
							
							if (obj instanceof String){
								identifier.add(obj.toString());
							}else{
								identifier.add(baseLocator + obj.toString());
							}
						}
					
					}else if(((IdBinding)afb).isCollection()){
						
						for (Object obj:(Collection<Object>)((IdBinding)afb).getValue(topicObject)){
							
							if (obj instanceof String){
								identifier.add(obj.toString());
							}else{
								identifier.add(baseLocator + obj.toString());
							}
						}
		
					}else{
						if (((IdBinding)afb).getValue(topicObject) instanceof String){
							identifier.add(((IdBinding)afb).getValue(topicObject).toString());
						}else{
							identifier.add(baseLocator + ((IdBinding)afb).getValue(topicObject).toString());
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
