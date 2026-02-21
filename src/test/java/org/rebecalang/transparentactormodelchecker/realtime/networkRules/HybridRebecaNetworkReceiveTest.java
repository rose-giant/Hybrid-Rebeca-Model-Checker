package org.rebecalang.transparentactormodelchecker.realtime.networkRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules.RealTimeRebecaNetworkReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaNetworkReceiveTest {
    RealTimeRebecaNetworkReceiveSOSRule realTimeRebecaNetworkReceiveSOSRule =
            new RealTimeRebecaNetworkReceiveSOSRule();

    RealTimeRebecaNetworkState realTimeRebecaNetworkState = new RealTimeRebecaNetworkState();
    RealTimeRebecaActorState realTimeRebecaActorState1 = new RealTimeRebecaActorState("actor1");
    RealTimeRebecaActorState realTimeRebecaActorState2 = new RealTimeRebecaActorState("actor2");

    @Test
    public void HybridRebecaNetworkReceivesMessages() {
        RealTimeRebecaMessage realTimeRebecaMessage = new RealTimeRebecaMessage();
        realTimeRebecaMessage.setSender(realTimeRebecaActorState1);
        realTimeRebecaMessage.setReceiver(realTimeRebecaActorState2);
        realTimeRebecaMessage.setName("m1");
        MessageAction messageAction = new MessageAction(realTimeRebecaMessage);
        RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState> result =
                (RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState>) realTimeRebecaNetworkReceiveSOSRule.applyRule(messageAction, realTimeRebecaNetworkState);

        assertTrue(result.getDestination().getReceivedMessages().size() == 1);
        HashMap<Pair<String, String>, ArrayList<RealTimeRebecaMessage>> messages = result.getDestination().getReceivedMessages();
        Pair <String, String> senderReceiverMap = new Pair<>(realTimeRebecaMessage.getSender().getId(), realTimeRebecaMessage.getReceiver().getId());
        assertEquals(realTimeRebecaMessage.getName(), messages.get(senderReceiverMap).get(0).getName());
    }
}


