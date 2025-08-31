package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkTransferSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HybridRebecaCompositionLevelNetworkDeliverySOSRule extends AbstractHybridSOSRule<HybridRebecaSystemState> {

    @Autowired
    HybridRebecaNetworkTransferSOSRule hybridRebecaNetworkTransferSOSRule = new HybridRebecaNetworkTransferSOSRule();
    @Autowired
    HybridRebecaReceiveSOSRule hybridRebecaReceiveSOSRule = new HybridRebecaReceiveSOSRule();

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(HybridRebecaSystemState source) {
        HybridRebecaNondeterministicTransition<HybridRebecaSystemState> transitions = new
                HybridRebecaNondeterministicTransition<HybridRebecaSystemState>();
        if(source.getNetworkState().getReceivedMessages().size() == 0)
            return transitions;

        HybridRebecaSystemState backup = null;
        HybridRebecaNondeterministicTransition<HybridRebecaNetworkState> deliveredMessageTransitions =
                (HybridRebecaNondeterministicTransition<HybridRebecaNetworkState>) hybridRebecaNetworkTransferSOSRule.applyRule(source.getNetworkState());

        for(Pair<? extends Action, HybridRebecaNetworkState> deliverable : deliveredMessageTransitions.getDestinations()) {
            backup = HybridRebecaStateSerializationUtils.clone(source);
            MessageAction action = (MessageAction) deliverable.getFirst();
            source.setNetworkState(deliverable.getSecond());
            source.getActorState(action.getMessage().getReceiver().getId()).
                    receiveMessage(action.getMessage());
            transitions.addDestination(Action.TAU, source);
            source = backup;
        }

        return transitions;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(Action synchAction, HybridRebecaSystemState source) {
        return null;
    }
}
