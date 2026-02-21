package org.rebecalang.transparentactormodelchecker.realtime.networkRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules.RealTimeRebecaNetworkEnvSyncSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HybridRebecaNetworkEnvSyncTest {
    RealTimeRebecaNetworkEnvSyncSOSRule hybridRebecaNetworkTransferSOSRule =
            new RealTimeRebecaNetworkEnvSyncSOSRule();
    RealTimeRebecaNetworkState realTimeRebecaNetworkState = new RealTimeRebecaNetworkState();
    RealTimeRebecaActorState realTimeRebecaActorState1 = new RealTimeRebecaActorState("actor1");
    RealTimeRebecaActorState realTimeRebecaActorState2 = new RealTimeRebecaActorState("actor2");
    RealTimeRebecaMessage realTimeRebecaMessage = new RealTimeRebecaMessage();
    ArrayList<RealTimeRebecaMessage> messagesArrayList = new ArrayList<>();

    @Test
    public void timeProgressApplicableCase() {
        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        realTimeRebecaNetworkState.setNow(now);
        Pair<Float, Float> arrivalTime = new Pair<>((float)3, (float)4);
        realTimeRebecaMessage.setMessageArrivalInterval(arrivalTime);
        messagesArrayList.add(realTimeRebecaMessage);
        HashMap<Pair<String, String>,ArrayList<RealTimeRebecaMessage>> messages = new HashMap<>();
        Pair<String, String> senderReceiver = new Pair<>(realTimeRebecaActorState1.getId(), realTimeRebecaActorState2.getId());
        messages.put(senderReceiver, messagesArrayList);
        realTimeRebecaNetworkState.setReceivedMessages(messages);

        RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState>)
                        hybridRebecaNetworkTransferSOSRule.applyRule(realTimeRebecaNetworkState);

        RealTimeRebecaNetworkState resultState = result.getDestination();
        assertEquals((now.getFirst() + now.getSecond()) / 2, resultState.getNow().getFirst());
        assertEquals(arrivalTime.getFirst() + (float) 0.1, resultState.getNow().getSecond());
    }
}
