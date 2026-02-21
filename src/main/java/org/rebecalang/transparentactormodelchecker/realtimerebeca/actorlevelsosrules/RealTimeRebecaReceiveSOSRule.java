package org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules;

import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class RealTimeRebecaReceiveSOSRule extends AbstractRealTimeSOSRule<RealTimeRebecaActorState> {
    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaActorState> applyRule(RealTimeRebecaActorState source) {
        return null;
    }

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaActorState> applyRule(Action synchAction, RealTimeRebecaActorState source) {
        source.receiveMessage(((MessageAction)synchAction).getMessage());

        RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState> result =
                new RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState>();
        result.setDestination(source);
        return result;
    }
}
