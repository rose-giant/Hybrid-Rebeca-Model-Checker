package org.rebecalang.transparentactormodelchecker.realtime.actorStmtRules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules.RealTimeRebecaAssignmentSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaAssignmentTest {
    RealTimeRebecaAssignmentSOSRule assignmentSOSRule = new RealTimeRebecaAssignmentSOSRule();
    RealTimeRebecaActorState actorState = new RealTimeRebecaActorState("actor1");
    Object left = new Variable("a");
    // Object leftVarName, Object firstOperand, Object secondOperand, String operator
//    AssignmentInstructionBean instructionBean = new AssignmentInstructionBean();

    @BeforeEach
    public void init() {
        int pcInitVal = 1;
        actorState.addVariableToScope("$PC$", new Pair<>("$PC$", pcInitVal));
    }

    @Test
    public void nonDetCase() {
        NonDetValue rightSide = new NonDetValue();
        LinkedList<Object> values = new LinkedList<>();
        Number n1 = 1;
        values.add(n1);
        Number n2 = 2;
        values.add(n2);
        rightSide.setNondetValues(values);
        actorState.addVariableToScope("a", new Pair<>("a", 0));
        AssignmentInstructionBean instructionBean = new AssignmentInstructionBean(left, rightSide, 1, "=");
        Pair<RealTimeRebecaActorState, InstructionBean> input = new Pair<>(actorState, instructionBean);
        RealTimeRebecaNondeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =
                (RealTimeRebecaNondeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>>) assignmentSOSRule.applyRule(input);
        assertTrue(result.getDestinations().size() == 2);

    }
}
