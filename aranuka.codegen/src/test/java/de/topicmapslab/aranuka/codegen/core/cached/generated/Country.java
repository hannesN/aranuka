/**
* This file was auto-generated by orm4tmql.
*
* Generation-Time: 12/4/09 1:18 PM
*/

package de.topicmapslab.aranuka.codegen.core.cached.generated;

import de.topicmapslab.aranuka.codegen.properties.*;
import java.util.*;
import java.net.*;
import de.topicmapslab.aranuka.codegen.core.utility.*;
import gnu.trove.*;
import de.topicmapslab.aranuka.codegen.core.exception.*;
import annoTM.core.annotations.*;


@Topic(name="Country", si="http://en.wikipedia.org/wiki/Country")
public class Country {

private boolean modified = false;

private final org.tmapi.core.Topic topic;

public Country(org.tmapi.core.Topic topic){
	this.topic = topic;
}


/*
* field definition for subjectIdentifiers
*/
@Id(type=IDTYPE.SUBJECT_IDENTIFIER)
private Set<String> subjectIdentifiers ;

/*
* field definition for subjectLocators
*/
@Id(type=IDTYPE.SUBJECT_LOCATOR)
private Set<String> subjectLocators ;

/*
* field definition for itemIdentifiers
*/
@Id(type=IDTYPE.ITEM_IDENTIFIER)
private Set<String> itemIdentifiers ;

/*
* field definition for topicname
*/
@Name(type="tm:name")
private String topicname ;

/*
* field definition for website
*/
@Occurrence(type="Website")
private String website ;

/*
* field definition for population
*/
@Occurrence(type="Population")
private String population ;

/*
* field definition for city
*/
@Association(container_role="http://en.wikipedia.org/wiki/Country", role="http://en.wikipedia.org/wiki/Capital", type="http://en.wikipedia.org/wiki/Country-Capital")
private Set<City> city ;

/*
* field definition for country
*/
@Association(container_role="http://en.wikipedia.org/wiki/Country", role="http://en.wikipedia.org/wiki/Neighbouring_Country", type="http://en.wikipedia.org/wiki/Neighbouring_Countries")
private Set<Country> country ;

/*
* field definition for republic
*/
@Association(container_role="http://en.wikipedia.org/wiki/Country", role="http://en.wikipedia.org/wiki/Neighbouring_Country", type="http://en.wikipedia.org/wiki/Neighbouring_Countries")
private Set<Republic> republic ;

/*
* field definition for grandduchy
*/
@Association(container_role="http://en.wikipedia.org/wiki/Country", role="http://en.wikipedia.org/wiki/Neighbouring_Country", type="http://en.wikipedia.org/wiki/Neighbouring_Countries")
private Set<GrandDuchy> grandduchy ;

/**
* get()-method for subjectIdentifiers
*/
public Set<String> getSubjectIdentifiers() throws TopicMap2JavaMapperException{
	if ( this.subjectIdentifiers == null ){
		this.subjectIdentifiers = (Set<String>) PropertyLoader.getInstance().getDataBridge().getIdentifiers(this.topic,IDTYPE.SUBJECT_IDENTIFIER );
	}
	return this.subjectIdentifiers;
}

/**
* add()-method for subjectIdentifiers
*/
public void addSubjectIdentifiers(final Set<String> subjectIdentifiers)  throws TopicMap2JavaMapperException{
	if ( this.subjectIdentifiers == null ){
		getSubjectIdentifiers();
	}
	this.subjectIdentifiers.addAll(subjectIdentifiers);
	this.modified = true;
}

/**
* get()-method for subjectLocators
*/
public Set<String> getSubjectLocators() throws TopicMap2JavaMapperException{
	if ( this.subjectLocators == null ){
		this.subjectLocators = (Set<String>) PropertyLoader.getInstance().getDataBridge().getIdentifiers(this.topic,IDTYPE.SUBJECT_LOCATOR );
	}
	return this.subjectLocators;
}

/**
* add()-method for subjectLocators
*/
public void addSubjectLocators(final Set<String> subjectLocators)  throws TopicMap2JavaMapperException{
	if ( this.subjectLocators == null ){
		getSubjectLocators();
	}
	this.subjectLocators.addAll(subjectLocators);
	this.modified = true;
}

/**
* get()-method for itemIdentifiers
*/
public Set<String> getItemIdentifiers() throws TopicMap2JavaMapperException{
	if ( this.itemIdentifiers == null ){
		this.itemIdentifiers = (Set<String>) PropertyLoader.getInstance().getDataBridge().getIdentifiers(this.topic,IDTYPE.ITEM_IDENTIFIER );
	}
	return this.itemIdentifiers;
}

/**
* add()-method for itemIdentifiers
*/
public void addItemIdentifiers(final Set<String> itemIdentifiers)  throws TopicMap2JavaMapperException{
	if ( this.itemIdentifiers == null ){
		getItemIdentifiers();
	}
	this.itemIdentifiers.addAll(itemIdentifiers);
	this.modified = true;
}

/**
* get()-method for topicname
*/
public String getTopicname() throws TopicMap2JavaMapperException{
	if ( this.topicname == null ){
		this.topicname = (String) PropertyLoader.getInstance().getDataBridge().getField(this.topic,"tm:name" );
	}
	return this.topicname;
}

/**
* set()-method for topicname
*/
public void setTopicname(final String topicname) throws TopicMap2JavaMapperException {
	if ( this.topicname == null ){
		getTopicname();
	}
	this.topicname = topicname;
	modified = true;
}

/**
* get()-method for website
*/
public String getWebsite() throws TopicMap2JavaMapperException{
	if ( this.website == null ){
		this.website = (String) PropertyLoader.getInstance().getDataBridge().getField(this.topic,"Website" );
	}
	return this.website;
}

/**
* set()-method for website
*/
public void setWebsite(final String website) throws TopicMap2JavaMapperException {
	if ( this.website == null ){
		getWebsite();
	}
	this.website = website;
	modified = true;
}

/**
* get()-method for population
*/
public String getPopulation() throws TopicMap2JavaMapperException{
	if ( this.population == null ){
		this.population = (String) PropertyLoader.getInstance().getDataBridge().getField(this.topic,"Population" );
	}
	return this.population;
}

/**
* set()-method for population
*/
public void setPopulation(final String population) throws TopicMap2JavaMapperException {
	if ( this.population == null ){
		getPopulation();
	}
	this.population = population;
	modified = true;
}

/**
* get()-method for city
*/
public Set<City> getCity() throws TopicMap2JavaMapperException{
	if ( this.city == null ){
		this.city = (Set<City>) PropertyLoader.getInstance().getDataBridge().getTraversalPlayers(this.topic,City.class ,"http://en.wikipedia.org/wiki/Capital" );
	}
	return this.city;
}

/**
* add()-method for city
*/
public void addCity(final Set<City> city)  throws TopicMap2JavaMapperException{
	if ( this.city == null ){
		getCity();
	}
	this.city.addAll(city);
	this.modified = true;
}

/**
* get()-method for country
*/
public Set<Country> getCountry() throws TopicMap2JavaMapperException{
	if ( this.country == null ){
		this.country = (Set<Country>) PropertyLoader.getInstance().getDataBridge().getTraversalPlayers(this.topic,Country.class ,"http://en.wikipedia.org/wiki/Neighbouring_Country" );
	}
	return this.country;
}

/**
* add()-method for country
*/
public void addCountry(final Set<Country> country)  throws TopicMap2JavaMapperException{
	if ( this.country == null ){
		getCountry();
	}
	this.country.addAll(country);
	this.modified = true;
}

/**
* get()-method for republic
*/
public Set<Republic> getRepublic() throws TopicMap2JavaMapperException{
	if ( this.republic == null ){
		this.republic = (Set<Republic>) PropertyLoader.getInstance().getDataBridge().getTraversalPlayers(this.topic,Republic.class ,"http://en.wikipedia.org/wiki/Neighbouring_Country" );
	}
	return this.republic;
}

/**
* add()-method for republic
*/
public void addRepublic(final Set<Republic> republic)  throws TopicMap2JavaMapperException{
	if ( this.republic == null ){
		getRepublic();
	}
	this.republic.addAll(republic);
	this.modified = true;
}

/**
* get()-method for grandduchy
*/
public Set<GrandDuchy> getGrandduchy() throws TopicMap2JavaMapperException{
	if ( this.grandduchy == null ){
		this.grandduchy = (Set<GrandDuchy>) PropertyLoader.getInstance().getDataBridge().getTraversalPlayers(this.topic,GrandDuchy.class ,"http://en.wikipedia.org/wiki/Neighbouring_Country" );
	}
	return this.grandduchy;
}

/**
* add()-method for grandduchy
*/
public void addGrandduchy(final Set<GrandDuchy> grandduchy)  throws TopicMap2JavaMapperException{
	if ( this.grandduchy == null ){
		getGrandduchy();
	}
	this.grandduchy.addAll(grandduchy);
	this.modified = true;
}

}

