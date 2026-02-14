package org.rebecalang.transparentactormodelchecker.realtime.actorStmtRules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules.HybridRebecaAssignmentSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaAssignmentTest {
    HybridRebecaAssignmentSOSRule assignmentSOSRule = new HybridRebecaAssignmentSOSRule();
    HybridRebecaActorState actorState = new HybridRebecaActorState("actor1");
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
        Pair<HybridRebecaActorState, InstructionBean> input = new Pair<>(actorState, instructionBean);
        HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                (HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) assignmentSOSRule.applyRule(input);
        assertTrue(result.getDestinations().size() == 2);

    }
}
