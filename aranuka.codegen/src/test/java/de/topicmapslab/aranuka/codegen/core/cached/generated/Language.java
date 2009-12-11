/**
* This file was auto-generated by orm4tmql.
*
* Generation-Time: 12/10/09 1:57 PM
*/

package de.topicmapslab.aranuka.codegen.core.cached.generated;

import de.topicmapslab.aranuka.codegen.properties.*;
import java.util.*;
import java.net.*;
import de.topicmapslab.aranuka.codegen.core.utility.*;
import gnu.trove.*;
import de.topicmapslab.aranuka.codegen.core.exception.*;
import annoTM.core.annotations.*;


@Topic(name="Language", si="http://en.wikipedia.org/wiki/Language")
public class Language {

private boolean modified = false;

private final org.tmapi.core.Topic topic;

public Language(org.tmapi.core.Topic topic){
	this.topic = topic;
}


/*
* field definition for subjectLocators
*/
@Id(type=IDTYPE.SUBJECT_LOCATOR)
private Set<String> subjectLocators ;

/*
* field definition for subjectIdentifiers
*/
@Id(type=IDTYPE.SUBJECT_IDENTIFIER)
private Set<String> subjectIdentifiers ;

/*
* field definition for itemIdentifiers
*/
@Id(type=IDTYPE.ITEM_IDENTIFIER)
private Set<String> itemIdentifiers ;

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

}

