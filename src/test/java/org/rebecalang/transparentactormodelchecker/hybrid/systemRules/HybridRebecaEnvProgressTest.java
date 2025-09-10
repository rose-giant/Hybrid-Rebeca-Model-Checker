package org.rebecalang.transparentactormodelchecker.hybrid.systemRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules.HybridRebecaCompositionLevelEnvProgressSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HybridRebecaEnvProgressTest {
    HybridRebecaCompositionLevelEnvProgressSOSRule hybridRebecaCompositionLevelEnvProgressSOSRule =
            new HybridRebecaCompositionLevelEnvProgressSOSRule();
    HybridRebecaSystemState systemState = new HybridRebecaSystemState();
    HybridRebecaActorState actorState1 = new HybridRebecaActorState("actor1");
    HybridRebecaActorState actorState2 = new HybridRebecaActorState("actor2");
    HybridRebecaNetworkState networkState = new HybridRebecaNetworkState();

    HybridRebecaMessage message1 = new HybridRebecaMessage();
    HybridRebecaMessage message2 = new HybridRebecaMessage();
    HybridRebecaMessage message3 = new HybridRebecaMessage();

    @Test
    public void test1() {
        Pair<Float, Float> interval1 = new Pair<>((float)1, (float)2);
        actorState1.setResumeTime(interval1);
        actorState1.setNow(new Pair<>(0f, 0.5f));

        message1.setMessageArrivalInterval(interval1);
        message1.setSender(actorState1);
        message1.setReceiver(actorState1);
        actorState1.receiveMessage(message1);
        systemState.setActorState(actorState1.getId(), actorState1);

        Pair<Float, Float> interval2 = new Pair<>((float)1, (float)2);
        message2.setMessageArrivalInterval(interval2);
        message2.setSender(actorState2);
        message2.setReceiver(actorState2);
        networkState.addMessage(message2);

        Pair<Float, Float> interval3 = new Pair<>((float)3, (float)4);
        message3.setMessageArrivalInterval(interval3);
        message3.setSender(actorState1);
        message3.setReceiver(actorState2);
        networkState.addMessage(message3);
        networkState.setNow(new Pair<>(0f, 0.5f));
        systemState.setNetworkState(networkState);

        Pair<Float, Float> now = new Pair<>((float)0, (float)0.5);
        systemState.setNow(now);

        HybridRebecaDeterministicTransition<HybridRebecaSystemState> result =
                (HybridRebecaDeterministicTransition<HybridRebecaSystemState>)
                        hybridRebecaCompositionLevelEnvProgressSOSRule.applyRule(systemState);

        HybridRebecaSystemState resultState = result.getDestination();
        assertEquals((float) 0.5, resultState.getNow().getFirst().floatValue());
        assertEquals((float) 1, resultState.getNow().getSecond().floatValue());
    }

    @Test
    public void test2() {
        Pair<Float, Float> interval1 = new Pair<>((float)0.6, (float)2);
        actorState1.setResumeTime(interval1);
        actorState1.setNow(new Pair<>(0.5f, 1f));

        message1.setMessageArrivalInterval(interval1);
        message1.setSender(actorState1);
        message1.setReceiver(actorState1);
        actorState1.receiveMessage(message1);
        systemState.setActorState(actorState1.getId(), actorState1);

        Pair<Float, Float> interval2 = new Pair<>((float)0.6, (float)0.8);
        message2.setMessageArrivalInterval(interval2);
        message2.setSender(actorState2);
        message2.setReceiver(actorState2);
        networkState.addMessage(message2);

        Pair<Float, Float> interval3 = new Pair<>((float)0.7, (float)4);
        message3.setMessageArrivalInterval(interval3);
        message3.setSender(actorState1);
        message3.setReceiver(actorState2);
        networkState.addMessage(message3);
        networkState.setNow(new Pair<>(0.5f, 1f));
        systemState.setNetworkState(networkState);

        Pair<Float, Float> now = new Pair<>((float)0.5, (float)1);
        systemState.setNow(now);

        HybridRebecaDeterministicTransition<HybridRebecaSystemState> result =
                (HybridRebecaDeterministicTransition<HybridRebecaSystemState>)
                        hybridRebecaCompositionLevelEnvProgressSOSRule.applyRule(systemState);

        HybridRebecaSystemState resultState = result.getDestination();
        assertEquals((float) 0.6, resultState.getNow().getFirst().floatValue());
        assertEquals((float) 1, resultState.getNow().getSecond().floatValue());
    }

    @Test
    public void test3() {
        Pair<Float, Float> interval1 = new Pair<>((float)1.5, (float)2);
        actorState1.setResumeTime(interval1);
        actorState1.setNow(new Pair<>(1f, 2f));

        message1.setMessageArrivalInterval(interval1);
        message1.setSender(actorState1);
        message1.setReceiver(actorState1);
        actorState1.receiveMessage(message1);
        systemState.setActorState(actorState1.getId(), actorState1);

        Pair<Float, Float> interval2 = new Pair<>((float)1.5, (float)2.8);
        message2.setMessageArrivalInterval(interval2);
        message2.setSender(actorState2);
        message2.setReceiver(actorState2);
        networkState.addMessage(message2);

        Pair<Float, Float> interval3 = new Pair<>((float)2.5, (float)4);
        message3.setMessageArrivalInterval(interval3);
        message3.setSender(actorState1);
        message3.setReceiver(actorState2);
        networkState.addMessage(message3);
        networkState.setNow(new Pair<>(1f, 2f));
        systemState.setNetworkState(networkState);

        Pair<Float, Float> now = new Pair<>((float)0.5, (float)1);
        systemState.setNow(now);

        HybridRebecaDeterministicTransition<HybridRebecaSystemState> result =
                (HybridRebecaDeterministicTransition<HybridRebecaSystemState>)
                        hybridRebecaCompositionLevelEnvProgressSOSRule.applyRule(systemState);

        HybridRebecaSystemState resultState = result.getDestination();
        assertEquals((float) 1.5, resultState.getNow().getFirst().floatValue());
        assertEquals((float) 2, resultState.getNow().getSecond().floatValue());
    }
}


