package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class HybridRebecaAssignmentSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {

    private Object getValue(Object reference, HybridRebecaActorState actorState) {
        if (reference instanceof Variable) {
            String varName = ((Variable) reference).getVarName();
            return actorState.getVariableValue(varName);
        }
        return reference;
    }

    private HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> handleContinuousAssignment(Pair<HybridRebecaActorState, InstructionBean> source) {
        StartUnbreakableConditionInstructionBean sucib = (StartUnbreakableConditionInstructionBean) source.getSecond();
        Variable leftSide = (Variable) sucib.getLeftSide();
        String operator = sucib.getOperator();
        if (operator.equals("=")) {
            if (sucib.getRightSide() instanceof Float) {
                source.getFirst().setVariableValue(leftSide, sucib.getRightSide());
            }
            else if (sucib.getRightSide() instanceof Variable) {
                source.getFirst().setVariableValue(((Variable) sucib.getLeftSide()) ,source.getFirst().getVariableValue(((Variable) sucib.getRightSide()).getVarName()));
            }
        }

        source.getFirst().moveToNextStatement();
        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>();
        result.setDestination(source);
        result.setAction(Action.TAU);
        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        if (source.getSecond() instanceof StartUnbreakableConditionInstructionBean) {
            return handleContinuousAssignment(source);
        }
        AssignmentInstructionBean aib = (AssignmentInstructionBean) source.getSecond();
        Object valueFirst = getValue(aib.getFirstOperand(), source.getFirst());
        Object valueSecond = getValue(aib.getSecondOperand(), source.getFirst());
        String expressionOperator = aib.getOperator();
        String operator = "=";

        Object rightSideResult = new Object();
        if (expressionOperator != null) {
            if (valueFirst instanceof HybridRebecaActorState) {
                if (operator.equals("=="))
                    rightSideResult = (((HybridRebecaActorState) valueFirst).getId()
                            .equals(((HybridRebecaActorState) valueSecond).getId()));
                else if (operator.equals("!="))
                    rightSideResult = !(((HybridRebecaActorState) valueFirst).getId()
                            .equals(((HybridRebecaActorState) valueSecond).getId()));
                else
                    throw new RebecaRuntimeInterpreterException(
                            "this case should have been reported as an error by the compiler.");
            }
            else if (rightSideResult instanceof NonDetValue) {
                return handleNonDetAssignment(source);
            }
            else if (expressionOperator.equals("<")) {
                rightSideResult = (float)valueFirst < (float)valueSecond;
            }
            else if (expressionOperator.equals(">")) {

                rightSideResult = (float)valueFirst > (float)valueSecond;
            }
            else if (expressionOperator.equals("==")) {
                rightSideResult = (float)valueFirst == (float)valueSecond;
            }
            else if (expressionOperator.equals("!=")) {
                rightSideResult = (float)valueFirst != (float)valueSecond;
            }
            else if (expressionOperator.equals("+")) {
                rightSideResult = (float)valueFirst + (float)valueSecond;
            }
            else if (expressionOperator.equals("-")) {
                rightSideResult = (float)valueFirst - (float)valueSecond;
            }
            else if (expressionOperator.equals("*")) {
                rightSideResult = (float)valueFirst * (float)valueSecond;
            }
            else if (expressionOperator.equals("/")) {
                rightSideResult = (float)valueFirst / (float)valueSecond;
            }
            else
                rightSideResult = SemanticCheckerUtils.evaluateConstantTerm(operator, null, valueFirst, valueSecond);
        } else {
            rightSideResult = valueFirst;
        }

        if (rightSideResult instanceof Float) {
            source.getFirst().setVariableValue((Variable) aib.getLeftVarName(), (Float)rightSideResult);
        } else if (rightSideResult instanceof Boolean) {
            source.getFirst().setVariableValue((Variable) aib.getLeftVarName(), (Boolean)rightSideResult);
        }
        else {
            source.getFirst().setVariableValue((Variable) aib.getLeftVarName(), rightSideResult);
        }
        source.getFirst().moveToNextStatement();

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>();
        result.setDestination(source);
        result.setAction(Action.TAU);
        return result;
    }

    private Object handleBinaryExpressionAssignment(Pair<HybridRebecaActorState, InstructionBean> source) {
        Object result = new Object();

        return result;
    }

    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
    handleNonDetAssignment(Pair<HybridRebecaActorState, InstructionBean> source) {
        HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaNondeterministicTransition<>();
        AssignmentInstructionBean aib = (AssignmentInstructionBean) source.getSecond();
        Object valueFirst = getValue(aib.getFirstOperand(), source.getFirst());
        NonDetValue rightSideResult = (NonDetValue) valueFirst;
        for (Object obj : rightSideResult.getNonDetValues()) {
            source.getFirst().setVariableValue((Variable) aib.getLeftVarName(), rightSideResult);
            result.addDestination(Action.TAU, source);
        }

        source.getFirst().movePCtoTheNextInstruction();
        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}
