package org.rebecalang.transparentactormodelchecker;

import java.util.HashMap;

public class TransparentActorStateSpace {
	private HashMap<Long, AbstractTransparentActorState> stateSpace = new HashMap<>();
	private long statesNumber = 0;

	public HashMap<Long, AbstractTransparentActorState> getStateSpace() {
		return stateSpace;
	}

	public void setStateSpace(HashMap<Long, AbstractTransparentActorState> stateSpace) {
		this.stateSpace = stateSpace;
	}

	public long getStatesNumber() {
		return statesNumber;
	}

	public void addStateToStateSpace(AbstractTransparentActorState transparentActorState) {
		statesNumber++;
		stateSpace.put(statesNumber, transparentActorState);
	}
}
