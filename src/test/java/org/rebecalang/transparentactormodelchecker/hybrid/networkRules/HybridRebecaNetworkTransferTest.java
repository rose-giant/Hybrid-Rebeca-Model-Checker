package org.rebecalang.transparentactormodelchecker.hybrid.networkRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkTransferSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaNetworkTransferTest {
    HybridRebecaNetworkTransferSOSRule hybridRebecaNetworkTransferSOSRule =
            new HybridRebecaNetworkTransferSOSRule();
    HybridRebecaNetworkState hybridRebecaNetworkState = new HybridRebecaNetworkState();
    HybridRebecaActorState hybridRebecaActorState1 = new HybridRebecaActorState("actor1");
    HybridRebecaActorState hybridRebecaActorState2 = new HybridRebecaActorState("actor2");
    HybridRebecaMessage hybridRebecaMessage = new HybridRebecaMessage();
    HybridRebecaMessage hybridRebecaMessage2 = new HybridRebecaMessage();
    ArrayList<HybridRebecaMessage> messagesArrayList = new ArrayList<>();
    ArrayList<HybridRebecaMessage> messagesArrayList2 = new ArrayList<>();

    @Test
    public void GivenOneMessageInNetworkBufferAndBothTransferAndPostponeAreApplicable() {
        Pair<Float, Float> now = new Pair<>((float)1, (float)3);
        hybridRebecaNetworkState.setNow(now);

        hybridRebecaMessage.setSender(hybridRebecaActorState1);
        hybridRebecaMessage.setReceiver(hybridRebecaActorState2);
        hybridRebecaMessage.setName("happiness");
        Pair<Float, Float> arrivalTime = new Pair<>((float)1, (float)4);
        hybridRebecaMessage.setMessageArrivalInterval(arrivalTime);
        Pair<Float, Float> arrivalTime2 = new Pair<>((float)2, (float)4);
        hybridRebecaMessage2.setMessageArrivalInterval(arrivalTime2);
        messagesArrayList.add(hybridRebecaMessage);
        messagesArrayList2.add(hybridRebecaMessage2);
        Pair<String, String> senderReceiver = new Pair<>(hybridRebecaActorState1.getId(), hybridRebecaActorState2.getId());
        Pair<String, String> senderReceiver2 = new Pair<>(hybridRebecaActorState2.getId(), hybridRebecaActorState1.getId());

        HashMap<Pair<String, String>,ArrayList<HybridRebecaMessage>> messages = new HashMap<>();
        messages.put(senderReceiver, messagesArrayList);
        messages.put(senderReceiver2, messagesArrayList2);
        hybridRebecaNetworkState.setReceivedMessages(messages);
        HybridRebecaNondeterministicTransition<HybridRebecaNetworkState> result =
                (HybridRebecaNondeterministicTransition<HybridRebecaNetworkState>)
                        hybridRebecaNetworkTransferSOSRule.applyRule(hybridRebecaNetworkState);

        assertTrue(result.getDestinations().size() == 2);
    }

    @Test
    public void GivenOneMessageInNetworkBufferAndTransferIsOnlyApplicable() {
        Pair<Float, Float> now = new Pair<>((float)1, (float)3);
        hybridRebecaNetworkState.setNow(now);

        hybridRebecaMessage.setSender(hybridRebecaActorState1);
        hybridRebecaMessage.setReceiver(hybridRebecaActorState2);
        hybridRebecaMessage.setName("happiness");
        Pair<Float, Float> arrivalTime = new Pair<>((float)1, (float)2);
        hybridRebecaMessage.setMessageArrivalInterval(arrivalTime);
        Pair<Float, Float> arrivalTime2 = new Pair<>((float)2, (float)4);
        hybridRebecaMessage2.setMessageArrivalInterval(arrivalTime2);
        messagesArrayList.add(hybridRebecaMessage);
        messagesArrayList2.add(hybridRebecaMessage2);
        Pair<String, String> senderReceiver = new Pair<>(hybridRebecaActorState1.getId(), hybridRebecaActorState2.getId());
        Pair<String, String> senderReceiver2 = new Pair<>(hybridRebecaActorState2.getId(), hybridRebecaActorState1.getId());

        HashMap<Pair<String, String>,ArrayList<HybridRebecaMessage>> messages = new HashMap<>();
        messages.put(senderReceiver, messagesArrayList);
        messages.put(senderReceiver2, messagesArrayList2);
        hybridRebecaNetworkState.setReceivedMessages(messages);
        HybridRebecaDeterministicTransition<HybridRebecaNetworkState> result =
                (HybridRebecaDeterministicTransition<HybridRebecaNetworkState>)
                        hybridRebecaNetworkTransferSOSRule.applyRule(hybridRebecaNetworkState);
        assertEquals( 1, result.getDestination().getReceivedMessages().size());
    }

    @Test
    public void GivenTwoMessageInNetworkBufferAndBothTransferAndPostponeAreApplicable() {
        Pair<Float, Float> now = new Pair<>((float)1, (float)3);
        hybridRebecaNetworkState.setNow(now);

        hybridRebecaMessage.setSender(hybridRebecaActorState1);
        hybridRebecaMessage.setReceiver(hybridRebecaActorState2);
        hybridRebecaMessage.setName("happiness");
        Pair<Float, Float> arrivalTime = new Pair<>((float)1, (float)4);
        hybridRebecaMessage.setMessageArrivalInterval(arrivalTime);
        Pair<Float, Float> arrivalTime2 = new Pair<>((float)1, (float)4);
        hybridRebecaMessage2.setMessageArrivalInterval(arrivalTime2);
        hybridRebecaMessage2.setSender(hybridRebecaActorState2);
        hybridRebecaMessage2.setReceiver(hybridRebecaActorState1);

        messagesArrayList.add(hybridRebecaMessage);
        messagesArrayList2.add(hybridRebecaMessage2);
        Pair<String, String> senderReceiver = new Pair<>(hybridRebecaActorState1.getId(), hybridRebecaActorState2.getId());
        Pair<String, String> senderReceiver2 = new Pair<>(hybridRebecaActorState2.getId(), hybridRebecaActorState1.getId());

        HashMap<Pair<String, String>,ArrayList<HybridRebecaMessage>> messages = new HashMap<>();
        messages.put(senderReceiver, messagesArrayList);
        messages.put(senderReceiver2, messagesArrayList2);
        hybridRebecaNetworkState.setReceivedMessages(messages);
        HybridRebecaNondeterministicTransition<HybridRebecaNetworkState> result =
                (HybridRebecaNondeterministicTransition<HybridRebecaNetworkState>)
                        hybridRebecaNetworkTransferSOSRule.applyRule(hybridRebecaNetworkState);

        assertTrue(result.getDestinations().size() == 4);
    }
}
