
package de.topicmapslab.aranuka.test.preusker.model;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = {
    "http://de.wikipedia.org/wiki/Publikation"
})
public class Publikation implements Comparable<Publikation>{

    @Id(type = IdType.ITEM_IDENTIFIER, autogenerate = true)
    private String id;
    @Name(type = "http://de.wikipedia.org/wiki/Zitation")
    private String zitation;
    @de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Opac", datatype = "http://www.w3.org/2001/XMLSchema#anyURI")
    private String webOPACUrl;
    @de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Sonstiges", datatype = "http://www.w3.org/2001/XMLSchema#string")
    private String weitereInformationen;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZitation() {
        return this.zitation;
    }

    public void setZitation(String zitation) {
        this.zitation = zitation;
    }

    public String getWebOPACUrl() {
        return this.webOPACUrl;
    }

    public void setWebOPACUrl(String webOPACUrl) {
        this.webOPACUrl = webOPACUrl;
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
        Publikation other = ((Publikation) obj);
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

    public int compareTo( Publikation argument ) {
        return zitation.compareToIgnoreCase(argument.getZitation());
    }
}
