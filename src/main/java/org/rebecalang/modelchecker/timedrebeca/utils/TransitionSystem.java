package org.rebecalang.modelchecker.timedrebeca.utils;

import org.rebecalang.modelchecker.corerebeca.utils.Policy;

public enum TransitionSystem {
    TRANSITION_SYSTEM_FTTS,
    TRANSITION_SYSTEM_FGTS;

    private TransitionSystem() {
    }

    public Policy getPolicy() {
        return switch (this) {
            case TRANSITION_SYSTEM_FGTS -> Policy.FINE_GRAINED_POLICY;
            case TRANSITION_SYSTEM_FTTS -> Policy.COARSE_GRAINED_POLICY;
        };
    }
}
