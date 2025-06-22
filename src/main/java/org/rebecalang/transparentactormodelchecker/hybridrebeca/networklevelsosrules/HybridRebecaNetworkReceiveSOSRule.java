package org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

public class HybridRebecaNetworkReceiveSOSRule extends AbstractHybridSOSRule<HybridRebecaNetworkState> {

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(HybridRebecaNetworkState source) {
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(Action synchAction, HybridRebecaNetworkState source) {
        HybridRebecaMessage message = ((MessageAction) synchAction).getMessage();
        source.addMessage(message);
        return new HybridRebecaDeterministicTransition<HybridRebecaNetworkState>(synchAction, source);
    }
}
