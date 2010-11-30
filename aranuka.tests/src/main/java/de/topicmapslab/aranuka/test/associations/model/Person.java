
package de.topicmapslab.aranuka.test.associations.model;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = {
    "http://test.de/person"
})
public class Person {

    @Id(type = IdType.SUBJECT_IDENTIFIER)
    private String subjectIdentifier;
    @Name(type = "http://test.de/name")
    private String name;
    @Association(type = "http://test.de/lives", persistOnCascade = true, played_role = "http://test.de/person")
    private de.topicmapslab.aranuka.test.associations.model.Person.Lives lives;

    @Association(type = "http://test.de/is_spy", played_role="http://test.de/spy")
    private boolean spy;
    
    @Association(type = "http://test.de/has_gun", played_role="http://test.de/spy", other_role="http://test.de/weapon")
    private Gun gun;
    
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

    public de.topicmapslab.aranuka.test.associations.model.Person.Lives getLives() {
        return this.lives;
    }

    public void setLives(de.topicmapslab.aranuka.test.associations.model.Person.Lives lives) {
        this.lives = lives;
    }

    public boolean isSpy() {
		return spy;
	}
    
    public void setSpy(boolean spy) {
		this.spy = spy;
	}
    
    public void setGun(Gun gun) {
		this.gun = gun;
	}
    
    public Gun getGun() {
		return gun;
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
        Person other = ((Person) obj);
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

    @AssociationContainer
    public static class Lives {

        @de.topicmapslab.aranuka.annotations.Role(type = "http://test.de/date")
        private de.topicmapslab.aranuka.test.associations.model.Date date;
        @de.topicmapslab.aranuka.annotations.Role(type = "http://test.de/adress")
        private de.topicmapslab.aranuka.test.associations.model.Address adress;

        public de.topicmapslab.aranuka.test.associations.model.Date getDate() {
            return this.date;
        }

        public void setDate(de.topicmapslab.aranuka.test.associations.model.Date date) {
            this.date = date;
        }

        public de.topicmapslab.aranuka.test.associations.model.Address getAdress() {
            return this.adress;
        }

        public void setAdress(de.topicmapslab.aranuka.test.associations.model.Address adress) {
            this.adress = adress;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.date == null) ? 0 : this.date.hashCode());
            result = prime * result + ((this.adress == null) ? 0 : this.adress.hashCode());
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
            Person.Lives other = ((Person.Lives) obj);
            if (this.date == null) {
                if (other.date!= null) {
                    return false;
                }
            } else {
                if (!this.date.equals(other.date)) {
                    return false;
                }
            }
            if (this.adress == null) {
                if (other.adress!= null) {
                    return false;
                }
            } else {
                if (!this.adress.equals(other.adress)) {
                    return false;
                }
            }
            return true;
        }

    }

}
