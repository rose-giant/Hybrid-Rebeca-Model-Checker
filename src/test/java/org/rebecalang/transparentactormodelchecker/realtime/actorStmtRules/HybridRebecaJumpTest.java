package org.rebecalang.transparentactormodelchecker.realtime.actorStmtRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules.HybridRebecaJumpSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaJumpTest {

    HybridRebecaJumpSOSRule hybridRebecaJumpSOSRule = new HybridRebecaJumpSOSRule();
    HybridRebecaActorState hybridRebecaActorState = new HybridRebecaActorState("actor1");
    Environment environment = new Environment();

    @Test
    public void TrueConditionGoesToIf() {
        hybridRebecaActorState.setEnvironment(environment);
        hybridRebecaActorState.addVariableToScope("$PC$", new Pair<>("$PC$", 1));

        Pair<Number, Number> left = new Pair<>(1, 3);
        Float right = (float)4;
        String operator = "<";
        StartUnbreakableConditionInstructionBean startUnbreakableConditionInstructionBean =
                new StartUnbreakableConditionInstructionBean(left, right, operator);
        JumpIfNotInstructionBean jumpIfNotInstructionBean =
                new JumpIfNotInstructionBean(startUnbreakableConditionInstructionBean, "methodName", 5);

        Pair<HybridRebecaActorState, InstructionBean> source = new Pair<>();
        source.setFirst(hybridRebecaActorState);
        source.setSecond(jumpIfNotInstructionBean);

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                (HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) hybridRebecaJumpSOSRule.applyRule(source);

        Pair<String, Object> pcVal = (Pair<String, Object>) result.getDestination().getFirst().getVariableValue("$PC$");
        assertEquals(jumpIfNotInstructionBean.getLineNumber(), (Number)pcVal.getSecond());
    }

    @Test
    public void TrueConditionGoesToElse() {
        hybridRebecaActorState.setEnvironment(environment);
        int pcInitVal = 1;
        hybridRebecaActorState.addVariableToScope("$PC$", new Pair<>("$PC$", pcInitVal));

        Pair<Number, Number> left = new Pair<>(1, 3);
        Float right = (float)0;
        String operator = "<";
        StartUnbreakableConditionInstructionBean startUnbreakableConditionInstructionBean =
                new StartUnbreakableConditionInstructionBean(left, right, operator);
        JumpIfNotInstructionBean jumpIfNotInstructionBean =
                new JumpIfNotInstructionBean(startUnbreakableConditionInstructionBean, "methodName", 5);

        Pair<HybridRebecaActorState, InstructionBean> source = new Pair<>();
        source.setFirst(hybridRebecaActorState);
        source.setSecond(jumpIfNotInstructionBean);

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                (HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) hybridRebecaJumpSOSRule.applyRule(source);

        Pair<String, Object> pcVal = (Pair<String, Object>) result.getDestination().getFirst().getVariableValue("$PC$");
        assertEquals(pcInitVal + 1, (Number)pcVal.getSecond());
    }

    @Test
    public void TrueConditionGoesToBoth() {
        hybridRebecaActorState.setEnvironment(environment);
        int pcInitVal = 1;
        hybridRebecaActorState.addVariableToScope("$PC$", new Pair<>("$PC$", pcInitVal));

        Pair<Number, Number> left = new Pair<>(1, 3);
        Float right = (float)2;
        String operator = "<";
        StartUnbreakableConditionInstructionBean startUnbreakableConditionInstructionBean =
                new StartUnbreakableConditionInstructionBean(left, right, operator);
        JumpIfNotInstructionBean jumpIfNotInstructionBean =
                new JumpIfNotInstructionBean(startUnbreakableConditionInstructionBean, "methodName", 5);

        Pair<HybridRebecaActorState, InstructionBean> source = new Pair<>();
        source.setFirst(hybridRebecaActorState);
        source.setSecond(jumpIfNotInstructionBean);

        HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                (HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) hybridRebecaJumpSOSRule.applyRule(source);

        assertTrue(result.getDestinations().size() == 2);
        HybridRebecaActorState ifDestination = result.getDestinations().get(0).getSecond().getFirst();
        HybridRebecaActorState elseDestination = result.getDestinations().get(1).getSecond().getFirst();

        Pair<String, Object> elsePC = (Pair<String, Object>) elseDestination.getVariableValue("$PC$");
        assertEquals(pcInitVal + 1, (Number)elsePC.getSecond());

        Pair<String, Object> ifPC = (Pair<String, Object>) ifDestination.getVariableValue("$PC$");
        assertEquals(jumpIfNotInstructionBean.getLineNumber(), (Number)ifPC.getSecond());
    }
}
