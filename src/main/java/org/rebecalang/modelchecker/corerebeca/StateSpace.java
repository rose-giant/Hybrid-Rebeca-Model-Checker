package org.rebecalang.modelchecker.corerebeca;

import java.io.PrintStream;
import java.util.Hashtable;

public class StateSpace<T extends State<? extends BaseActorState>> {
	Hashtable<Long, T> statespace;
	T initialState;

	public StateSpace() {
		statespace = new Hashtable<>();
	}

	public void addState(T state) {
		addState(Long.valueOf(state.hashCode()), state);
	}

	public void addInitialState(T initialState) {
		this.initialState = initialState;
		addState(initialState);
	}

	public void addState(Long stateKey, T state) {
		statespace.put(stateKey, state);
	}

	public T getState(Long stateKey) {
		return statespace.get(stateKey);
	}

	public boolean hasStateWithKey(Long stateKey) {
		return statespace.containsKey(stateKey);
	}

	public T getInitialState() {
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
