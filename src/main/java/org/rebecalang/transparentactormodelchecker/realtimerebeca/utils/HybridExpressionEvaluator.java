package org.rebecalang.transparentactormodelchecker.realtimerebeca.utils;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.*;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;

import java.util.ArrayList;
import java.util.List;

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

    private static Object evaluate(Object expression, HybridRebecaActorState state) {
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

        // ===== nondet propagation =====
        if (left instanceof NonDetValue || right instanceof NonDetValue) {
            return evaluateNonDet(left, op, right);
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

    private static List<Object> expand(Object o) {
        if (o instanceof NonDetValue nd) {
            return nd.getNonDetValues();
        }
        return List.of(o);
    }

    public static Object evaluateNonDet(Object left, String op, Object right) {
        List<Object> leftVals = expand(left);
        List<Object> rightVals = expand(right);
        NonDetValue result = new NonDetValue();

        for (Object l : leftVals) {
            for (Object r : rightVals) {

                Object computed;

                // ===== numeric =====
                if (l instanceof Number && r instanceof Number) {
                    float a = ((Number) l).floatValue();
                    float b = ((Number) r).floatValue();

                    computed = switch (op) {
                        case "+" -> a + b;
                        case "-" -> a - b;
                        case "*" -> a * b;
                        case "/" -> b != 0 ? a / b : Float.NaN;

                        case "<" -> a < b;
                        case ">" -> a > b;
                        case "<=" -> a <= b;
                        case ">=" -> a >= b;
                        case "==" -> a == b;
                        case "!=" -> a != b;

                        default -> throw new UnsupportedOperationException(
                                "Unsupported nondet numeric op: " + op);
                    };
                }

                // ===== boolean =====
                else if (l instanceof Boolean && r instanceof Boolean) {
                    boolean a = (Boolean) l;
                    boolean b = (Boolean) r;

                    computed = switch (op) {
                        case "&&" -> a && b;
                        case "||" -> a || b;
                        case "==" -> a == b;
                        case "!=" -> a != b;
                        default -> throw new UnsupportedOperationException(
                                "Unsupported nondet boolean op: " + op);
                    };
                }

                // ===== string =====
                else if (l instanceof String || r instanceof String) {
                    String a = String.valueOf(l);
                    String b = String.valueOf(r);

                    computed = switch (op) {
                        case "==" -> a.equals(b);
                        case "!=" -> !a.equals(b);
                        case "+" -> a + b;
                        default -> throw new UnsupportedOperationException(
                                "Unsupported nondet string op: " + op);
                    };
                }

                else {
                    throw new IllegalArgumentException(
                            "Unsupported nondet types: " + l.getClass() + " , " + r.getClass());
                }

                // avoid duplicates (VERY IMPORTANT)
                if (!result.getNonDetValues().contains(computed)) {
                    result.addNonDetValue(computed);
                }
            }
        }

        return collapseIfDeterministic(result);
    }

    private static Object collapseIfDeterministic(NonDetValue nd) {
        if (nd.getNonDetValues().isEmpty()) {
            return nd;
        }

        Object first = nd.getNonDetValues().getFirst();

        for (Object v : nd.getNonDetValues()) {
            if (!first.equals(v)) {
                return nd;   // real nondet
            }
        }

        return first;  // all equal → deterministic
    }

    private static Pair<Float, Float> toInterval(Object o) {
        if (o instanceof NonDetValue nd) {
            return new Pair<>((float)nd.getNonDetValues().get(0), (float)nd.getNonDetValues().get(1));
        }
        if (o instanceof Number n) {
            float v = n.floatValue();
            return new Pair<>(v, v);
        }
        throw new IllegalArgumentException("Cannot convert to interval: " + o);
    }

    private static NonDetValue evaluateBooleanInterval(
            Pair<Float, Float> l, String op, Pair<Float, Float> r) {

        float lmin = l.getFirst();
        float lmax = l.getSecond();
        float rmin = r.getFirst();
        float rmax = r.getSecond();

        boolean canBeTrue = false;
        boolean canBeFalse = false;

        // check boundary combinations
        for (float a : new float[]{lmin, lmax}) {
            for (float b : new float[]{rmin, rmax}) {

                boolean res = switch (op) {
                    case "<" -> a < b;
                    case ">" -> a > b;
                    case "<=" -> a <= b;
                    case ">=" -> a >= b;
                    case "==" -> a == b;
                    case "!=" -> a != b;
                    default -> throw new UnsupportedOperationException("Unknown operator: " + op);
                };

                if (res) canBeTrue = true;
                else canBeFalse = true;
            }
        }

        NonDetValue nd = new NonDetValue();

        if (canBeTrue) nd.addNonDetValue(true);
        if (canBeFalse) nd.addNonDetValue(false);

        return nd;
    }


    private static Object getValue(Object reference, HybridRebecaActorState actorState) {
        if (reference instanceof Variable) {
            String varName = ((Variable) reference).getVarName();
            return actorState.getVariableValue(varName);
        }
        return reference;
    }

}
