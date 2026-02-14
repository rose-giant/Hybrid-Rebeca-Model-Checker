package org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules;

import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules.HybridRebecaTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HybridRebecaCompositionLevelTakeMessageSOSRule extends AbstractHybridSOSRule<HybridRebecaSystemState> {

    @Autowired
    HybridRebecaTakeMessageSOSRule hybridRebecaTakeMessageSOSRule = new HybridRebecaTakeMessageSOSRule();

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(HybridRebecaSystemState source) {
        HybridRebecaNondeterministicTransition<HybridRebecaSystemState> transitions = new HybridRebecaNondeterministicTransition<HybridRebecaSystemState>();
        HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
        for (String actorId : backup.getActorsIds()) {
            HybridRebecaActorState hybridRebecaActorState = source.getActorState(actorId);
            if (hybridRebecaTakeMessageSOSRule.isEnable(hybridRebecaActorState) && !hybridRebecaActorState.isSuspent()) {

                HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                        (HybridRebecaDeterministicTransition<HybridRebecaActorState>)
                        hybridRebecaTakeMessageSOSRule.applyRule(hybridRebecaActorState);

                transitions.addDestination(result.getAction(), source);
                source = HybridRebecaStateSerializationUtils.clone(backup);
            }
        }

        if (transitions.getDestinations().size() == 1) {
            HybridRebecaDeterministicTransition<HybridRebecaSystemState> transition = new HybridRebecaDeterministicTransition<>();
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
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(Action synchAction, HybridRebecaSystemState source) {
        return null;
    }
}
