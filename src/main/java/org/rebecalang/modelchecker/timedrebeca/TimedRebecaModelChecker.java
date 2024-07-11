package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.CoreRebecaModelChecker;
import org.springframework.stereotype.Component;

@Component
public class TimedRebecaModelChecker extends CoreRebecaModelChecker {

    public final static String CURRENT_TIME = "current_time";
    public final static String RESUMING_TIME = "resuming_time";

    public TimedRebecaModelChecker() {
        super();
    }

}
