package org.rebecalang.modelchecker.timedrebeca.utils;

import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;

import java.io.Serializable;

public enum SchedulingPolicy implements Serializable {
    SCHEDULING_ALGORITHM_FIFO,
    SCHEDULING_ALGORITHM_EDF,
    SCHEDULING_ALGORITHM_RMS,
    SCHEDULING_ALGORITHM_DMS;

    private SchedulingPolicy() {
    }

    public static SchedulingPolicy getSchedulingPolicy(String input) throws ModelCheckingException {
        return switch (input.toUpperCase()) {
            case "FIFO" -> SCHEDULING_ALGORITHM_FIFO;
            case "EDF"  -> SCHEDULING_ALGORITHM_EDF;
            case "RMS"  -> SCHEDULING_ALGORITHM_RMS;
            case "DMS"  -> SCHEDULING_ALGORITHM_DMS;

            default     -> throw new ModelCheckingException("Unknown scheduling policy " + input);
        };
    }
}
