package org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules.RealTimeRebecaReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules.RealTimeRebecaNetworkTransferSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RealTimeRebecaCompositionLevelNetworkDeliverySOSRule extends AbstractRealTimeSOSRule<RealTimeRebecaSystemState> {

    @Autowired
    RealTimeRebecaNetworkTransferSOSRule realTimeRebecaNetworkTransferSOSRule = new RealTimeRebecaNetworkTransferSOSRule();
    @Autowired
    RealTimeRebecaReceiveSOSRule realTimeRebecaReceiveSOSRule = new RealTimeRebecaReceiveSOSRule();

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaSystemState> applyRule(RealTimeRebecaSystemState source) {
        RealTimeRebecaNondeterministicTransition<RealTimeRebecaSystemState> transitions = new
                RealTimeRebecaNondeterministicTransition<RealTimeRebecaSystemState>();
        if(source.getNetworkState().getReceivedMessages().size() == 0)
            return transitions;

        RealTimeRebecaSystemState backup = null;
        RealTimeRebecaNetworkState networkState = source.getNetworkState();
        networkState.setNow(source.getNow());
        RealTimeRebecaAbstractTransition<RealTimeRebecaNetworkState> deliveredMessageTransitions =
                realTimeRebecaNetworkTransferSOSRule.applyRule(networkState);

        if (deliveredMessageTransitions instanceof RealTimeRebecaNondeterministicTransition<RealTimeRebecaNetworkState>) {
            for(Pair<? extends Action, RealTimeRebecaNetworkState> deliverable : deliveredMessageTransitions.getDestinations()) {
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
        } else if (deliveredMessageTransitions instanceof RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState>) {
            backup = HybridRebecaStateSerializationUtils.clone(source);
            RealTimeRebecaDeterministicTransition result = new RealTimeRebecaDeterministicTransition<>();
            result.setDestination(deliveredMessageTransitions);
            Action action;
            if (((RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState>) deliveredMessageTransitions).getAction() instanceof MessageAction) {
                action = ((RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState>) deliveredMessageTransitions).getAction();
                source.getActorState(((MessageAction)action).getMessage().getReceiver().getId()).receiveMessage(((MessageAction)action).getMessage());
            } else {
                action = Action.TAU;
            }

            source.setNetworkState(((RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState>) deliveredMessageTransitions).getDestination());
            result.setDestination(source);
            result.setAction(action);
            return result;
        }
        else if (deliveredMessageTransitions == null) {
            return null;
        }

//        if (transitions.getDestinations().size() == 1) {
//            HybridRebecaDeterministicTransition<HybridRebecaSystemState> transition = new HybridRebecaDeterministicTransition<>();
//            transition.setDestination(transitions.getDestinations().get(0).getSecond());
//            transition.setAction(transitions.getDestinations().get(0).getFirst());
//            return transition;
//        }

        return transitions;
    }

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaSystemState> applyRule(Action synchAction, RealTimeRebecaSystemState source) {
        return null;
    }
}
