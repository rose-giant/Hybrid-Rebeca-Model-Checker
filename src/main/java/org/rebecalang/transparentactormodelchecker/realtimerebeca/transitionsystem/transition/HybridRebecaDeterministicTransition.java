package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition;

import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;

public class HybridRebecaDeterministicTransition<H> extends HybridRebecaAbstractTransition<H> {

    private H destination;
    private Action action;

    public HybridRebecaDeterministicTransition(H destination) {
        super();
        this.destination = destination;
    }

    public HybridRebecaDeterministicTransition(Action action, H destination) {
        this.action = action;
        this.destination = destination;
    }

    public HybridRebecaDeterministicTransition() {
    }

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
