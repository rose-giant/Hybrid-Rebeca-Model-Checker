package org.rebecalang.transparentactormodelchecker.realtimerebeca.utils;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.*;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;

import java.util.ArrayList;

public class HybridExpressionEvaluator {

    public static Object hybridExpressionEvaluator(Pair<HybridRebecaActorState, InstructionBean> source) {
        if (source.getSecond() instanceof JumpIfNotInstructionBean) {
            return evaluate(((JumpIfNotInstructionBean) source.getSecond()).getCondition(), source.getFirst());
        }
//        Object expression = ((JumpIfNotInstructionBean) source.getSecond()).getCondition();
        Object expression = source.getSecond();

        if (expression == null) {
            return true;
        }

        return evaluate(expression, source.getFirst());
    }

    private static Object unbreakableExpressionEvaluator(Object expression, HybridRebecaActorState state) {
        return evaluate(expression, state);
    }

    private static Object evaluateOperand(Object operand, HybridRebecaActorState state) {
        if (operand instanceof StartUnbreakableConditionInstructionBean) {
            return evaluate(operand, state);
        }
        if (operand instanceof Variable) {
            return state.getVariableValue(((Variable) operand).getVarName());
        }
        return operand;
    }

//    private static object evaluateAssignment(AssignmentInstructionBean instructionBean, HybridRebecaActorState state) {
//
//    }

    private static Object evaluate(Object expression, HybridRebecaActorState state) {
//        if (expression instanceof AssignmentInstructionBean) {
//            evaluateAssignment((AssignmentInstructionBean)expression, state);
//        }
        if (!(expression instanceof StartUnbreakableConditionInstructionBean bean)) {
            return expression;
        }

        Object left = evaluateOperand(bean.getLeftSide(), state);
        Object right = evaluateOperand(bean.getRightSide(), state);
        String op = bean.getOperator();

        // ===== interval vs number =====
        if (left instanceof Pair<?, ?> && right instanceof Number) {
            Pair<Number, Number> interval = (Pair<Number, Number>) left;
            float l = interval.getFirst().floatValue();
            float r = interval.getSecond().floatValue();
            float x = ((Number) right).floatValue();

            if (op.equals("<")) {
                if (r < x) return true;
                if (x > l && x < r) return null;
                return false;
            }
            if (op.equals(">=")) {
                if (r < x) return false;
                if (x > l && x < r) return null;
                return true;
            }
        }

        // ===== number vs number =====
        if (left instanceof Number && right instanceof Number) {
            double a = ((Number) left).doubleValue();
            double b = ((Number) right).doubleValue();

            return switch (op) {
                case ">" -> a > b;
                case ">=" -> a >= b;
                case "<" -> a < b;
                case "<=" -> a <= b;
                case "==" -> a == b;
                case "!=" -> a != b;
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> b != 0 ? a / b : Double.NaN;
                default -> throw new UnsupportedOperationException("Unknown operator: " + op);
            };
        }

        // ===== boolean vs boolean =====
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

        // ===== string handling =====
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

        if (left instanceof NonDetValue) {
//            StartUnbreakableConditionInstructionBean aib = (StartUnbreakableConditionInstructionBean) expression;
//            NonDetValue valueFirst = (NonDetValue) getValue(aib.getFirstOperand(), state);
//            Object valueSecond = getValue(aib.getSecondOperand(), state);
//            String expressionOperator = aib.getOperator();
//            ArrayList<Object> results = new ArrayList<>();
//            for (Object val: valueFirst.getNonDetValues()) {
//                AssignmentInstructionBean instructionBean = new AssignmentInstructionBean(aib.getLeftVarName(), val, valueSecond, expressionOperator);
//                results.add(evaluateAssignmentExpression(instructionBean ,state));
//            }
//
//            return results;
            return left;
        }

        throw new IllegalArgumentException(
                "Unsupported operand types: " + left.getClass() + " and " + right.getClass());
    }

    private static Object getValue(Object reference, HybridRebecaActorState actorState) {
        if (reference instanceof Variable) {
            String varName = ((Variable) reference).getVarName();
            return actorState.getVariableValue(varName);
        }
        return reference;
    }

    private static Object evaluateAssignmentExpression(AssignmentInstructionBean aib, HybridRebecaActorState state) {
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


}
