package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition;

import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;

public class HybridRebecaDeterministicTransition<H> extends HybridRebecaAbstractTransition<H> {

    private H destination;
    private Action action;

    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        this.action = action;
    }

    public H getDestination() {
        return destination;
    }
    public void setDestination(H destination) {
        this.destination = destination;
    }

}
