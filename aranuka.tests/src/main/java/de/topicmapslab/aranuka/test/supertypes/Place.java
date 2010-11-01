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

	@Id(type = IdType.SUBJECT_IDENTIFIER, autogenerate=true)
	private String si;

	@Name
	private String name;

	@Association(type = "http://test.de/owners", played_role = "http://test.de/owner_place", persistOnCascade=true)
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

	}
}
