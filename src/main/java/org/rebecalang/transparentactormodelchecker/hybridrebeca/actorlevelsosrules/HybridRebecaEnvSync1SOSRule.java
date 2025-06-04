package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

public class HybridRebecaEnvSync1SOSRule {

    public HybridRebecaDeterministicTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source) {
        if(source.getLowerBound(source.getResumeTime()) >= source.getUpperBound(source.getNow()))
            throw new RebecaRuntimeInterpreterException("Execution rule is disabled");

        //TODO: Revise this
        Pair<Integer, Integer> updatedNowTimeInterval = new Pair<>();
        updatedNowTimeInterval.setFirst(source.getLowerBound(source.getNow()) + 1);
        updatedNowTimeInterval.setSecond(
                source.getUpperBound(source.getNow()) + updatedNowTimeInterval.getFirst() - source.getLowerBound(source.getNow()));
        source.setNow(updatedNowTimeInterval);

        HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                new HybridRebecaDeterministicTransition<HybridRebecaActorState>();
        result.setAction(new TimeProgressAction());
        result.setDestination(source);
        return result;
    }
}
