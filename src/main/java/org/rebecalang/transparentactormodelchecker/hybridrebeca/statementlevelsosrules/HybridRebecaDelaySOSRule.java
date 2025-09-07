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
        Float delayLowerBound = (float)0;
        Float delayUpperBound = (float) 0;
        if (source.getSecond() instanceof ContnuousNonDetInstructionBean) {
            ContnuousNonDetInstructionBean delayInterval = (ContnuousNonDetInstructionBean) source.getSecond();
            delayLowerBound = (Float) delayInterval.getLowerBound();
            delayUpperBound = (Float) delayInterval.getUpperBound();
        }

        source.getFirst().setResumeTime(new Pair<>(source.getFirst().getResumeTime().getFirst() + delayLowerBound, source.getFirst().getResumeTime().getSecond() + delayUpperBound));
        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState,InstructionBean>>();
        result.setDestination(source);
        result.setAction(Action.TAU);
        HybridRebecaResumeSOSRule rebecaResumeSOSRule = new HybridRebecaResumeSOSRule();
        return rebecaResumeSOSRule.applyRule(source);
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
