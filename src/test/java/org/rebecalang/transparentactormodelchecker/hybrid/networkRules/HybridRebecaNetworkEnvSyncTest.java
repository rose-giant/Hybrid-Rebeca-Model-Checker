package org.rebecalang.transparentactormodelchecker.hybrid.networkRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkEnvSyncSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkTransferSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HybridRebecaNetworkEnvSyncTest {
    HybridRebecaNetworkEnvSyncSOSRule hybridRebecaNetworkTransferSOSRule =
            new HybridRebecaNetworkEnvSyncSOSRule();
    HybridRebecaNetworkState hybridRebecaNetworkState = new HybridRebecaNetworkState();
    HybridRebecaActorState hybridRebecaActorState1 = new HybridRebecaActorState("actor1");
    HybridRebecaActorState hybridRebecaActorState2 = new HybridRebecaActorState("actor2");
    HybridRebecaMessage hybridRebecaMessage = new HybridRebecaMessage();
    ArrayList<HybridRebecaMessage> messagesArrayList = new ArrayList<>();

    @Test
    public void timeProgressApplicableCase() {
        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        hybridRebecaNetworkState.setNow(now);
        Pair<Float, Float> arrivalTime = new Pair<>((float)3, (float)4);
        hybridRebecaMessage.setMessageArrivalInterval(arrivalTime);
        messagesArrayList.add(hybridRebecaMessage);
        HashMap<Pair<String, String>,ArrayList<HybridRebecaMessage>> messages = new HashMap<>();
        Pair<String, String> senderReceiver = new Pair<>(hybridRebecaActorState1.getId(), hybridRebecaActorState2.getId());
        messages.put(senderReceiver, messagesArrayList);
        hybridRebecaNetworkState.setReceivedMessages(messages);

        HybridRebecaDeterministicTransition<HybridRebecaNetworkState> result =
                (HybridRebecaDeterministicTransition<HybridRebecaNetworkState>)
                        hybridRebecaNetworkTransferSOSRule.applyRule(hybridRebecaNetworkState);

        HybridRebecaNetworkState resultState = result.getDestination();
        assertEquals((now.getFirst() + now.getSecond()) / 2, resultState.getNow().getFirst());
        assertEquals(arrivalTime.getFirst() + (float) 0.1, resultState.getNow().getSecond());
    }
}
