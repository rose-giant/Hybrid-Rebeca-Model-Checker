package org.rebecalang.transparentactormodelchecker.hybridrebeca.utils;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;

public class HybridExpressionEvaluator {

    public static Object hybridExpressionEvaluator(Object expression) {
        if (expression instanceof StartUnbreakableConditionInstructionBean) {
            StartUnbreakableConditionInstructionBean bean = (StartUnbreakableConditionInstructionBean) expression;
            Object right = bean.getRightSide();
            String op = bean.getOperator();
            Object left = bean.getLeftSide();

            if (left instanceof Pair<?,?> && right instanceof Number) {
                Pair<Number, Number> leftInterval = (Pair<Number, Number>) left;
                float floatRight = ((Number) right).floatValue();
                float leftFirst = leftInterval.getFirst().floatValue();
                float leftSecond = leftInterval.getSecond().floatValue();

                if (op == "<") {
                    if (leftSecond < floatRight) {
                        return true;
                    }
                    if (floatRight < leftSecond && floatRight > leftFirst) {
                        return null;
                    }
                    if (floatRight < leftFirst) {
                        return false;
                    }
                } else if (op == ">=") {
                    if (leftSecond < floatRight) {
                        return false;
                    }
                    if (floatRight < leftSecond && floatRight > leftFirst) {
                        return null;
                    }
                    if (floatRight < leftFirst) {
                        return true;
                    }
                }
            }

            else if (left instanceof Number && right instanceof Number) {
                double leftVal = ((Number) left).doubleValue();
                double rightVal = ((Number) right).doubleValue();

                return switch (op) {
                    case ">" -> leftVal > rightVal;
                    case ">=" -> leftVal >= rightVal;
                    case "<" -> leftVal < rightVal;
                    case "<=" -> leftVal <= rightVal;
                    case "==" -> leftVal == rightVal;
                    case "!=" -> leftVal != rightVal;
                    case "+" -> leftVal + rightVal;
                    case "-" -> leftVal - rightVal;
                    case "*" -> leftVal * rightVal;
                    case "/" -> rightVal != 0 ? leftVal / rightVal : Double.NaN;
                    default -> throw new UnsupportedOperationException("Unknown operator: " + op);
                };
            }

            if (left instanceof String || right instanceof String) {
                String leftStr = left.toString();
                String rightStr = right.toString();

                return switch (op) {
                    case "==" -> leftStr.equals(rightStr);
                    case "!=" -> !leftStr.equals(rightStr);
                    case "+" -> leftStr + rightStr;
                    default -> throw new UnsupportedOperationException("Unsupported string operator: " + op);
                };
            }
            throw new IllegalArgumentException("Unsupported operand types: " + left.getClass() + " and " + right.getClass());
        }

        return expression;
    }
}
