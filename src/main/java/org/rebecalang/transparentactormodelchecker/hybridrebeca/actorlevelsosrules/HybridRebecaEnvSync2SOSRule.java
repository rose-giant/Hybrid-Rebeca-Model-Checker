package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class HybridRebecaEnvSync2SOSRule {

    public HybridRebecaDeterministicTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source, int t) {
        if(!source.messageQueueIsEmpty())
            throw new RebecaRuntimeInterpreterException("Execution rule is disabled");

        Pair<Integer, Integer> updatedNowTimeInterval = new Pair<>();
        updatedNowTimeInterval.setFirst(source.getLowerBound(source.getNow()) + t);
        updatedNowTimeInterval.setSecond(source.getUpperBound(source.getNow()) + t);
        source.setNow(updatedNowTimeInterval);

        HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                new HybridRebecaDeterministicTransition<HybridRebecaActorState>();
        result.setAction(new TimeProgressAction());
        result.setDestination(source);
        return result;
    }
}
