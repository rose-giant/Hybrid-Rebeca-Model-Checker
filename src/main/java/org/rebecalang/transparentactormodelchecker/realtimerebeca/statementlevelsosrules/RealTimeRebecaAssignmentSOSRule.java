package org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import static org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridExpressionEvaluator.evaluateNonDet;

@Component
public class RealTimeRebecaAssignmentSOSRule extends AbstractRealTimeSOSRule<Pair<RealTimeRebecaActorState, InstructionBean>> {

    private Object getValue(Object reference, RealTimeRebecaActorState actorState) {
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

    private RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> handleContinuousAssignment(Pair<RealTimeRebecaActorState, InstructionBean> source) {
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
        RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =
                new RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>>();
        result.setDestination(source);
        result.setAction(Action.TAU);
        return result;
    }

    @Override
    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> applyRule(
            Pair<RealTimeRebecaActorState, InstructionBean> source) {
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

    private Object evaluateAssignmentExpression(AssignmentInstructionBean aib,
                                                RealTimeRebecaActorState state) {

        Object left = getValue(aib.getFirstOperand(), state);
        Object right = getValue(aib.getSecondOperand(), state);
        String op = aib.getOperator();

        if (op == null) {
            return left;
        }

        // ===== nondet present → lift computation =====
        if (left instanceof NonDetValue || right instanceof NonDetValue) {
            return evaluateNonDet(left, op, right);
        }

        // ===== actor equality =====
        if (left instanceof RealTimeRebecaActorState && right instanceof RealTimeRebecaActorState) {
            return switch (op) {
                case "==" -> ((RealTimeRebecaActorState) left).getId()
                        .equals(((RealTimeRebecaActorState) right).getId());
                case "!=" -> !((RealTimeRebecaActorState) left).getId()
                        .equals(((RealTimeRebecaActorState) right).getId());
                default -> throw new RebecaRuntimeInterpreterException(
                        "this case should have been reported as an error by the compiler.");
            };
        }

        // ===== numeric =====
        if (left instanceof Number && right instanceof Number) {
            float a = ((Number) left).floatValue();
            float b = ((Number) right).floatValue();

            return switch (op) {
                case "<" -> a < b;
                case ">" -> a > b;
                case "==" -> a == b;
                case "!=" -> a != b;
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> a / b;
                default -> SemanticCheckerUtils.evaluateConstantTerm("=", null, left, right);
            };
        }

        // ===== boolean =====
        if (left instanceof Boolean && right instanceof Boolean) {
            boolean a = (Boolean) left;
            boolean b = (Boolean) right;

            return switch (op) {
                case "&&" -> a && b;
                case "||" -> a || b;
                case "==" -> a == b;
                case "!=" -> a != b;
                default -> throw new UnsupportedOperationException("Unknown operator: " + op);
            };
        }

        // ===== string =====
        if (left instanceof String || right instanceof String) {
            String a = String.valueOf(left);
            String b = String.valueOf(right);

            return switch (op) {
                case "==" -> a.equals(b);
                case "!=" -> !a.equals(b);
                case "+" -> a + b;
                default -> throw new UnsupportedOperationException("Unsupported string operator: " + op);
            };
        }

        throw new IllegalArgumentException(
                "Unsupported operand types: " + left.getClass() + " and " + right.getClass());
    }

    private void assignValue(RealTimeRebecaActorState state, Variable var, Object value) {
        state.setVariableValue(var, value);
        state.moveToNextStatement();
    }

    private RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
    buildTauTransition(Pair<RealTimeRebecaActorState, InstructionBean> dest) {
        RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> t =
                new RealTimeRebecaDeterministicTransition<>();

        t.setDestination(dest);
        t.setAction(Action.TAU);
        return t;
    }

    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
    handleNonDetAssignment(Pair<RealTimeRebecaActorState, InstructionBean> source) {
        RealTimeRebecaNondeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result = new RealTimeRebecaNondeterministicTransition<>();
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

    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
    handleNonDetExpression(Pair<RealTimeRebecaActorState, InstructionBean> source) {
        RealTimeRebecaNondeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result = new RealTimeRebecaNondeterministicTransition<>();
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
    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<RealTimeRebecaActorState, InstructionBean> source) {
        return null;
    }
}
