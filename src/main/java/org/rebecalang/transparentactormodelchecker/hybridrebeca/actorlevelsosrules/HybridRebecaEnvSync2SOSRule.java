package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
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
//        TODO: check it here?? or somewhere at a higher level
        if (!source.isSuspent() && source.messageQueueIsEmpty()) {

        }
        HybridRebecaActorState backup = HybridRebecaStateSerializationUtils.clone(source);
        float lowerBound = source.getNow().getSecond().floatValue();
        float upperBound = Float.MAX_VALUE;
        backup.setNow(new Pair<>(backup.getNow().getSecond(), upperBound));
        HybridRebecaDeterministicTransition<HybridRebecaActorState> result = new HybridRebecaDeterministicTransition<>();
        result.setDestination(backup);
        TimeProgressAction timeAction = new TimeProgressAction();
        timeAction.setTimeProgress(new Pair<>(lowerBound, upperBound));
        result.setAction(timeAction);
        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }
}
