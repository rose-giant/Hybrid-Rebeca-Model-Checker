package org.rebecalang.transparentactormodelchecker.hybrid.actorLevelRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaEnvSync1SOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaEnvSync2SOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaEnvSync1Test {

    HybridRebecaActorState hybridRebecaActorState1 = new HybridRebecaActorState("actor1");
    HybridRebecaEnvSync2SOSRule envSync2SOSRule = new HybridRebecaEnvSync2SOSRule();

    HybridRebecaEnvSync1SOSRule envSync1SOSRule = new HybridRebecaEnvSync1SOSRule();

    @Test
    public void testEnvSync2Result() {
        hybridRebecaActorState1.setNow(new Pair<>((float) 1, (float) 3));
        envSync2SOSRule.applyRule(hybridRebecaActorState1);
    }

    @Test
    public void testEnvSync1ResultCase1() {
        hybridRebecaActorState1.setNow(new Pair<>((float) 1, (float) 3));
        hybridRebecaActorState1.setResumeTime(new Pair<>((float) 2, (float) 4));

        HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                (HybridRebecaDeterministicTransition<HybridRebecaActorState>) envSync1SOSRule.applyRule(hybridRebecaActorState1);

        HybridRebecaActorState destination = result.getDestination();
        assertTrue(destination.getNow().getFirst().floatValue() == hybridRebecaActorState1.getResumeTime().getFirst().floatValue());
        assertTrue(destination.getNow().getSecond().floatValue() == hybridRebecaActorState1.getResumeTime().getSecond().floatValue());
    }

    @Test
    public void testEnvSync1ResultCase2() {
        hybridRebecaActorState1.setNow(new Pair<>((float) 1, (float) 2));
        hybridRebecaActorState1.setResumeTime(new Pair<>((float) 3, (float) 4));

        HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                (HybridRebecaDeterministicTransition<HybridRebecaActorState>) envSync1SOSRule.applyRule(hybridRebecaActorState1);

        HybridRebecaActorState destination = result.getDestination();
        assertTrue(destination.getNow().getFirst().floatValue() == hybridRebecaActorState1.getNow().getSecond().floatValue());
        assertTrue(destination.getNow().getSecond().floatValue() == hybridRebecaActorState1.getResumeTime().getFirst().floatValue());
    }

    @Test
    public void testEnvSync1ResultCase3() {
        hybridRebecaActorState1.setNow(new Pair<>((float) 1, (float) 2));
        hybridRebecaActorState1.setResumeTime(new Pair<>((float) 2, (float) 4));

        HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                (HybridRebecaDeterministicTransition<HybridRebecaActorState>) envSync1SOSRule.applyRule(hybridRebecaActorState1);

        HybridRebecaActorState destination = result.getDestination();
        assertTrue(destination.getNow().getFirst().floatValue() == hybridRebecaActorState1.getResumeTime().getFirst().floatValue());
        assertTrue(destination.getNow().getSecond().floatValue() == hybridRebecaActorState1.getResumeTime().getSecond().floatValue());
    }
}
