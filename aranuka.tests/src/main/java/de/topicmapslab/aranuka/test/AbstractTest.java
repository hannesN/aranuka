package de.topicmapslab.aranuka.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.ctm.writer.core.CTMTopicMapWriter;

public abstract class AbstractTest {

	private static Properties properties;
	
	public static void setProperties(Properties properties) {
		AbstractTest.properties = properties;
	}
	
	public static Properties getProperties() {
		if (properties==null)
			throw new RuntimeException("No properties set!");
		return properties;
	}
	
	public static boolean verifyTopicExist(TopicMap map, String subjectIdentifier) {

		Topic topic = map.getTopicBySubjectIdentifier(map.createLocator(subjectIdentifier));

		if (topic != null)
			return true;

		return false;

	}

	public static int numberOfSubjectIdentifiers(TopicMap map,
			String subjectIdentifier) {

		Topic topic = map.getTopicBySubjectIdentifier(map
				.createLocator(subjectIdentifier));

		if (topic == null)
			return 0;

		return topic.getSubjectIdentifiers().size();

	}

	public static int numberOfSubjectLocators(TopicMap map,
			String subjectIdentifier) {

		Topic topic = map.getTopicBySubjectIdentifier(map
				.createLocator(subjectIdentifier));

		if (topic == null)
			return 0;

		return topic.getSubjectLocators().size();

	}

	public static int numberOfItemIdentifiers(TopicMap map,
			String subjectIdentifier) {

		Topic topic = map.getTopicBySubjectIdentifier(map.createLocator(subjectIdentifier));

		if (topic == null)
			return 0;

		return topic.getItemIdentifiers().size();

	}

	public static int numberOfTopicNames(TopicMap map, String subjectIdentifier, String nameType) {

		Topic topic = map.getTopicBySubjectIdentifier(map.createLocator(subjectIdentifier));

		if (topic == null)
			return 0;

		Topic type = map.getTopicBySubjectIdentifier(map
				.createLocator(nameType));

		if (nameType == null)
			return 0;

		return topic.getNames(type).size();

	}
	
	public static Set<String> getNameValues(TopicMap map, String subjectIdentifier, String nameType){
		
		Set<String> set = new HashSet<String>();
		
		Topic topic = map.getTopicBySubjectIdentifier(map.createLocator(subjectIdentifier));

		if (topic == null)
			return set;

		Topic type = map.getTopicBySubjectIdentifier(map
				.createLocator(nameType));

		if (nameType == null)
			return set;

		Set<Name> names = topic.getNames(type);
		
		for(Name name:names)
			set.add(name.getValue());
		
		return set;
		
	}

	public static boolean verifyNameScope(TopicMap map, String subjectIdentifier, String nameType, String[] themes){
		
		Topic topic = map.getTopicBySubjectIdentifier(map.createLocator(subjectIdentifier));

		if (topic == null)
			return false;

		Topic type = map.getTopicBySubjectIdentifier(map
				.createLocator(nameType));

		if (nameType == null)
			return false;

		Set<Name> names = topic.getNames(type);
		
		if(names.isEmpty())
			return false;
		
		return verifyScope(map, names, themes);
	}
	
	public static boolean verifyOccurrenceScope(TopicMap map, String subjectIdentifier, String occurrenceType, String[] themes){
		
		Topic topic = map.getTopicBySubjectIdentifier(map.createLocator(subjectIdentifier));

		if (topic == null)
			return false;

		Topic type = map.getTopicBySubjectIdentifier(map.createLocator(occurrenceType));

		if (occurrenceType == null)
			return false;

		Set<Occurrence> occurrences = topic.getOccurrences(type);
		
		if(occurrences.isEmpty())
			return false;
		
		return verifyScope(map, occurrences, themes);
	}
	
	private static <T extends Scoped> boolean verifyScope(TopicMap map, Set<T> scopedObjects, String[] themes){
		
		Set<Topic> expectedScope = new HashSet<Topic>();
		
		for(String theme:themes){
			
			Topic t = map.getTopicBySubjectIdentifier(map.createLocator(theme));
			if(t == null)
				return false;
			
			expectedScope.add(t);
		}
		
		for(Scoped scopedObject:scopedObjects){
			
			Set<Topic> scope = scopedObject.getScope();
			
			if(!scope.equals(expectedScope))
				return false;
		}	
		
		return true;
	}
	
	private static Set<Occurrence> getOccurrences(TopicMap map, String subjectIdentifier, String occurrenceType){
		
		Topic topic = map.getTopicBySubjectIdentifier(map.createLocator(subjectIdentifier));

		if (topic == null)
			return null;

		Topic type = map.getTopicBySubjectIdentifier(map.createLocator(occurrenceType));

		if (type == null)
			return null;
		
		return topic.getOccurrences(type);
		
		
	}
	
