
package de.topicmapslab.aranuka.test.preusker.model;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = {
    "http://de.wikipedia.org/wiki/Kategorie"
})
public class Kategorie implements Comparable<Kategorie>{
    @Id(type = IdType.SUBJECT_IDENTIFIER)
    private String subjectIdentifier;
    @Name(type = "http://de.wikipedia.org/wiki/Name")
    private String name;
    @Occurrence(type = "http://de.wikipedia.org/wiki/Schlagwortnormdatei", datatype = "http://www.w3.org/2001/XMLSchema#string")
    private String swdId;

    public String getSubjectIdentifier() {
    	return this.subjectIdentifier;
    }

    public void setSubjectIdentifier(String subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSwdId() {
        return this.swdId;
    }

    public void setSwdId(String swdId) {
        this.swdId = swdId;
        setSubjectIdentifier("http://d-nb.info/gnd/" + swdId);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.subjectIdentifier == null) ? 0 : this.subjectIdentifier.hashCode());
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
        Kategorie other = ((Kategorie) obj);
        if (this.subjectIdentifier == null) {
            if (other.subjectIdentifier!= null) {
                return false;
            }
        } else {
            if (!this.subjectIdentifier.equals(other.subjectIdentifier)) {
                return false;
            }
        }
        return true;
    }

    public int compareTo( Kategorie argument ) {
        return name.compareToIgnoreCase(argument.getName());
    }
}
