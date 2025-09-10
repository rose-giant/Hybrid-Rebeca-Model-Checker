package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.stereotype.Component;

@Component
public class HybridRebecaEnvSync2SOSRule extends AbstractHybridSOSRule<HybridRebecaActorState> {

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source) {
        if(!source.messageQueueIsEmpty())
            throw new RebecaRuntimeInterpreterException("envSync2 rule is disabled");

        HybridRebecaActorState backup = HybridRebecaStateSerializationUtils.clone(source);
        float lowerBound = Float.MAX_VALUE;
        float upperBound = Float.MAX_VALUE;
        backup.setNow(new Pair<>(lowerBound, upperBound));
        HybridRebecaDeterministicTransition<HybridRebecaActorState> result = new HybridRebecaDeterministicTransition<>();
        result.setDestination(backup);
        TimeProgressAction timeAction = new TimeProgressAction();
        timeAction.setTimeIntervalProgress(new Pair<>(new Pair<>(lowerBound, lowerBound), new Pair<>(upperBound, upperBound)));
        result.setAction(timeAction);
        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }
}