	public static int getNumberOfOccurrences(TopicMap map, String subjectIdentifier, String occurrenceType){
		
		Set<Occurrence> occurrences = getOccurrences(map, subjectIdentifier, occurrenceType);
		
		if(occurrences == null)
			return 0;
		
		return occurrences.size();
	}
	
	public static Set<String> getOccurrenceValues(TopicMap map, String subjectIdentifier, String occurrenceType){
		
		Set<String> result = new HashSet<String>();
		
		Set<Occurrence> occurrences = getOccurrences(map, subjectIdentifier, occurrenceType);
		
		if(occurrences == null)
			return result;
		
		for(Occurrence occ:occurrences)
			result.add(occ.getValue());
		
		return result;
		
	}
	
	public static boolean verifyOccurrence(TopicMap map, String subjectIdentifier, String occurrenceType, boolean value){
		
		Set<Occurrence> occurrences = getOccurrences(map, subjectIdentifier, occurrenceType);
		
		if(occurrences == null)
			return false;
		
		if(occurrences.size() != 1)
			return false;
		
		if(Boolean.parseBoolean(occurrences.iterator().next().getValue()) == value)
			return true;
		
		return false;
	}
	
	public static boolean verifyOccurrence(TopicMap map, String subjectIdentifier, String occurrenceType, int value){
		
		Set<Occurrence> occurrences = getOccurrences(map, subjectIdentifier, occurrenceType);
		
		if(occurrences == null)
			return false;
		
		if(occurrences.size() != 1)
			return false;
		
		if(Integer.parseInt(occurrences.iterator().next().getValue()) == value)
			return true;
		
		return false;
	}
	
	public static boolean verifyOccurrence(TopicMap map, String subjectIdentifier, String occurrenceType, float value){
		
		Set<Occurrence> occurrences = getOccurrences(map, subjectIdentifier, occurrenceType);
		
		if(occurrences == null)
			return false;
		
		if(occurrences.size() != 1)
			return false;
		
		if(Float.parseFloat(occurrences.iterator().next().getValue()) == value)
			return true;
		
		return false;
	}
	
	public static boolean verifyOccurrence(TopicMap map, String subjectIdentifier, String occurrenceType, double value){
		
		Set<Occurrence> occurrences = getOccurrences(map, subjectIdentifier, occurrenceType);
		
		if(occurrences == null)
			return false;
		
		if(occurrences.size() != 1)
			return false;
		
		if(Double.parseDouble(occurrences.iterator().next().getValue()) == value)
			return true;
		
		return false;
	}
	
	public static boolean verifyOccurrence(TopicMap map, String subjectIdentifier, String occurrenceType, Date value){
		
		Set<Occurrence> occurrences = getOccurrences(map, subjectIdentifier, occurrenceType);
		
		if(occurrences == null)
			return false;
		
		if(occurrences.size() != 1)
			return false;
		
		Object date;
		
		try{
			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(occurrences.iterator().next().getValue());
			
		}catch(ParseException e){
			return false;
		}
		
		if(date.equals(value))
			return true;
		
		return false;
	}
	
	public static boolean verifyOccurrence(TopicMap map, String subjectIdentifier, String occurrenceType, String value){
		
		Set<Occurrence> occurrences = getOccurrences(map, subjectIdentifier, occurrenceType);
		
		if(occurrences == null)
			return false;
		
		if(occurrences.size() != 1)
			return false;
		
		if(occurrences.iterator().next().getValue().equals(value))
			return true;
		
		return false;
	}
	
	public static boolean verifyOccurrence(TopicMap map, String subjectIdentifier, String occurrenceType, Set<String> value){
		
		Set<Occurrence> occurrences = getOccurrences(map, subjectIdentifier, occurrenceType);
		
		if(occurrences == null)
			return false;
		
		if(occurrences.size() != value.size())
			return false;
		
		for(Occurrence occ:occurrences)
			if(!value.contains(occ.getValue()))
				return false;
		
		return true;
	}
	
	public static boolean verifyOccurrence(TopicMap map, String subjectIdentifier, String occurrenceType, String[] value){
		
		Set<Occurrence> occurrences = getOccurrences(map, subjectIdentifier, occurrenceType);
		
		if(occurrences == null)
			return false;
		
		if(occurrences.size() != value.length)
			return false;
		
		for(Occurrence occ:occurrences){
			boolean found = false;
			for(String s:value)
				if(s.equals(occ.getValue()))
					found = true;
			
			if(!found)
				return false;
		}
		
		return true;
	}
	
