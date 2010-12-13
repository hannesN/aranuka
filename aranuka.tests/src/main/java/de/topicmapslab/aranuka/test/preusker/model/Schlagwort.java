
package de.topicmapslab.aranuka.test.preusker.model;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = {
    "http://de.wikipedia.org/wiki/Schlagwort"
})
public class Schlagwort implements Comparable<Schlagwort>{

    @Id(type = IdType.ITEM_IDENTIFIER, autogenerate = true)
    private String id;
    @Name(type = "http://de.wikipedia.org/wiki/Name")
    private String name;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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
        Schlagwort other = ((Schlagwort) obj);
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

    public int compareTo( Schlagwort argument ) {
        return name.compareToIgnoreCase(argument.getName());
    }
}
