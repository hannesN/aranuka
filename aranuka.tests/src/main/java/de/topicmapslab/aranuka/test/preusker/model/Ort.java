
package de.topicmapslab.aranuka.test.preusker.model;

import java.util.HashSet;
import java.util.Set;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = {
    "http://de.wikipedia.org/wiki/Ort"
})
public class Ort implements Comparable<Ort>{

    @Id(type = IdType.SUBJECT_IDENTIFIER)
    private Set<String> subjectIdentifierSet;
    @Name(type = "http://de.wikipedia.org/wiki/Name")
    private String name;
    @de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Geoname", datatype = "http://www.w3.org/2001/XMLSchema#string")
    private String geonameID;
    @de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Koordinaten", datatype = "http://en.wikipedia.org/wiki/World_Geodetic_System_1984")
    private String koordinaten;
    @de.topicmapslab.aranuka.annotations.Occurrence(type = "http://psi.archaeologie.sachsen.de/preusker/dia-gis-id", datatype = "http://www.w3.org/2001/XMLSchema#string")
    private String diaGisId;

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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeonameID() {
        return this.geonameID;
    }

    public void setGeonameID(String geonameID) {
        this.geonameID = geonameID;
        updateSubjectIdentifiers();
    }

    public String getKoordinaten() {
        return this.koordinaten;
    }

    public void setKoordinaten(String koordinaten) {
        this.koordinaten = koordinaten;
    }

    public String getDiaGisId() {
        return this.diaGisId;
    }

    public void setDiaGisId(String diaGisId) {
        this.diaGisId = diaGisId;
        updateSubjectIdentifiers();
    }

    private void updateSubjectIdentifiers () {
    	HashSet<String> tmpSet = new HashSet<String>();
    	if (getGeonameID() != null) {
    		tmpSet.add("http://www.geonames.org/" + getGeonameID());
    	}
    	if (getDiaGisId() != null) {
    		tmpSet.add("http://d-nb.info/gnd/" + getDiaGisId());
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
        Ort other = ((Ort) obj);
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

    public int compareTo( Ort argument ) {
        return name.compareToIgnoreCase(argument.getName());
    }

}
