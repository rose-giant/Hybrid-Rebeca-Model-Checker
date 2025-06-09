package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;

public class CoreRebecaDeterministicTransition<T> extends CoreRebecaAbstractTransition<T> {
	private T destination;
	private Action action;

	public CoreRebecaDeterministicTransition() {
		action = Action.TAU;
	}

	public CoreRebecaDeterministicTransition(T destination) {
		super();
		this.destination = destination;
	}

	public CoreRebecaDeterministicTransition(Action action, T destination) {
		this.action = action;
		this.destination = destination;
	}

	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}

	public T getDestination() {
		return destination;
	}
	public void setDestination(T destination) {
		this.destination = destination;
	}
}
