
package de.topicmapslab.aranuka.test.preusker.model;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = {
    "http://de.wikipedia.org/wiki/Digitalisat"
})
public class Digitalisat implements Comparable<Digitalisat>{

    @Id(type = IdType.ITEM_IDENTIFIER, autogenerate = true)
    private String id;
    @Name(type = "http://de.wikipedia.org/wiki/Titel")
    private String titel;
    @de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Quelle_(Geschichtswissenschaft)", datatype = "http://www.w3.org/2001/XMLSchema#anyType")
    private String quelle;
    @de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Sonstiges", datatype = "http://www.w3.org/2001/XMLSchema#string")
    private String weitereInformationen;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitel() {
        return this.titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getQuelle() {
        return this.quelle;
    }

    public void setQuelle(String quelle) {
        this.quelle = quelle;
    }

    public String getWeitereInformationen() {
        return this.weitereInformationen;
    }

    public void setWeitereInformationen(String weitereInformationen) {
        this.weitereInformationen = weitereInformationen;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
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
        Digitalisat other = ((Digitalisat) obj);
        if (this.id == null) {
            if (other.id!= null) {
                return false;
            }
        } else {
            if (!this.id.equals(other.id)) {
                return false;
            }
        }
        return true;
    }

    public int compareTo( Digitalisat argument ) {
        return titel.compareToIgnoreCase(argument.getTitel());
    }
}
