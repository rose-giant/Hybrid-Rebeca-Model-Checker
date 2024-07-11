package org.rebecalang.modelchecker.corerebeca;

import java.io.PrintStream;
import java.util.Hashtable;

public class StateSpace<T extends BaseActorState> {
	Hashtable<Long, State<T>> statespace;
	State<T> initialState;
	
	public StateSpace() {
		statespace = new Hashtable<Long, State<T>>();
	}

	public void addState(State<T> state) {
		addState(Long.valueOf(state.hashCode()), state);
	}
	
	public void addInitialState(State<T> initialState) {
		this.initialState = initialState;
		addState(initialState);
	}

	public void addState(Long stateKey, State<T> state) {
		statespace.put(stateKey, state);
		
	}

	public State<T> getState(Long stateKey) {
		return statespace.get(stateKey);
	}

	public boolean hasStateWithKey(Long stateKey) {
		return statespace.containsKey(stateKey);
	}

	public State<T> getInitialState() {
		return initialState;
	}

	public int size() {
		return statespace.size();
	}

	public void exportStateSpace(PrintStream output) {
		output.println("<transitionsystem>");
		initialState.exportState(output);
		output.println("</transitionsystem>");
	}
}
