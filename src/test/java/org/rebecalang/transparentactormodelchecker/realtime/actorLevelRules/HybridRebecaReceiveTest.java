package org.rebecalang.transparentactormodelchecker.realtime.actorLevelRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules.RealTimeRebecaReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import static org.junit.jupiter.api.Assertions.*;

public class HybridRebecaReceiveTest {
    RealTimeRebecaReceiveSOSRule realTimeRebecaReceiveSOSRule = new RealTimeRebecaReceiveSOSRule();
    RealTimeRebecaActorState realTimeRebecaActorState1 = new RealTimeRebecaActorState("actor1");
    RealTimeRebecaActorState realTimeRebecaActorState2 = new RealTimeRebecaActorState("actor2");

    @Test
    public void hybridActorStateReceivesMessage () {
        RealTimeRebecaMessage message = new RealTimeRebecaMessage();
        message.setSender(realTimeRebecaActorState1);
        message.setReceiver(realTimeRebecaActorState2);
        MessageAction messageAction = new MessageAction(message);
        RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState> result
                = (RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState>) realTimeRebecaReceiveSOSRule.applyRule(messageAction , realTimeRebecaActorState2);

        assertNotNull(result.getDestination());

        RealTimeRebecaActorState destination = result.getDestination();
        assertFalse(destination.messageQueueIsEmpty());
        assertEquals(realTimeRebecaActorState2.getId() ,destination.getId());
        RealTimeRebecaMessage firstMessage = destination.getFirstMessage();
        assertEquals(firstMessage.getSender().getId(), realTimeRebecaActorState1.getId());
        assertEquals(firstMessage.getReceiver().getId(), realTimeRebecaActorState2.getId());
    }
}
