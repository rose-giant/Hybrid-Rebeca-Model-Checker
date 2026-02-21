package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition;

import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;

public class RealTimeRebecaDeterministicTransition<H> extends RealTimeRebecaAbstractTransition<H> {

    private H destination;
    private Action action;

    public RealTimeRebecaDeterministicTransition(H destination) {
        super();
        this.destination = destination;
    }

    public RealTimeRebecaDeterministicTransition(Action action, H destination) {
        this.action = action;
        this.destination = destination;
    }

    public RealTimeRebecaDeterministicTransition() {
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
