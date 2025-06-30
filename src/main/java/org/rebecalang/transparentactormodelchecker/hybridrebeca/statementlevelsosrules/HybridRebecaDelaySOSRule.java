package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

public class HybridRebecaDelaySOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        HybridRebecaActorState hybridRebecaActorState = getHybridRebecaActorState(source);
        source.setFirst(hybridRebecaActorState);
        HybridRebecaAbstractTransition<Pair<HybridRebecaActorState,InstructionBean>> result = resume(source);
        return result;
    }

    private static HybridRebecaAbstractTransition<Pair<HybridRebecaActorState,InstructionBean>> resume(Pair<HybridRebecaActorState, InstructionBean> source) {
        HybridRebecaActorState originalSource = HybridRebecaStateSerializationUtils.clone(source.getFirst());

        Pair<Float, Float> sourceNow = source.getFirst().getNow();
        Pair<Float, Float> sourceResumeTime = source.getFirst().getResumeTime();

        if(sourceNow.getFirst().equals(sourceResumeTime.getFirst()) &&
                sourceNow.getSecond() < sourceResumeTime.getSecond() ) {
            HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaNondeterministicTransition<>();
            Pair<HybridRebecaActorState, InstructionBean> resultInnerPair = new Pair<>(originalSource, source.getSecond());

            originalSource.setResumeTime(sourceNow);
            result.addDestination(Action.TAU, resultInnerPair);

            HybridRebecaActorState sourceState = source.getFirst();
            Pair<Float, Float> postponeResumeTime = new Pair<>(sourceNow.getSecond() ,sourceResumeTime.getSecond());
            sourceState.setResumeTime(postponeResumeTime);
            TimeProgressAction timeProgressAction = new TimeProgressAction();
            Pair<HybridRebecaActorState, InstructionBean> resultInnerPair2 = new Pair<>(sourceState, source.getSecond());

            result.addDestination(timeProgressAction, resultInnerPair2);
            return result;
        }
        else if(sourceNow.getFirst().equals(sourceResumeTime.getFirst())) {
            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaDeterministicTransition<>();
            HybridRebecaActorState sourceState = source.getFirst();
            sourceState.setResumeTime(sourceNow);
            Pair<HybridRebecaActorState, InstructionBean> resultInnerPair = new Pair<>(sourceState, source.getSecond());
            result.setDestination(resultInnerPair);
            result.setAction(Action.TAU);
            return result;
        }

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState,InstructionBean>>();
        result.setDestination(source);
        result.setAction(Action.TAU);
        return result;
    }

    private static HybridRebecaActorState getHybridRebecaActorState(Pair<HybridRebecaActorState, InstructionBean> source) {
        ContnuousNonDetInstructionBean contnuousNonDetInstructionBean = (ContnuousNonDetInstructionBean) source.getSecond();
        HybridRebecaActorState hybridRebecaActorState = source.getFirst();

        Pair<Float, Float> newResumeTime = new Pair<>();
        newResumeTime.setFirst(hybridRebecaActorState.getResumeTime().getFirst() + (Float) contnuousNonDetInstructionBean.getLowerBound());
        newResumeTime.setSecond(hybridRebecaActorState.getResumeTime().getSecond() + (Float) contnuousNonDetInstructionBean.getUpperBound());
        hybridRebecaActorState.setResumeTime(newResumeTime);
        return hybridRebecaActorState;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}
