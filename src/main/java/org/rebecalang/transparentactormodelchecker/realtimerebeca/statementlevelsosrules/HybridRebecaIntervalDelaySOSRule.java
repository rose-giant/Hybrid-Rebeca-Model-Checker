package org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

    @Component
    public class HybridRebecaIntervalDelaySOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {

        @Override
        public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        ContnuousNonDetInstructionBean cnib = (ContnuousNonDetInstructionBean) source.getSecond();
        float assignee = (float) cnib.getAssignee();
        float updatedResumeTimeLowerBound = source.getFirst().getResumeTime().getFirst() + assignee;
        float updatedResumeTimeUpperBound = source.getFirst().getResumeTime().getSecond() + assignee;
        Pair newResumeTime = new Pair(updatedResumeTimeLowerBound, updatedResumeTimeUpperBound);
        source.getFirst().setResumeTime(newResumeTime);
        source.getFirst().movePCtoTheNextInstruction();

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>();
        result.setDestination(source);
        result.setAction(Action.TAU);
        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}
