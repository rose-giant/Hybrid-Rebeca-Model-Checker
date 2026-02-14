package org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import static org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridExpressionEvaluator.hybridExpressionEvaluator;

@Component
public class HybridRebecaAssignmentSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {

    private Object getValue(Object reference, HybridRebecaActorState actorState) {
        if (reference instanceof Variable) {
            String varName = ((Variable) reference).getVarName();
            return actorState.getVariableValue(varName);
        }
        return reference;
    }

    public static float randomFloatInclusive(float min, float max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max");
        }
        return min + (max - min) * ThreadLocalRandom.current().nextFloat();
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
            else if (sucib.getRightSide() instanceof NonDetValue) {
                float lower = (float) ((NonDetValue) sucib.getRightSide()).getNonDetValues().getFirst();
                float upper = (float) ((NonDetValue) sucib.getRightSide()).getNonDetValues().getLast();
//                source.getFirst().setVariableValue(leftSide, randomFloatInclusive(lower, upper));
                NonDetValue nonDetValue = new NonDetValue();
                LinkedList<Object> valueList = new LinkedList<>();
                valueList.add(lower);
                valueList.add(upper);
                nonDetValue.setNondetValues(valueList);
                source.getFirst().setVariableValue(leftSide, nonDetValue);
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
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(
            Pair<HybridRebecaActorState, InstructionBean> source) {
        if (source.getSecond() instanceof StartUnbreakableConditionInstructionBean) {
            return handleContinuousAssignment(source);
        }

        AssignmentInstructionBean aib = (AssignmentInstructionBean) source.getSecond();
        Object result = evaluateAssignmentExpression(aib, source.getFirst());
//        if (result instanceof NonDetValue) {
//            return handleNonDetAssignment(source);
//        }

        assignValue(source.getFirst(), (Variable) aib.getLeftVarName(), result);
        return buildTauTransition(source);
    }

    private Object evaluateAssignmentExpression(AssignmentInstructionBean aib, HybridRebecaActorState state) {
        Object valueFirst = getValue(aib.getFirstOperand(), state);
        Object valueSecond = getValue(aib.getSecondOperand(), state);
        String op = aib.getOperator();

        if (op == null) {
            return valueFirst;
        }

        if (valueFirst instanceof NonDetValue) {
            return valueFirst;
        }

        if (valueFirst instanceof HybridRebecaActorState) {
            if (op.equals("=="))
                return ((HybridRebecaActorState) valueFirst).getId()
                        .equals(((HybridRebecaActorState) valueSecond).getId());
            if (op.equals("!="))
                return !((HybridRebecaActorState) valueFirst).getId()
                        .equals(((HybridRebecaActorState) valueSecond).getId());

            throw new RebecaRuntimeInterpreterException(
                    "this case should have been reported as an error by the compiler.");
        }

        float a = ((Number) valueFirst).floatValue();
        float b = ((Number) valueSecond).floatValue();

        return switch (op) {
            case "<" -> a < b;
            case ">" -> a > b;
            case "==" -> a == b;
            case "!=" -> a != b;
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> SemanticCheckerUtils.evaluateConstantTerm("=", null, valueFirst, valueSecond);
        };
    }

    private void assignValue(HybridRebecaActorState state, Variable var, Object value) {
        state.setVariableValue(var, value);
        state.moveToNextStatement();
    }

    private HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>
    buildTauTransition(Pair<HybridRebecaActorState, InstructionBean> dest) {
        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> t =
                new HybridRebecaDeterministicTransition<>();

        t.setDestination(dest);
        t.setAction(Action.TAU);
        return t;
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

        source.getFirst().moveToNextStatement();
        return result;
    }

    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
    handleNonDetExpression(Pair<HybridRebecaActorState, InstructionBean> source) {
        HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaNondeterministicTransition<>();
//        HybridRebecaActorState backup = HybridRebecaStateSerializationUtils.clone(source.getFirst());
        AssignmentInstructionBean aib = (AssignmentInstructionBean) source.getSecond();
        NonDetValue valueFirst = (NonDetValue) getValue(aib.getFirstOperand(), source.getFirst());
        Object valueSecond = getValue(aib.getSecondOperand(), source.getFirst());
        String expressionOperator = aib.getOperator();
        ArrayList<Object> results = new ArrayList<>();
        for (Object val: valueFirst.getNonDetValues()) {
            AssignmentInstructionBean instructionBean = new AssignmentInstructionBean(aib.getLeftVarName(), val, valueSecond, expressionOperator);
            source.setSecond(instructionBean);
            results.add(evaluateAssignmentExpression(aib ,source.getFirst()));
        }

        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}
