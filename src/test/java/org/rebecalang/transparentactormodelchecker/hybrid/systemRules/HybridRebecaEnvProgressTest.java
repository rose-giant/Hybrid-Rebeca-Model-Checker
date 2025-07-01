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
        Pair<Float, Float> interval1 = new Pair<>((float)2, (float)3);
        message1.setMessageArrivalInterval(interval1);
        message1.setSender(actorState1);
        actorState1.receiveMessage(message1);
        systemState.setActorState(actorState1.getId(), actorState1);

        Pair<Float, Float> interval2 = new Pair<>((float)3, (float)4);
        message2.setMessageArrivalInterval(interval2);
        actorState2.receiveMessage(message2);
        message2.setSender(actorState2);
        systemState.setActorState(actorState2.getId(), actorState2);

        Pair<Float, Float> interval3 = new Pair<>((float)3, (float)4.5);
        message3.setMessageArrivalInterval(interval3);
        message3.setSender(actorState1);
        message3.setReceiver(actorState2);
        networkState.addMessage(message3);
        systemState.setNetworkState(networkState);

        Pair<Float, Float> now = new Pair<>((float)0, (float)0.5);
        systemState.setNow(now);

        HybridRebecaDeterministicTransition<HybridRebecaSystemState> result =
                (HybridRebecaDeterministicTransition<HybridRebecaSystemState>)
                        hybridRebecaCompositionLevelEnvProgressSOSRule.applyRule(systemState);

        HybridRebecaSystemState resultState = result.getDestination();
        assertEquals((float) 1, resultState.getNow().getFirst());
        assertEquals((float) 1.5, resultState.getNow().getSecond());

        HybridRebecaDeterministicTransition<HybridRebecaSystemState> result2 =
                (HybridRebecaDeterministicTransition<HybridRebecaSystemState>)
                        hybridRebecaCompositionLevelEnvProgressSOSRule.applyRule(systemState);

        HybridRebecaSystemState resultState2 = result.getDestination();
        assertEquals((float) 1.5, resultState2.getNow().getFirst());
        assertEquals((float) 2, resultState2.getNow().getSecond());
    }
}


