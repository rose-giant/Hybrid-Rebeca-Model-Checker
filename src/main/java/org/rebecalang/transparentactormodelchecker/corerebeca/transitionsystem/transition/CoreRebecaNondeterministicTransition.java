package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition;

import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;

public class CoreRebecaNondeterministicTransition<T> extends CoreRebecaAbstractTransition<T> {
	private List<Pair<? extends Action, T>> destinations;

	public CoreRebecaNondeterministicTransition() {
		destinations = new ArrayList<Pair<? extends Action,T>>();
	}

	public List<Pair<? extends Action, T>> getDestinations() {
		return destinations;
	}

	public void addDestination(Action action, T destination) {
		destinations.add(new Pair<Action, T>(action, destination));
	}

	public void addAllDestinations(List<Pair<? extends Action, T>> destinations) {
		this.destinations.addAll(destinations);
	}
}
