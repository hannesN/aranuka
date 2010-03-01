package de.topicmapslab.aranuka.test.annotations;

import java.util.Set;

import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.Role;

@AssociationContainer
public class AssociationContainer01 {

	@Role(type="test:counter_player_01_role")
	private CounterPlayer01 counterPlayer01;
	@Role(type="test:counter_player_02_role")
	private Set<CounterPlayer02> counterPlayer02;
	public CounterPlayer01 getCounterPlayer01() {
		return counterPlayer01;
	}
	public void setCounterPlayer01(CounterPlayer01 counterPlayer01) {
		this.counterPlayer01 = counterPlayer01;
	}
	public Set<CounterPlayer02> getCounterPlayer02() {
		return counterPlayer02;
	}
	public void setCounterPlayer02(Set<CounterPlayer02> counterPlayer02) {
		this.counterPlayer02 = counterPlayer02;
	}

}
