package org.rebecalang.transparentactormodelchecker.realtimerebeca.utils;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;

public class HybridExpressionEvaluator {

    public static Object hybridExpressionEvaluator(Pair<HybridRebecaActorState, InstructionBean> source) {
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

        throw new IllegalArgumentException(
                "Unsupported operand types: " + left.getClass() + " and " + right.getClass());
    }

}
