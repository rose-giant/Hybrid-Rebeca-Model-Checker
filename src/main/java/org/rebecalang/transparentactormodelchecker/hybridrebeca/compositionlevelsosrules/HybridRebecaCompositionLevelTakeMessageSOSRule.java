package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HybridRebecaCompositionLevelTakeMessageSOSRule extends AbstractHybridSOSRule<HybridRebecaSystemState> {

    @Autowired
    HybridRebecaTakeMessageSOSRule hybridRebecaTakeMessageSOSRule;

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(HybridRebecaSystemState source) {
        HybridRebecaNondeterministicTransition<HybridRebecaSystemState> transitions = new HybridRebecaNondeterministicTransition<HybridRebecaSystemState>();

        HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
        for (String actorId : backup.getActorsIds()) {
            HybridRebecaActorState hybridRebecaActorState = source.getActorState(actorId);
            if (hybridRebecaTakeMessageSOSRule.isEnable(hybridRebecaActorState)) {

                HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                        (HybridRebecaDeterministicTransition<HybridRebecaActorState>) hybridRebecaTakeMessageSOSRule.applyRule(hybridRebecaActorState);
                transitions.addDestination(result.getAction(), source);
                source = HybridRebecaStateSerializationUtils.clone(backup);
            }
        }

        return transitions;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(Action synchAction, HybridRebecaSystemState source) {
        return null;
    }
}
