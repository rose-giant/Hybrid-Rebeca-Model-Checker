package org.rebecalang.transparentactormodelchecker.realtime.networkRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules.RealTimeRebecaNetworkTransferSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaNetworkTransferTest {
    RealTimeRebecaNetworkTransferSOSRule realTimeRebecaNetworkTransferSOSRule =
            new RealTimeRebecaNetworkTransferSOSRule();
    RealTimeRebecaNetworkState realTimeRebecaNetworkState = new RealTimeRebecaNetworkState();
    RealTimeRebecaActorState realTimeRebecaActorState1 = new RealTimeRebecaActorState("actor1");
    RealTimeRebecaActorState realTimeRebecaActorState2 = new RealTimeRebecaActorState("actor2");
    RealTimeRebecaMessage realTimeRebecaMessage = new RealTimeRebecaMessage();
    RealTimeRebecaMessage realTimeRebecaMessage2 = new RealTimeRebecaMessage();
    RealTimeRebecaMessage realTimeRebecaMessage3 = new RealTimeRebecaMessage();
    ArrayList<RealTimeRebecaMessage> messagesArrayList = new ArrayList<>();
    ArrayList<RealTimeRebecaMessage> messagesArrayList2 = new ArrayList<>();

    @Test
    public void GivenOneMessageInNetworkBufferAndBothTransferAndPostponeAreApplicable() {
        Pair<Float, Float> now = new Pair<>((float)1, (float)4);
        realTimeRebecaNetworkState.setNow(now);

        realTimeRebecaMessage.setSender(realTimeRebecaActorState1);
        realTimeRebecaMessage.setReceiver(realTimeRebecaActorState2);
        realTimeRebecaMessage.setName("happiness");
        Pair<Float, Float> arrivalTime = new Pair<>((float)1, (float)5);
        realTimeRebecaMessage.setMessageArrivalInterval(arrivalTime);
        Pair<Float, Float> arrivalTime2 = new Pair<>((float)2, (float)4);
//        Pair<Float, Float> arrivalTime3 = new Pair<>((float)12, (float)14);
        realTimeRebecaMessage2.setMessageArrivalInterval(arrivalTime2);
//        hybridRebecaMessage3.setMessageArrivalInterval(arrivalTime3);
        messagesArrayList.add(realTimeRebecaMessage);
        messagesArrayList2.add(realTimeRebecaMessage2);
//        messagesArrayList2.add(hybridRebecaMessage3);
        Pair<String, String> senderReceiver = new Pair<>(realTimeRebecaActorState1.getId(), realTimeRebecaActorState2.getId());
        Pair<String, String> senderReceiver2 = new Pair<>(realTimeRebecaActorState2.getId(), realTimeRebecaActorState1.getId());

        HashMap<Pair<String, String>,ArrayList<RealTimeRebecaMessage>> messages = new HashMap<>();
        messages.put(senderReceiver, messagesArrayList);
        messages.put(senderReceiver2, messagesArrayList2);
        realTimeRebecaNetworkState.setReceivedMessages(messages);
        RealTimeRebecaNondeterministicTransition<RealTimeRebecaNetworkState> result =
                (RealTimeRebecaNondeterministicTransition<RealTimeRebecaNetworkState>)
                        realTimeRebecaNetworkTransferSOSRule.applyRule(realTimeRebecaNetworkState);

        assertTrue(result.getDestinations().size() == 3);
    }

    @Test
    public void GivenOneMessageInNetworkBufferAndTransferIsOnlyApplicable() {
        Pair<Float, Float> now = new Pair<>((float)1, (float)3);
        realTimeRebecaNetworkState.setNow(now);

        realTimeRebecaMessage.setSender(realTimeRebecaActorState1);
        realTimeRebecaMessage.setReceiver(realTimeRebecaActorState2);
        realTimeRebecaMessage.setName("happiness");
        Pair<Float, Float> arrivalTime = new Pair<>((float)1, (float)2);
        realTimeRebecaMessage.setMessageArrivalInterval(arrivalTime);
        Pair<Float, Float> arrivalTime2 = new Pair<>((float)1, (float)4);
        realTimeRebecaMessage2.setMessageArrivalInterval(arrivalTime2);
        messagesArrayList.add(realTimeRebecaMessage);
        messagesArrayList2.add(realTimeRebecaMessage2);
        Pair<String, String> senderReceiver = new Pair<>(realTimeRebecaActorState1.getId(), realTimeRebecaActorState2.getId());
        Pair<String, String> senderReceiver2 = new Pair<>(realTimeRebecaActorState2.getId(), realTimeRebecaActorState1.getId());

        HashMap<Pair<String, String>,ArrayList<RealTimeRebecaMessage>> messages = new HashMap<>();
        messages.put(senderReceiver, messagesArrayList);
        messages.put(senderReceiver2, messagesArrayList2);
        realTimeRebecaNetworkState.setReceivedMessages(messages);
        RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState>)
                        realTimeRebecaNetworkTransferSOSRule.applyRule(realTimeRebecaNetworkState);
        assertEquals( 1, result.getDestination().getReceivedMessages().size());
    }

    @Test
    public void GivenTwoMessageInNetworkBufferAndBothTransferAndPostponeAreApplicable() {
        Pair<Float, Float> now = new Pair<>((float)1, (float)3);
        realTimeRebecaNetworkState.setNow(now);

        realTimeRebecaMessage.setSender(realTimeRebecaActorState1);
        realTimeRebecaMessage.setReceiver(realTimeRebecaActorState2);
        realTimeRebecaMessage.setName("happiness");
        Pair<Float, Float> arrivalTime = new Pair<>((float)1, (float)4);
        realTimeRebecaMessage.setMessageArrivalInterval(arrivalTime);
        Pair<Float, Float> arrivalTime2 = new Pair<>((float)1, (float)4);
        realTimeRebecaMessage2.setMessageArrivalInterval(arrivalTime2);
        realTimeRebecaMessage2.setSender(realTimeRebecaActorState2);
        realTimeRebecaMessage2.setReceiver(realTimeRebecaActorState1);

        messagesArrayList.add(realTimeRebecaMessage);
        messagesArrayList2.add(realTimeRebecaMessage2);
        Pair<String, String> senderReceiver = new Pair<>(realTimeRebecaActorState1.getId(), realTimeRebecaActorState2.getId());
        Pair<String, String> senderReceiver2 = new Pair<>(realTimeRebecaActorState2.getId(), realTimeRebecaActorState1.getId());

        HashMap<Pair<String, String>,ArrayList<RealTimeRebecaMessage>> messages = new HashMap<>();
        messages.put(senderReceiver, messagesArrayList);
        messages.put(senderReceiver2, messagesArrayList2);
        realTimeRebecaNetworkState.setReceivedMessages(messages);
        RealTimeRebecaNondeterministicTransition<RealTimeRebecaNetworkState> result =
                (RealTimeRebecaNondeterministicTransition<RealTimeRebecaNetworkState>)
                        realTimeRebecaNetworkTransferSOSRule.applyRule(realTimeRebecaNetworkState);

        assertTrue(result.getDestinations().size() == 4);
    }
}
