package de.topicmapslab.aranuka.test.update;

import java.util.Set;

import org.junit.Ignore;

import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.Role;

@Ignore
@AssociationContainer
public class TestAssociationContainer {

	@Role(type="test:counter_player_role")
	private TestCounterPlayer counterPlayer;
	
	@Role(type="test:counter_player_set_role")
	private Set<TestCounterPlayer> counterPlayerSet;

	public TestCounterPlayer getCounterPlayer() {
		return counterPlayer;
	}

	public void setCounterPlayer(TestCounterPlayer counterPlayer) {
		this.counterPlayer = counterPlayer;
	}

	public Set<TestCounterPlayer> getCounterPlayerSet() {
		return counterPlayerSet;
	}

	public void setCounterPlayerSet(Set<TestCounterPlayer> counterPlayerSet) {
		this.counterPlayerSet = counterPlayerSet;
	}
	
	

}
