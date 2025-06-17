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

public class HybridRebecaResumeSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {
    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        HybridRebecaActorState originalSource = HybridRebecaStateSerializationUtils.clone(source.getFirst());

        Pair<Float, Float> sourceNow = source.getFirst().getNow();
        Pair<Float, Float> sourceResumeTime = source.getFirst().getResumeTime();

        if(sourceNow.getFirst().equals(sourceResumeTime.getFirst()) &&
        sourceNow.getSecond() < sourceResumeTime.getSecond() ) {
            HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaNondeterministicTransition<>();

            originalSource.setResumeTime(sourceNow);
            Pair<HybridRebecaActorState, InstructionBean> newSource1 = new Pair<>(originalSource, null);
            result.addDestination(Action.TAU, newSource1);

            HybridRebecaActorState sourceState = source.getFirst();
            Pair<Float, Float> postponeResumeTime = new Pair<>(sourceNow.getSecond() ,sourceResumeTime.getSecond());
            sourceState.setResumeTime(postponeResumeTime);
            Pair<HybridRebecaActorState, InstructionBean> newSource2 = new Pair<>(sourceState, null);
            TimeProgressAction timeProgressAction = new TimeProgressAction();
            result.addDestination(timeProgressAction, newSource2);
            return result;
        }
        else if(sourceNow.getFirst().equals(sourceResumeTime.getFirst())) {
            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaDeterministicTransition<>();
            HybridRebecaActorState sourceState = source.getFirst();
            sourceState.setResumeTime(sourceNow);
            Pair<HybridRebecaActorState, InstructionBean> newSource2 = new Pair<>(sourceState, null);
            result.setDestination(newSource2);
            result.setAction(Action.TAU);
            return result;
        }
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }
}
