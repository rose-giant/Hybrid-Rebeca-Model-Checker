package org.rebecalang.modelchecker.corerebeca.policy;

import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;

public class PolicyFactory {
    public static AbstractPolicy getPolicy(Policy policy) throws ModelCheckingException {
        return switch (policy) {
            case COARSE_GRAINED_POLICY -> new CoarseGrainedPolicy();
            case FINE_GRAINED_POLICY   -> new FineGrainedPolicy();
            default                    -> throw new ModelCheckingException("Unknown policy " + policy);
        };
    }
}