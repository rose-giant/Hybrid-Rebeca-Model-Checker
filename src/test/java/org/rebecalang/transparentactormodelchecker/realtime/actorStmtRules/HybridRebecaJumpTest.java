package org.rebecalang.transparentactormodelchecker.realtime.actorStmtRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules.RealTimeRebecaJumpSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaJumpTest {

    RealTimeRebecaJumpSOSRule realTimeRebecaJumpSOSRule = new RealTimeRebecaJumpSOSRule();
    RealTimeRebecaActorState realTimeRebecaActorState = new RealTimeRebecaActorState("actor1");
    Environment environment = new Environment();

    @Test
    public void TrueConditionGoesToIf() {
        realTimeRebecaActorState.setEnvironment(environment);
        realTimeRebecaActorState.addVariableToScope("$PC$", new Pair<>("$PC$", 1));

        Pair<Number, Number> left = new Pair<>(1, 3);
        Float right = (float)4;
        String operator = "<";
        StartUnbreakableConditionInstructionBean startUnbreakableConditionInstructionBean =
                new StartUnbreakableConditionInstructionBean(left, right, operator);
        JumpIfNotInstructionBean jumpIfNotInstructionBean =
                new JumpIfNotInstructionBean(startUnbreakableConditionInstructionBean, "methodName", 5);

        Pair<RealTimeRebecaActorState, InstructionBean> source = new Pair<>();
        source.setFirst(realTimeRebecaActorState);
        source.setSecond(jumpIfNotInstructionBean);

        RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =
                (RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>>) realTimeRebecaJumpSOSRule.applyRule(source);

        Pair<String, Object> pcVal = (Pair<String, Object>) result.getDestination().getFirst().getVariableValue("$PC$");
        assertEquals(jumpIfNotInstructionBean.getLineNumber(), (Number)pcVal.getSecond());
    }

    @Test
    public void TrueConditionGoesToElse() {
        realTimeRebecaActorState.setEnvironment(environment);
        int pcInitVal = 1;
        realTimeRebecaActorState.addVariableToScope("$PC$", new Pair<>("$PC$", pcInitVal));

        Pair<Number, Number> left = new Pair<>(1, 3);
        Float right = (float)0;
        String operator = "<";
        StartUnbreakableConditionInstructionBean startUnbreakableConditionInstructionBean =
                new StartUnbreakableConditionInstructionBean(left, right, operator);
        JumpIfNotInstructionBean jumpIfNotInstructionBean =
                new JumpIfNotInstructionBean(startUnbreakableConditionInstructionBean, "methodName", 5);

        Pair<RealTimeRebecaActorState, InstructionBean> source = new Pair<>();
        source.setFirst(realTimeRebecaActorState);
        source.setSecond(jumpIfNotInstructionBean);

        RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =
                (RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>>) realTimeRebecaJumpSOSRule.applyRule(source);

        Pair<String, Object> pcVal = (Pair<String, Object>) result.getDestination().getFirst().getVariableValue("$PC$");
        assertEquals(pcInitVal + 1, (Number)pcVal.getSecond());
    }

    @Test
    public void TrueConditionGoesToBoth() {
        realTimeRebecaActorState.setEnvironment(environment);
        int pcInitVal = 1;
        realTimeRebecaActorState.addVariableToScope("$PC$", new Pair<>("$PC$", pcInitVal));

        Pair<Number, Number> left = new Pair<>(1, 3);
        Float right = (float)2;
        String operator = "<";
        StartUnbreakableConditionInstructionBean startUnbreakableConditionInstructionBean =
                new StartUnbreakableConditionInstructionBean(left, right, operator);
        JumpIfNotInstructionBean jumpIfNotInstructionBean =
                new JumpIfNotInstructionBean(startUnbreakableConditionInstructionBean, "methodName", 5);

        Pair<RealTimeRebecaActorState, InstructionBean> source = new Pair<>();
        source.setFirst(realTimeRebecaActorState);
        source.setSecond(jumpIfNotInstructionBean);

        RealTimeRebecaNondeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =
                (RealTimeRebecaNondeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>>) realTimeRebecaJumpSOSRule.applyRule(source);

        assertTrue(result.getDestinations().size() == 2);
        RealTimeRebecaActorState ifDestination = result.getDestinations().get(0).getSecond().getFirst();
        RealTimeRebecaActorState elseDestination = result.getDestinations().get(1).getSecond().getFirst();

        Pair<String, Object> elsePC = (Pair<String, Object>) elseDestination.getVariableValue("$PC$");
        assertEquals(pcInitVal + 1, (Number)elsePC.getSecond());

        Pair<String, Object> ifPC = (Pair<String, Object>) ifDestination.getVariableValue("$PC$");
        assertEquals(jumpIfNotInstructionBean.getLineNumber(), (Number)ifPC.getSecond());
    }
}
