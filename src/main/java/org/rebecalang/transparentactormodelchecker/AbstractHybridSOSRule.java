package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;

public abstract class AbstractHybridSOSRule<T> {
    public boolean isEnable(HybridRebecaActorState source) {
        return true;
    }

    public abstract HybridRebecaAbstractTransition<T> applyRule(T source);

    public abstract HybridRebecaAbstractTransition<T> applyRule(Action synchAction, T source);

    public abstract HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action synchAction, HybridRebecaSystemState source);
}
