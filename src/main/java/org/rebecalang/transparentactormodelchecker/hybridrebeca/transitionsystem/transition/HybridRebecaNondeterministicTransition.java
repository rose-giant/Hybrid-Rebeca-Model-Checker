package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import java.util.ArrayList;
import java.util.List;

public class HybridRebecaNondeterministicTransition<H> extends HybridRebecaAbstractTransition<H>{
    private List<Pair<? extends Action, H>> destinations;

    public HybridRebecaNondeterministicTransition() {
        destinations = new ArrayList<Pair<? extends Action,H>>();
    }

    public List<Pair<? extends Action, H>> getDestinations() {
        return destinations;
    }

    public void addDestination(Action action, H destination) {
        destinations.add(new Pair<Action, H>(action, destination));
    }

    public void addAllDestinations(List<Pair<? extends Action, H>> destinations) {
        this.destinations.addAll(destinations);
    }
}
