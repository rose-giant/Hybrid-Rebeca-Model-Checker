package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaEnvSync2SOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkEnvSync1SOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkEnvSync2SOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkTransferSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
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
        HybridRebecaNetworkState networkState = source.getNetworkState();
        networkState.setNow(source.getNow());
        HybridRebecaAbstractTransition<HybridRebecaNetworkState> deliveredMessageTransitions = hybridRebecaNetworkTransferSOSRule.applyRule(networkState);

        if (deliveredMessageTransitions instanceof HybridRebecaNondeterministicTransition<HybridRebecaNetworkState>) {
            for(Pair<? extends Action, HybridRebecaNetworkState> deliverable : deliveredMessageTransitions.getDestinations()) {
                backup = HybridRebecaStateSerializationUtils.clone(source);
                Action action;
                if (deliverable.getFirst() instanceof MessageAction) {
                    action = deliverable.getFirst();
                    source.getActorState(((MessageAction)action).getMessage().getReceiver().getId()).receiveMessage(((MessageAction)action).getMessage());
                } else {
                    action = Action.TAU;
                }
                source.setNetworkState(deliverable.getSecond());
                transitions.addDestination(action, source);
                source = backup;
            }
        }
        else {
            return null;
        }

        return transitions;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(Action synchAction, HybridRebecaSystemState source) {
        return null;
    }
}
