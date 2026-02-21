package org.rebecalang.transparentactormodelchecker.realtime.actorStmtRules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.MsgsrvCallWithAfterInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules.RealTimeRebecaSendMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaSendMessageTest {
    RealTimeRebecaSendMessageSOSRule realTimeRebecaSendMessageSOSRule = new RealTimeRebecaSendMessageSOSRule();
    RealTimeRebecaActorState realTimeRebecaActorState1 = new RealTimeRebecaActorState("actor1");
    RealTimeRebecaActorState realTimeRebecaActorState2 = new RealTimeRebecaActorState("actor2");
    Environment environment = new Environment();

    @BeforeEach
    public void init() {
        int pcInitVal = 1;
        realTimeRebecaActorState1.addVariableToScope("$PC$", new Pair<>("$PC$", pcInitVal));
    }

    @Test
    public void hybridActorStateSendsAMessageWithoutAfter() {
        Pair<RealTimeRebecaActorState, InstructionBean> source = new Pair<>();
        Variable variable = new Variable(realTimeRebecaActorState2.getId());
        realTimeRebecaActorState1.addVariableToScope(variable.getVarName(), realTimeRebecaActorState2);
        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        realTimeRebecaActorState1.setNow(now);
        TreeMap<String, Object> stringObjectTreeMap = new TreeMap<>();
        MsgsrvCallWithAfterInstructionBean sendMessageWithAfterInstructionBean =
                new MsgsrvCallWithAfterInstructionBean(variable, "m1", stringObjectTreeMap, null);
        source.setFirst(realTimeRebecaActorState1);
        source.setSecond(sendMessageWithAfterInstructionBean);

        RealTimeRebecaDeterministicTransition result = (RealTimeRebecaDeterministicTransition) realTimeRebecaSendMessageSOSRule.applyRule(source);
        assertTrue( result.getAction().getClass() == MessageAction.class);
        MessageAction messageAction = (MessageAction) result.getAction();
        assertEquals(sendMessageWithAfterInstructionBean.getMethodName(), messageAction.getMessage().getName());
        assertEquals(sendMessageWithAfterInstructionBean.getBase().getVarName(), messageAction.getMessage().getReceiver().getId());
        assertEquals(realTimeRebecaActorState1.getId(), messageAction.getMessage().getSender().getId());
        assertEquals(realTimeRebecaActorState1.getNow().getFirst() ,messageAction.getMessage().getMessageArrivalInterval().getFirst());
        assertEquals(realTimeRebecaActorState1.getNow().getSecond() ,messageAction.getMessage().getMessageArrivalInterval().getSecond());
    }

    @Test
    public void hybridActorStateSendsAMessageWithAfter() {
        Pair<RealTimeRebecaActorState, InstructionBean> source = new Pair<>();
        Variable variable = new Variable(realTimeRebecaActorState2.getId());
        realTimeRebecaActorState1.addVariableToScope(variable.getVarName(), realTimeRebecaActorState2);
        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        realTimeRebecaActorState1.setNow(now);
        TreeMap<String, Object> stringObjectTreeMap = new TreeMap<>();
        Pair<Object, Object> after = new Pair<>((float)0.1, (float)0.2);
        MsgsrvCallWithAfterInstructionBean sendMessageWithAfterInstructionBean =
                new MsgsrvCallWithAfterInstructionBean(variable, "m1", stringObjectTreeMap, after);
        source.setFirst(realTimeRebecaActorState1);
        source.setSecond(sendMessageWithAfterInstructionBean);

        RealTimeRebecaDeterministicTransition result = (RealTimeRebecaDeterministicTransition) realTimeRebecaSendMessageSOSRule.applyRule(source);
        assertTrue( result.getAction().getClass() == MessageAction.class);
        MessageAction messageAction = (MessageAction) result.getAction();
        assertEquals(sendMessageWithAfterInstructionBean.getMethodName(), messageAction.getMessage().getName());
        assertEquals(sendMessageWithAfterInstructionBean.getBase().getVarName(), messageAction.getMessage().getReceiver().getId());
        assertEquals(realTimeRebecaActorState1.getId(), messageAction.getMessage().getSender().getId());

//        assertEquals(hybridRebecaActorState1.getNow().getFirst().floatValue() + after.getFirst() ,
//                messageAction.getMessage().getMessageArrivalInterval().getFirst().floatValue());
//
//        assertEquals(hybridRebecaActorState1.getNow().getSecond().floatValue() + after.getSecond() ,
//                messageAction.getMessage().getMessageArrivalInterval().getSecond());
    }
}
