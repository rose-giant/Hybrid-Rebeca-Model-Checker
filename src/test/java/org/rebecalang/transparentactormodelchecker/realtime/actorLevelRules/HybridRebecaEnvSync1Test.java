package org.rebecalang.transparentactormodelchecker.realtime.actorLevelRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules.RealTimeRebecaEnvSync1SOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules.RealTimeRebecaEnvSync2SOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaEnvSync1Test {

    RealTimeRebecaActorState realTimeRebecaActorState1 = new RealTimeRebecaActorState("actor1");
    RealTimeRebecaEnvSync2SOSRule envSync2SOSRule = new RealTimeRebecaEnvSync2SOSRule();

    RealTimeRebecaEnvSync1SOSRule envSync1SOSRule = new RealTimeRebecaEnvSync1SOSRule();

    @Test
    public void testEnvSync2Result() {
        realTimeRebecaActorState1.setNow(new Pair<>((float) 1, (float) 3));
        envSync2SOSRule.applyRule(realTimeRebecaActorState1);
    }

    @Test
    public void testEnvSync1ResultCase1() {
        realTimeRebecaActorState1.setNow(new Pair<>((float) 1, (float) 3));
        realTimeRebecaActorState1.setResumeTime(new Pair<>((float) 3, (float) 4));

        RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState>) envSync1SOSRule.applyRule(realTimeRebecaActorState1);

        RealTimeRebecaActorState destination = result.getDestination();
        assertTrue(destination.getNow().getFirst().floatValue() == realTimeRebecaActorState1.getNow().getSecond().floatValue());
        assertTrue(destination.getNow().getSecond().floatValue() == realTimeRebecaActorState1.getResumeTime().getSecond().floatValue());
    }

    @Test
    public void testEnvSync1ResultCase2() {
        realTimeRebecaActorState1.setNow(new Pair<>((float) 1, (float) 2));
        realTimeRebecaActorState1.setResumeTime(new Pair<>((float) 3, (float) 4));

        RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState>) envSync1SOSRule.applyRule(realTimeRebecaActorState1);

        RealTimeRebecaActorState destination = result.getDestination();
        assertTrue(destination.getNow().getFirst().floatValue() == realTimeRebecaActorState1.getNow().getSecond().floatValue());
        assertTrue(destination.getNow().getSecond().floatValue() == realTimeRebecaActorState1.getResumeTime().getFirst().floatValue());
    }

    @Test
    public void testEnvSync1ResultCase3() {
        realTimeRebecaActorState1.setNow(new Pair<>((float) 1, (float) 2));
        realTimeRebecaActorState1.setResumeTime(new Pair<>((float) 2, (float) 4));

        RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState>) envSync1SOSRule.applyRule(realTimeRebecaActorState1);

        RealTimeRebecaActorState destination = result.getDestination();
        assertTrue(destination.getNow().getFirst().floatValue() == realTimeRebecaActorState1.getResumeTime().getFirst().floatValue());
        assertTrue(destination.getNow().getSecond().floatValue() == realTimeRebecaActorState1.getResumeTime().getSecond().floatValue());
    }
}
