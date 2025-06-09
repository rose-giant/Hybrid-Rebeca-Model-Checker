package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;

public class HybridRebecaIntervalDelaySOSRule {

//    public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> applyRule(
//            Pair<CoreRebecaActorState, InstructionBean> source) {
//        AssignmentInstructionBean aib = (Delay) source.getSecond();
//        Object valueFirst = getValue(aib.getFirstOperand(), source.getFirst());
//        Object valueSecond = getValue(aib.getSecondOperand(), source.getFirst());
//        String operator = aib.getOperator();
//        Object rightSideResult = valueFirst;
//        if (operator != null) {
//            if (valueFirst instanceof CoreRebecaActorState) {
//                if (operator.equals("=="))
//                    rightSideResult = (((CoreRebecaActorState) valueFirst).getId()
//                            .equals(((CoreRebecaActorState) valueSecond).getId()));
//                else if (operator.equals("!="))
//                    rightSideResult = !(((CoreRebecaActorState) valueFirst).getId()
//                            .equals(((CoreRebecaActorState) valueSecond).getId()));
//                else
//                    throw new RebecaRuntimeInterpreterException(
//                            "this case should have been reported as an error by the compiler.");
//            } else
//                rightSideResult = SemanticCheckerUtils.evaluateConstantTerm(operator, null, valueFirst, valueSecond);
//        }

//        source.getFirst().setVariableValue((Variable) aib.getLeftVarName(), rightSideResult);
//        source.getFirst().movePCtoTheNextInstruction();

//        CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> result =
//                new CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>>();
//        result.setDestination(source);
//        result.setAction(Action.TAU);
//        return result;
//    }

    public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> applyRule(Action action,
                                                                                                    Pair<CoreRebecaActorState, InstructionBean> source) {
        throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
    }
}
