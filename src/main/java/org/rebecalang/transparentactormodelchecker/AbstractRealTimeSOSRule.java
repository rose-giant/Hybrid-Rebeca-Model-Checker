package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;

public abstract class AbstractRealTimeSOSRule<H> {
    public boolean isEnable(RealTimeRebecaActorState source) {
        return true;
    }

    public abstract RealTimeRebecaAbstractTransition<H> applyRule(H source);

    public abstract RealTimeRebecaAbstractTransition<H> applyRule(Action synchAction, H source);
}
