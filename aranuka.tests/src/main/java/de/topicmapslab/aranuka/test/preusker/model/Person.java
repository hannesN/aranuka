
package de.topicmapslab.aranuka.test.preusker.model;

import java.util.HashSet;
import java.util.Set;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = {
    "http://de.wikipedia.org/wiki/Person"
})
public class Person
    extends Entity
{

    @Id(type = IdType.SUBJECT_IDENTIFIER)
    private Set<String> subjectIdentifierSet;
    @Name(type = "http://de.wikipedia.org/wiki/Personenname")
    private String personenname;
    @de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Url", datatype = "http://www.w3.org/2001/XMLSchema#anyURI")
    private String wikipediaUrl;
    @de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Personennamendatei", datatype = "http://www.w3.org/2001/XMLSchema#string")
    private String pndId;

    public Set<String> getSubjectIdentifierSet() {
    	return this.subjectIdentifierSet;
    }

    public void setSubjectIdentifierSet(Set<String> subjectIdentifierSet) {
        this.subjectIdentifierSet = subjectIdentifierSet;
    }

    public void addSubjectIdentifier(String subjectIdentifier) {
        if (this.subjectIdentifierSet == null) {
            this.subjectIdentifierSet = new HashSet<String>();
        }
        this.subjectIdentifierSet.add(subjectIdentifier);
    }

    public void removeSubjectIdentifier(String subjectIdentifier) {
        if (this.subjectIdentifierSet!= null) {
            this.subjectIdentifierSet.remove(subjectIdentifier);
        }
    }

    public String getPersonenname() {
        return this.personenname;
    }

    @Override
    public String getName() {
    	return this.personenname;
    }
    
    public void setPersonenname(String personenname) {
        this.personenname = personenname;
        updateSubjectIdentifiers();
    }

    public String getWikipediaUrl() {
        return this.wikipediaUrl;
    }

    public void setWikipediaUrl(String wikipediaUrl) {
        this.wikipediaUrl = wikipediaUrl;
        updateSubjectIdentifiers();
    }

    public String getPndId() {
        return this.pndId;
    }

    public void setPndId(String pndId) {
        this.pndId = pndId;
        updateSubjectIdentifiers();
    }
    
    private void updateSubjectIdentifiers() {
    	HashSet<String> tmpSet = new HashSet<String>();
    	if (getPersonenname() != null) {
    		tmpSet.add("http://psi.archaeologie.sachsen.de/preusker/Person/" + getPersonenname().replace(',', '_').replace(" ", ""));
    	}
    	if (getPndId() != null) {
    		tmpSet.add("http://d-nb.info/gnd/" + getPndId());
    	}
    	if (getWikipediaUrl() != null) {
    		tmpSet.add(getWikipediaUrl());
    	}
    	setSubjectIdentifierSet(tmpSet);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.subjectIdentifierSet == null) ? 0 : this.subjectIdentifierSet.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass()!= obj.getClass()) {
            return false;
        }
        Person other = ((Person) obj);
        if (this.subjectIdentifierSet == null) {
            if (other.subjectIdentifierSet!= null) {
                return false;
            }
        } else {
            if (!this.subjectIdentifierSet.equals(other.subjectIdentifierSet)) {
                return false;
            }
        }
        return true;
    }

}
