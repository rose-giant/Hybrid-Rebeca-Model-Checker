package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;

public abstract class AbstractHybridSOSRule<H> {
    public boolean isEnable(HybridRebecaActorState source) {
        return true;
    }

    public abstract HybridRebecaAbstractTransition<H> applyRule(H source);

    public abstract HybridRebecaAbstractTransition<H> applyRule(Action synchAction, H source);
}
