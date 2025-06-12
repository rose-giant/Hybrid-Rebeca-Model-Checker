package org.rebecalang.transparentactormodelchecker.hybrid.actorStmtRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.SendMessageWithAfterInstructionBean;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules.HybridRebecaSendMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaAbstractState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaSendMessageTest {
    HybridRebecaSendMessageSOSRule hybridRebecaSendMessageSOSRule = new HybridRebecaSendMessageSOSRule();
    HybridRebecaActorState hybridRebecaActorState1 = new HybridRebecaActorState("actor1");
    HybridRebecaActorState hybridRebecaActorState2 = new HybridRebecaActorState("actor2");
    Environment environment = new Environment();

    @Test
    public void hybridActorStateSendsAMessageWithoutAfter() {
        Pair<HybridRebecaActorState, InstructionBean> source = new Pair<>();
        Variable variable = new Variable(hybridRebecaActorState2.getId());
        hybridRebecaActorState1.addVariableToScope(variable.getVarName(), hybridRebecaActorState2);
//        hybridRebecaActorState1.addVariableToScope("$PC$", 1);
        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        hybridRebecaActorState1.setNow(now);
        TreeMap<String, Object> stringObjectTreeMap = new TreeMap<>();
        SendMessageWithAfterInstructionBean sendMessageWithAfterInstructionBean =
                new SendMessageWithAfterInstructionBean(variable, "m1", stringObjectTreeMap, null);
        source.setFirst(hybridRebecaActorState1);
        source.setSecond(sendMessageWithAfterInstructionBean);

        HybridRebecaDeterministicTransition result = (HybridRebecaDeterministicTransition) hybridRebecaSendMessageSOSRule.applyRule(source);
        assertTrue( result.getAction().getClass() == MessageAction.class);
        MessageAction messageAction = (MessageAction) result.getAction();
        assertEquals(sendMessageWithAfterInstructionBean.getMethodName(), messageAction.getMessage().getName());
        assertEquals(sendMessageWithAfterInstructionBean.getBase().getVarName(), messageAction.getMessage().getReceiver().getId());
        assertEquals(hybridRebecaActorState1.getId(), messageAction.getMessage().getSender().getId());
        assertEquals(hybridRebecaActorState1.getNow().getFirst() ,messageAction.getMessage().getMessageArrivalInterval().getFirst());
        assertEquals(hybridRebecaActorState1.getNow().getSecond() ,messageAction.getMessage().getMessageArrivalInterval().getSecond());
    }

    @Test
    public void hybridActorStateSendsAMessageWithAfter() {
        Pair<HybridRebecaActorState, InstructionBean> source = new Pair<>();
        Variable variable = new Variable(hybridRebecaActorState2.getId());
        hybridRebecaActorState1.addVariableToScope(variable.getVarName(), hybridRebecaActorState2);
        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        hybridRebecaActorState1.setNow(now);
        TreeMap<String, Object> stringObjectTreeMap = new TreeMap<>();
        Pair<Float, Float> after = new Pair<>((float)0.1, (float)0.2);
        SendMessageWithAfterInstructionBean sendMessageWithAfterInstructionBean =
                new SendMessageWithAfterInstructionBean(variable, "m1", stringObjectTreeMap, after);
        source.setFirst(hybridRebecaActorState1);
        source.setSecond(sendMessageWithAfterInstructionBean);

        HybridRebecaDeterministicTransition result = (HybridRebecaDeterministicTransition) hybridRebecaSendMessageSOSRule.applyRule(source);
        assertTrue( result.getAction().getClass() == MessageAction.class);
        MessageAction messageAction = (MessageAction) result.getAction();
        assertEquals(sendMessageWithAfterInstructionBean.getMethodName(), messageAction.getMessage().getName());
        assertEquals(sendMessageWithAfterInstructionBean.getBase().getVarName(), messageAction.getMessage().getReceiver().getId());
        assertEquals(hybridRebecaActorState1.getId(), messageAction.getMessage().getSender().getId());

        assertEquals(hybridRebecaActorState1.getNow().getFirst() + after.getFirst() ,
                messageAction.getMessage().getMessageArrivalInterval().getFirst());

        assertEquals(hybridRebecaActorState1.getNow().getSecond() + after.getSecond() ,
                messageAction.getMessage().getMessageArrivalInterval().getSecond());
    }
}