	// use only to check single association fields
	// TODO check as well counter player type
	public static boolean verifyAssociation(TopicMap map, String subjectIdentifier, String associationType, String ownRoleType, Set<String> counterRoleTypes, int numberOfCounterPlayer){
		
		// check if topic exist
		Topic topic = map.getTopicBySubjectIdentifier(map.createLocator(subjectIdentifier));

		if (topic == null)
			return false;

		// check if association type exist
		Topic assocType = map.getTopicBySubjectIdentifier(map.createLocator(associationType));

		if (assocType == null)
			return false;
		
		// check if counter role types exist
		Set<Topic> roleTypes = getRoleTypes(map, counterRoleTypes);
		
		if(counterRoleTypes != null)
			if(counterRoleTypes.size() != roleTypes.size())
				return false;
		
		// check if own role type exist
		Topic ownRole = map.getTopicBySubjectIdentifier(map.createLocator(ownRoleType));
		
		if(ownRole == null)
			return false;
		
		// check each association
		Set<Role> rolesPlayed = topic.getRolesPlayed(ownRole);
		
		for(Role role:rolesPlayed){
			
			if(!role.getParent().getType().equals(assocType))
				return false;
			
			int count = 0;
			
			for(Role counterRole:role.getParent().getRoles()){
				
				if(!counterRole.equals(role)){
					
					count++;
					if(!roleTypes.contains(counterRole.getType()))
						return false;
				}
			}
			
			if(count != numberOfCounterPlayer)
				return false;
		}
		
		return true;
	}
	
	private static Set<Topic> getRoleTypes(TopicMap map, Set<String> roleTypes){
		
		Set<Topic> result = new HashSet<Topic>();
		
		if(roleTypes == null)
			return result;

		for(String roleType:roleTypes){
			
			Topic type = map.getTopicBySubjectIdentifier(map.createLocator(roleType));
			
			if(type != null)
				result.add(type);
		}
				
		return result;
	}
	
	// use only for single associations
	public static boolean associationExist(TopicMap map, String subjectIdentifier, String associationTypeIdentifier, String ownRoleTypeIdentifier, Map<String,Set<String>> counterPlayerIdentifier){
		
		// check if topic exist
		Topic topic = map.getTopicBySubjectIdentifier(map.createLocator(subjectIdentifier));

		if (topic == null)
			return false;

		// check if association type exist
		Topic associationType = map.getTopicBySubjectIdentifier(map.createLocator(associationTypeIdentifier));

		if (associationType == null)
			return false;
		
		// check if own role type exist
		Topic ownRoleType = map.getTopicBySubjectIdentifier(map.createLocator(ownRoleTypeIdentifier));
		
		if(ownRoleType == null)
			return false;
		
		// get counter player
		Map<Topic,Set<Topic>> counterPlayer = getCounterPlayer(map, counterPlayerIdentifier);
		
		if(counterPlayer == null)
			return false;
		
		if(counterPlayer.isEmpty() && topic.getRolesPlayed(ownRoleType).size() == 1){
			
			return true;
		}
			
		
		// check all associations
		
		boolean found = false;
		
		for(Role topicRole:topic.getRolesPlayed(ownRoleType)){
			
			if(topicRole.getParent().getType().equals(associationType)){

				for(Map.Entry<Topic, Set<Topic>>entry:counterPlayer.entrySet()){
					
					Set<Role> matchingRoles = topicRole.getParent().getRoles(entry.getKey());
					
					if(matchingRoles.size() == entry.getValue().size()){

						// check of each player matches
						boolean counterPlayerMatch = true;
						
						for(Role matchingRole:matchingRoles){
							
							if(!entry.getValue().contains(matchingRole.getPlayer()))
								counterPlayerMatch = false;
						}
						
						if(counterPlayerMatch)
							found = true;
					}
				}
			}
		}
				
		return found;
	}
	
	private static Map<Topic,Set<Topic>> getCounterPlayer(TopicMap map, Map<String,Set<String>> counterPlayerIdentifier){
		
		Map<Topic,Set<Topic>> result = new HashMap<Topic, Set<Topic>>();
		
		for(Map.Entry<String, Set<String>>entry:counterPlayerIdentifier.entrySet()){
			
			//  get role type
			Topic roleType = map.getTopicBySubjectIdentifier(map.createLocator(entry.getKey()));
			
			if(roleType == null)
				return null;
			
			Set<Topic> players = new HashSet<Topic>();
			
			for(String si:entry.getValue()){
				
				Topic player = map.getTopicBySubjectIdentifier(map.createLocator(si));
				if(player == null)
					return null;
				
				players.add(player);
			}
			
			result.put(roleType, players);
		}
		
		return result;
	}
	
	
	/**
	 * Serializes the topic map as ctm file
	 * @param file the absolute filename
	 * @param topicMap the topic map
	 * @throws IOException 
	 */
	public static void serializeTopicMap(String file, TopicMap topicMap) throws IOException  {
		
		CTMTopicMapWriter writer = new CTMTopicMapWriter(new FileOutputStream(
				new File(file)), "http://aranukatests.de/");
		writer.write(topicMap);
		
	}
}
