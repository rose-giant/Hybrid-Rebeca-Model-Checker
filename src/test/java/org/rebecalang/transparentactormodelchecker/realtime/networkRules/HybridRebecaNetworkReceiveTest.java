package org.rebecalang.transparentactormodelchecker.realtime.networkRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules.HybridRebecaNetworkReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaNetworkReceiveTest {
    HybridRebecaNetworkReceiveSOSRule hybridRebecaNetworkReceiveSOSRule =
            new HybridRebecaNetworkReceiveSOSRule();

    HybridRebecaNetworkState hybridRebecaNetworkState = new HybridRebecaNetworkState();
    HybridRebecaActorState hybridRebecaActorState1 = new HybridRebecaActorState("actor1");
    HybridRebecaActorState hybridRebecaActorState2 = new HybridRebecaActorState("actor2");

    @Test
    public void HybridRebecaNetworkReceivesMessages() {
        HybridRebecaMessage hybridRebecaMessage = new HybridRebecaMessage();
        hybridRebecaMessage.setSender(hybridRebecaActorState1);
        hybridRebecaMessage.setReceiver(hybridRebecaActorState2);
        hybridRebecaMessage.setName("m1");
        MessageAction messageAction = new MessageAction(hybridRebecaMessage);
        HybridRebecaDeterministicTransition<HybridRebecaNetworkState> result =
                (HybridRebecaDeterministicTransition<HybridRebecaNetworkState>) hybridRebecaNetworkReceiveSOSRule.applyRule(messageAction, hybridRebecaNetworkState);

        assertTrue(result.getDestination().getReceivedMessages().size() == 1);
        HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> messages = result.getDestination().getReceivedMessages();
        Pair <String, String> senderReceiverMap = new Pair<>(hybridRebecaMessage.getSender().getId(), hybridRebecaMessage.getReceiver().getId());
        assertEquals(hybridRebecaMessage.getName(), messages.get(senderReceiverMap).get(0).getName());
    }
}


