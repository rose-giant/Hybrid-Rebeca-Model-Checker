package org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules;

import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

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
