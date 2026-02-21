package org.rebecalang.transparentactormodelchecker.realtime.systemRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules.RealTimeRebecaCompositionLevelEnvProgressSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HybridRebecaEnvProgressTest {
    RealTimeRebecaCompositionLevelEnvProgressSOSRule realTimeRebecaCompositionLevelEnvProgressSOSRule =
            new RealTimeRebecaCompositionLevelEnvProgressSOSRule();
    RealTimeRebecaSystemState systemState = new RealTimeRebecaSystemState();
    RealTimeRebecaActorState actorState1 = new RealTimeRebecaActorState("actor1");
    RealTimeRebecaActorState actorState2 = new RealTimeRebecaActorState("actor2");
    RealTimeRebecaNetworkState networkState = new RealTimeRebecaNetworkState();

    RealTimeRebecaMessage message1 = new RealTimeRebecaMessage();
    RealTimeRebecaMessage message2 = new RealTimeRebecaMessage();
    RealTimeRebecaMessage message3 = new RealTimeRebecaMessage();

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

        RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState>)
                        realTimeRebecaCompositionLevelEnvProgressSOSRule.applyRule(systemState);

        RealTimeRebecaSystemState resultState = result.getDestination();
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

        RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState>)
                        realTimeRebecaCompositionLevelEnvProgressSOSRule.applyRule(systemState);

        RealTimeRebecaSystemState resultState = result.getDestination();
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

        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        systemState.setNow(now);

        RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState>)
                        realTimeRebecaCompositionLevelEnvProgressSOSRule.applyRule(systemState);

        RealTimeRebecaSystemState resultState = result.getDestination();
        assertEquals((float) 1.5, resultState.getNow().getFirst().floatValue());
        assertEquals((float) 2, resultState.getNow().getSecond().floatValue());
    }

    @Test
    public void test4() {
        Pair<Float, Float> interval1 = new Pair<>((float)1.5, (float)2);
        actorState1.setResumeTime(interval1);
        actorState1.setNow(new Pair<>(1f, 2f));

        message1.setMessageArrivalInterval(interval1);
        message1.setSender(actorState1);
        message1.setReceiver(actorState1);
        actorState1.receiveMessage(message1);
        systemState.setActorState(actorState1.getId(), actorState1);

        Pair<Float, Float> interval2 = new Pair<>((float)2, (float)2.8);
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

        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        systemState.setNow(now);

        RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState>)
                        realTimeRebecaCompositionLevelEnvProgressSOSRule.applyRule(systemState);

        RealTimeRebecaSystemState resultState = result.getDestination();
        assertEquals((float) 1.5, resultState.getNow().getFirst().floatValue());
        assertEquals((float) 2, resultState.getNow().getSecond().floatValue());
    }

    @Test
    public void test5() {
        Pair<Float, Float> interval1 = new Pair<>((float)1.5, (float)2);
        actorState1.setResumeTime(interval1);
        actorState1.setNow(new Pair<>(1f, 2f));

        message1.setMessageArrivalInterval(interval1);
        message1.setSender(actorState1);
        message1.setReceiver(actorState1);
//        actorState1.receiveMessage(message1);
        systemState.setActorState(actorState1.getId(), actorState1);

        Pair<Float, Float> interval2 = new Pair<>((float)2, (float)2.8);
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

        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        systemState.setNow(now);

        RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaSystemState>)
                        realTimeRebecaCompositionLevelEnvProgressSOSRule.applyRule(systemState);

        RealTimeRebecaSystemState resultState = result.getDestination();
        assertEquals((float) 2, resultState.getNow().getFirst().floatValue());
        assertEquals((float) 2.5, resultState.getNow().getSecond().floatValue());
    }
}


