
package de.topicmapslab.aranuka.test.associations.model;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = {
    "http://test.de/date"
})
public class Date {

    @Id(type = IdType.ITEM_IDENTIFIER)
    private String itemIdentifier;
    @Occurrence(type = "http://test.de/content", datatype = "http://www.w3.org/2001/XMLSchema#anyType")
    private String content;

    public String getItemIdentifier() {
        return this.itemIdentifier;
    }

    public void setItemIdentifier(String itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.itemIdentifier == null) ? 0 : this.itemIdentifier.hashCode());
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
        Date other = ((Date) obj);
        if (this.itemIdentifier == null) {
            if (other.itemIdentifier!= null) {
                return false;
            }
        } else {
            if (!this.itemIdentifier.equals(other.itemIdentifier)) {
                return false;
            }
        }
        return true;
    }

}
