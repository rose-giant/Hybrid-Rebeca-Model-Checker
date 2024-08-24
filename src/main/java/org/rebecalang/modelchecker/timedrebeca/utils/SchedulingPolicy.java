package org.rebecalang.modelchecker.timedrebeca.utils;

import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.timedrebeca.TimedMessageSpecification;

import java.io.Serializable;

public enum SchedulingPolicy implements Serializable {
    SCHEDULING_ALGORITHM_FIFO,
    SCHEDULING_ALGORITHM_EDF,
    SCHEDULING_ALGORITHM_RMS,
    SCHEDULING_ALGORITHM_DMS,
    SCHEDULING_ALGORITHM_PRIORITY;

    private SchedulingPolicy() {
    }

    public static SchedulingPolicy getSchedulingPolicy(String input) throws ModelCheckingException {
        return switch (input.toUpperCase()) {
            case "FIFO"     -> SCHEDULING_ALGORITHM_FIFO;
            case "EDF"      -> SCHEDULING_ALGORITHM_EDF;
            case "RMS"      -> SCHEDULING_ALGORITHM_RMS;
            case "DMS"      -> SCHEDULING_ALGORITHM_DMS;
            case "PRIORITY" -> SCHEDULING_ALGORITHM_PRIORITY;

            default     -> throw new ModelCheckingException("Unknown scheduling policy " + input);
        };
    }

    public static boolean compare(SchedulingPolicy schedulingPolicy, TimedMessageSpecification first, TimedMessageSpecification second, String operator) {
        int comparisonResult = switch (schedulingPolicy) {
            case SCHEDULING_ALGORITHM_FIFO     -> Integer.compare(first.getMinStartTime(), second.getMinStartTime());
            case SCHEDULING_ALGORITHM_EDF      -> Integer.compare(first.getRelativeDeadline(), second.getRelativeDeadline());
            case SCHEDULING_ALGORITHM_RMS      -> Integer.compare(first.getPeriod(), second.getPeriod());
            case SCHEDULING_ALGORITHM_DMS      -> Integer.compare(first.getMaxStartTime(), second.getMaxStartTime());
            case SCHEDULING_ALGORITHM_PRIORITY -> Integer.compare(first.getPriority(), second.getPriority());
            default -> throw new IllegalArgumentException("Unknown scheduling policy: " + schedulingPolicy);
        };

        return evaluateComparison(comparisonResult, operator);
    }

    private static boolean evaluateComparison(int comparisonResult, String operator) {
        return switch (operator) {
            case "<"  -> comparisonResult < 0;
            case "<=" -> comparisonResult <= 0;
            case ">"  -> comparisonResult > 0;
            case ">=" -> comparisonResult >= 0;
            case "==" -> comparisonResult == 0;
            case "!=" -> comparisonResult != 0;
            default   -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

}
