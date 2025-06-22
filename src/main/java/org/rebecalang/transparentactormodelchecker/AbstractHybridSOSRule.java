package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;

public abstract class AbstractHybridSOSRule<H> {
    public boolean isEnable(HybridRebecaActorState source) {
        return true;
    }

    public abstract HybridRebecaAbstractTransition<H> applyRule(H source);

    public abstract HybridRebecaAbstractTransition<H> applyRule(Action synchAction, H source);
}
