package org.rebecalang.transparentactormodelchecker.hybrid.actorStmtRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules.HybridRebecaDelaySOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaDelayTest {
    HybridRebecaDelaySOSRule hybridRebecaDelaySOSRule = new HybridRebecaDelaySOSRule();
    HybridRebecaActorState hybridRebecaActorState1 = new HybridRebecaActorState("actor1");

    @Test
    public void nonDetDelayMakesResumeTimeProgress() {
        Object assignee = new Object();
        Float delayLowerBound = (float)1;
        Float delayUpperBound = (float)2;
        Pair<Float, Float> resumeTime = new Pair<>((float)0, (float)0.5);
        Pair<Float, Float> now = new Pair<>((float)1, (float)3.5);
        hybridRebecaActorState1.setResumeTime(resumeTime);
        hybridRebecaActorState1.setNow(now);
        ContnuousNonDetInstructionBean contnuousNonDetInstructionBean = new ContnuousNonDetInstructionBean(assignee, delayLowerBound, delayUpperBound);
        Pair<HybridRebecaActorState, InstructionBean> delayInput = new Pair<>(hybridRebecaActorState1, contnuousNonDetInstructionBean);
        HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> result =  hybridRebecaDelaySOSRule.applyRule(delayInput);
        HybridRebecaDeterministicTransition hdt = (HybridRebecaDeterministicTransition)result;

        HybridRebecaActorState destination = ((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) result).getDestination().getFirst();
        assertEquals(resumeTime.getFirst()+delayLowerBound, destination.getResumeTime().getFirst());
        assertEquals((float) 3.5, destination.getResumeTime().getSecond());
    }

    @Test
    public void nonDetDelayMakesResumeAndPostponeTransitions() {
        Object assignee = new Object();
        Float delayLowerBound = (float)1;
        Float delayUpperBound = (float)2;
        Pair<Float, Float> resumeTime = new Pair<>((float)0, (float)0.5);
        Pair<Float, Float> now = new Pair<>((float)1, (float)1.5);
        hybridRebecaActorState1.setResumeTime(resumeTime);
        hybridRebecaActorState1.setNow(now);
        ContnuousNonDetInstructionBean contnuousNonDetInstructionBean = new ContnuousNonDetInstructionBean(assignee, delayLowerBound, delayUpperBound);
        Pair<HybridRebecaActorState, InstructionBean> delayInput = new Pair<>(hybridRebecaActorState1, contnuousNonDetInstructionBean);
        HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> result =  hybridRebecaDelaySOSRule.applyRule(delayInput);
        HybridRebecaNondeterministicTransition hdt = (HybridRebecaNondeterministicTransition)result;
        assertTrue(hdt.getDestinations().size() == 2);
    }

}
