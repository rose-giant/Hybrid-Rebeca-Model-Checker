package org.rebecalang.transparentactormodelchecker.realtime.actorLevelRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules.HybridRebecaReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import static org.junit.jupiter.api.Assertions.*;

public class HybridRebecaReceiveTest {
    HybridRebecaReceiveSOSRule hybridRebecaReceiveSOSRule = new HybridRebecaReceiveSOSRule();
    HybridRebecaActorState hybridRebecaActorState1 = new HybridRebecaActorState("actor1");
    HybridRebecaActorState hybridRebecaActorState2 = new HybridRebecaActorState("actor2");

    @Test
    public void hybridActorStateReceivesMessage () {
        HybridRebecaMessage message = new HybridRebecaMessage();
        message.setSender(hybridRebecaActorState1);
        message.setReceiver(hybridRebecaActorState2);
        MessageAction messageAction = new MessageAction(message);
        HybridRebecaDeterministicTransition<HybridRebecaActorState> result
                = (HybridRebecaDeterministicTransition<HybridRebecaActorState>) hybridRebecaReceiveSOSRule.applyRule(messageAction ,hybridRebecaActorState2);

        assertNotNull(result.getDestination());

        HybridRebecaActorState destination = result.getDestination();
        assertFalse(destination.messageQueueIsEmpty());
        assertEquals(hybridRebecaActorState2.getId() ,destination.getId());
        HybridRebecaMessage firstMessage = destination.getFirstMessage();
        assertEquals(firstMessage.getSender().getId(), hybridRebecaActorState1.getId());
        assertEquals(firstMessage.getReceiver().getId(), hybridRebecaActorState2.getId());
    }
}
