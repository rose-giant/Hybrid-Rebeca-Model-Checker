package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.timedrebeca.utils.TransitionSystem;

public class TimedRebecaModelCheckerFactory {
    public static TimedRebecaModelChecker getModelChecker(TransitionSystem transitionSystem) {
        switch (transitionSystem) {
            case TRANSITION_SYSTEM_FGTS:
                return new FGTSModelChecker();
            case TRANSITION_SYSTEM_FTTS:
                return new FTTSModelChecker();
            default:
                throw new IllegalArgumentException("Unknown model checker type: " + transitionSystem);
        }
    }
}