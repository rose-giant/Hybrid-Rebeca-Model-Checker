package org.rebecalang.transparentactormodelchecker.realtime.actorStmtRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules.RealTimeRebecaDelaySOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaDelayTest {
    RealTimeRebecaDelaySOSRule realTimeRebecaDelaySOSRule = new RealTimeRebecaDelaySOSRule();
    RealTimeRebecaActorState realTimeRebecaActorState1 = new RealTimeRebecaActorState("actor1");

    @Test
    public void nonDetDelayMakesResumeTimeProgress() {
        Object assignee = new Object();
        Float delayLowerBound = (float)1;
        Float delayUpperBound = (float)2;
        Pair<Float, Float> resumeTime = new Pair<>((float)0, (float)1.5);
        Pair<Float, Float> now = new Pair<>((float)1, (float)3.5);
        realTimeRebecaActorState1.setResumeTime(resumeTime);
        realTimeRebecaActorState1.setNow(now);
        ContnuousNonDetInstructionBean contnuousNonDetInstructionBean = new ContnuousNonDetInstructionBean(assignee, delayLowerBound, delayUpperBound);
        Pair<RealTimeRebecaActorState, InstructionBean> delayInput = new Pair<>(realTimeRebecaActorState1, contnuousNonDetInstructionBean);
        RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =  realTimeRebecaDelaySOSRule.applyRule(delayInput);
        RealTimeRebecaDeterministicTransition hdt = (RealTimeRebecaDeterministicTransition)result;

        RealTimeRebecaActorState destination = ((RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>>) result).getDestination().getFirst();
        assertEquals(resumeTime.getFirst()+delayLowerBound, destination.getResumeTime().getFirst());
        assertEquals((float) 3.5, destination.getResumeTime().getSecond());
    }

    @Test
    public void nonDetDelayMakesResumeAndPostponeTransitions() {
        Object assignee = new Object();
        Float delayLowerBound = (float)1;
        Float delayUpperBound = (float)2;
        Pair<Float, Float> resumeTime = new Pair<>((float)1, (float)1.5);
        Pair<Float, Float> now = new Pair<>((float)2, (float)2.5);
        realTimeRebecaActorState1.setResumeTime(resumeTime);
        realTimeRebecaActorState1.setNow(now);
        ContnuousNonDetInstructionBean contnuousNonDetInstructionBean = new ContnuousNonDetInstructionBean(assignee, delayLowerBound, delayUpperBound);
        Pair<RealTimeRebecaActorState, InstructionBean> delayInput = new Pair<>(realTimeRebecaActorState1, contnuousNonDetInstructionBean);
        RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =  realTimeRebecaDelaySOSRule.applyRule(delayInput);
        RealTimeRebecaNondeterministicTransition hdt = (RealTimeRebecaNondeterministicTransition)result;
        assertTrue(hdt.getDestinations().size() == 2);
    }

}
