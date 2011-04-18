package de.topicmapslab.aranuka.test.rewrite.model;

import java.util.List;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Scope;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="http://test.de/person")
public class Person {
	
	@Id(autogenerate=true, type=IdType.SUBJECT_IDENTIFIER)
	private String id;
	
	@Name
	private String name;

	@Occurrence
	private int age;
	
	@Occurrence
	@Scope(themes="http://de.wikipedia.de/deutsch")
	private String vita;
	
	@Association(type="http://de.wikipedia.de/stateassoc", played_role="http://de.wikipedia.de/person", other_role="http://de.wikipedia.de/state", persistOnCascade=true)
	private State state;
	
	@Association(type="http://de.wikipedia.de/nonstateassoc", played_role="http://de.wikipedia.de/person", other_role="http://de.wikipedia.de/state", persistOnCascade=true)
	private List<State> nonstate;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
		
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getVita() {
		return vita;
	}

	public void setVita(String vita) {
		this.vita = vita;
	}
	
	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * @param nonstate the nonstate to set
	 */
	public void setNonstate(List<State> nonstate) {
		this.nonstate = nonstate;
	}
	
	/**
	 * @return the nonstate
	 */
	public List<State> getNonstate() {
		return nonstate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!getClass().isAssignableFrom(obj.getClass()))
			return false;
		Person other = (Person) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
