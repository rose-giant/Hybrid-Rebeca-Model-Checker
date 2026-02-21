package org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules;

import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules.RealTimeRebecaTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RealTimeRebecaCompositionLevelTakeMessageSOSRule extends AbstractRealTimeSOSRule<RealTimeRebecaSystemState> {

    @Autowired
    RealTimeRebecaTakeMessageSOSRule realTimeRebecaTakeMessageSOSRule = new RealTimeRebecaTakeMessageSOSRule();

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaSystemState> applyRule(RealTimeRebecaSystemState source) {
        RealTimeRebecaNondeterministicTransition<RealTimeRebecaSystemState> transitions = new RealTimeRebecaNondeterministicTransition<RealTimeRebecaSystemState>();
        RealTimeRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
        for (String actorId : backup.getActorsIds()) {
            RealTimeRebecaActorState realTimeRebecaActorState = source.getActorState(actorId);
            if (realTimeRebecaTakeMessageSOSRule.isEnable(realTimeRebecaActorState) && !realTimeRebecaActorState.isSuspent()) {

                RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState> result =
                        (RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState>)
                        realTimeRebecaTakeMessageSOSRule.applyRule(realTimeRebecaActorState);

                transitions.addDestination(result.getAction(), source);
                source = HybridRebecaStateSerializationUtils.clone(backup);
            }
        }

        if (transitions.getDestinations().size() == 1) {
            RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState> transition = new RealTimeRebecaDeterministicTransition<>();
            transition.setDestination(transitions.getDestinations().get(0).getSecond());
            transition.setAction(transitions.getDestinations().get(0).getFirst());
            return transition;
        }

        if (transitions.getDestinations().isEmpty()) {
            return null;
        }
        return transitions;
    }

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaSystemState> applyRule(Action synchAction, RealTimeRebecaSystemState source) {
        return null;
    }
}
