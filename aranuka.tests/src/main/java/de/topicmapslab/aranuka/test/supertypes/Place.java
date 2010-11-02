package de.topicmapslab.aranuka.test.supertypes;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Role;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = "http://test.de/place")
public class Place {

	@Id(type = IdType.SUBJECT_IDENTIFIER, autogenerate = true)
	private String si;

	@Name
	private String name;

	@Association(type = "http://test.de/owners", played_role = "http://test.de/owner_place", persistOnCascade = true)
	private AssocContainer owners;

	public String getSi() {
		return si;
	}

	public void setSi(String si) {
		this.si = si;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AssocContainer getOwners() {
		return owners;
	}

	public void setOwners(AssocContainer owners) {
		this.owners = owners;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owners == null) ? 0 : owners.hashCode());
		result = prime * result + ((si == null) ? 0 : si.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Place other = (Place) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owners == null) {
			if (other.owners != null)
				return false;
		} else if (!owners.equals(other.owners))
			return false;
		if (si == null) {
			if (other.si != null)
				return false;
		} else if (!si.equals(other.si))
			return false;
		return true;
	}



	@AssociationContainer
	public static class AssocContainer {

		@Role(type = "http://test.de/owned")
		private Thing thing;

		@Role(type = "http://test.de/owner")
		private Person owner;

		public Thing getThing() {
			return thing;
		}

		public void setThing(Thing thing) {
			this.thing = thing;
		}

		public Person getOwner() {
			return owner;
		}

		public void setOwner(Person owner) {
			this.owner = owner;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((owner == null) ? 0 : owner.hashCode());
			result = prime * result + ((thing == null) ? 0 : thing.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AssocContainer other = (AssocContainer) obj;
			if (owner == null) {
				if (other.owner != null)
					return false;
			} else if (!owner.equals(other.owner))
				return false;
			if (thing == null) {
				if (other.thing != null)
					return false;
			} else if (!thing.equals(other.thing))
				return false;
			return true;
		}

	}
}
