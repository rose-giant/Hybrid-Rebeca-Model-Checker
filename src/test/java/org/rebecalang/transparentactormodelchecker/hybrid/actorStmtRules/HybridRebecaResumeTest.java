package org.rebecalang.transparentactormodelchecker.hybrid.actorStmtRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaResumeSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaResumeTest {
    HybridRebecaResumeSOSRule hybridRebecaResumeSOSRule = new HybridRebecaResumeSOSRule();
    HybridRebecaActorState hybridRebecaActorState = new HybridRebecaActorState("actor1");

    @Test
    public void NonDetResumePostponeCase() {
        Pair<Float, Float> now = new Pair<>((float) 1, (float)3);
        Pair<Float, Float> resumeTime = new Pair<>((float) 1, (float)4);
        hybridRebecaActorState.setNow(now);
        hybridRebecaActorState.setResumeTime(resumeTime);
        Pair<HybridRebecaActorState, InstructionBean> source = new Pair<>(hybridRebecaActorState, null);
        HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                (HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) hybridRebecaResumeSOSRule.applyRule(source);
        assertTrue(result.getDestinations().size() == 2);

        HybridRebecaActorState resumeDestination = result.getDestinations().get(0).getSecond().getFirst();
        assertEquals(hybridRebecaActorState.getNow().getFirst(), resumeDestination.getResumeTime().getFirst());
        assertEquals(hybridRebecaActorState.getNow().getSecond(), resumeDestination.getResumeTime().getSecond());

        HybridRebecaActorState postponeDestination = result.getDestinations().get(1).getSecond().getFirst();
        assertEquals(hybridRebecaActorState.getNow().getSecond(), postponeDestination.getResumeTime().getFirst());
        assertEquals(hybridRebecaActorState.getResumeTime().getSecond(), postponeDestination.getResumeTime().getSecond());
    }

    @Test
    public void NonDetOnlyResumeCase() {
        Pair<Float, Float> now = new Pair<>((float) 1, (float)3);
        Pair<Float, Float> resumeTime = new Pair<>((float) 1, (float)2);
        hybridRebecaActorState.setNow(now);
        hybridRebecaActorState.setResumeTime(resumeTime);
        Pair<HybridRebecaActorState, InstructionBean> source = new Pair<>(hybridRebecaActorState, null);
        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                (HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) hybridRebecaResumeSOSRule.applyRule(source);

        HybridRebecaActorState resumeDestination = result.getDestination().getFirst();
        assertEquals(hybridRebecaActorState.getNow().getFirst(), resumeDestination.getResumeTime().getFirst());
        assertEquals(hybridRebecaActorState.getNow().getSecond(), resumeDestination.getResumeTime().getSecond());
    }

}
