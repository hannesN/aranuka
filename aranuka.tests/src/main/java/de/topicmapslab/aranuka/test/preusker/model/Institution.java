
package de.topicmapslab.aranuka.test.preusker.model;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = {
    "http://de.wikipedia.org/wiki/Institution"
})
public class Institution
    extends Entity
{

    @Id(type = IdType.SUBJECT_IDENTIFIER)
    private String subjectIdentifier;
    @Name(type = "http://de.wikipedia.org/wiki/Name")
    private String name;
    @Association(type = "http://psi.archaeologie.sachsen.de/preusker/institutionsort", played_role = "http://psi.archaeologie.sachsen.de/preusker/ansaessige_institution", other_role = "http://psi.archaeologie.sachsen.de/preusker/ort_der_institution")
    private Ort ort;

    public String getSubjectIdentifier() {
    	return this.subjectIdentifier;
    }

    public void setSubjectIdentifier(String subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        setSubjectIdentifier("http://psi.archaeologie.sachsen.de/preusker/" + name.replace(',', '_').replace(" ", ""));
    }

    public Ort getOrt() {
        return this.ort;
    }

    public void setOrt(Ort ort) {
        this.ort = ort;
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
        Institution other = ((Institution) obj);
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

}
