package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.timedrebeca.utils.TransitionSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public record TimedRebecaModelCheckerFactory(ApplicationContext applicationContext) {

    @Autowired
    public TimedRebecaModelCheckerFactory {
    }

    public TimedRebecaModelChecker getModelChecker(TransitionSystem transitionSystem) throws ModelCheckingException {
        switch (transitionSystem) {
            case TRANSITION_SYSTEM_FGTS:
                return applicationContext.getBean(FGTSModelChecker.class);
            case TRANSITION_SYSTEM_FTTS:
                return applicationContext.getBean(FTTSModelChecker.class);
            default:
                throw new IllegalArgumentException("Unknown model checker type: " + transitionSystem);
        }
    }
}