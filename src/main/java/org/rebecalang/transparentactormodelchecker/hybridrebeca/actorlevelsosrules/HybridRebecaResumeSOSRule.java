package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

public class HybridRebecaResumeSOSRule extends AbstractHybridSOSRule<HybridRebecaActorState> {

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source) {
        HybridRebecaActorState originalSource = HybridRebecaStateSerializationUtils.clone(source);

        Pair<Float, Float> sourceNow = source.getNow();
        Pair<Float, Float> sourceResumeTime = source.getResumeTime();

        if(sourceNow.getFirst().equals(sourceResumeTime.getFirst()) &&
                sourceNow.getSecond() < sourceResumeTime.getSecond() ) {
            HybridRebecaNondeterministicTransition<HybridRebecaActorState> result = new HybridRebecaNondeterministicTransition<>();

            originalSource.setResumeTime(sourceNow);
            result.addDestination(Action.TAU, originalSource);

            HybridRebecaActorState sourceState = source;
            Pair<Float, Float> postponeResumeTime = new Pair<>(sourceNow.getSecond() ,sourceResumeTime.getSecond());
            sourceState.setResumeTime(postponeResumeTime);
            TimeProgressAction timeProgressAction = new TimeProgressAction();
            result.addDestination(timeProgressAction, sourceState);
            return result;
        }
        else if(sourceNow.getFirst().equals(sourceResumeTime.getFirst())) {
            HybridRebecaDeterministicTransition<HybridRebecaActorState> result = new HybridRebecaDeterministicTransition<>();
            HybridRebecaActorState sourceState = source;
            sourceState.setResumeTime(sourceNow);
            result.setDestination(sourceState);
            result.setAction(Action.TAU);
            return result;
        }
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }
}
