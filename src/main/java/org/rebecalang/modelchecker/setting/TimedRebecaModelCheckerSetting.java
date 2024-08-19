package org.rebecalang.modelchecker.setting;

import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.modelchecker.timedrebeca.utils.SchedulingPolicy;
import org.rebecalang.modelchecker.timedrebeca.utils.TransitionSystem;

import java.util.Set;

public class TimedRebecaModelCheckerSetting extends CoreRebecaModelCheckerSetting {
    private TransitionSystem    transitionSystem;
    private SchedulingPolicy schedulingPolicy;

    public TimedRebecaModelCheckerSetting() {
    }

    public TimedRebecaModelCheckerSetting(Set<CompilerExtension> extension, CoreVersion coreVersion, TransitionSystem transitionSystem) {
        super(extension, coreVersion, transitionSystem.getPolicy());

        setTransitionSystem(transitionSystem);
    }

    public TimedRebecaModelCheckerSetting(Set<CompilerExtension> extension, CoreVersion coreVersion, TransitionSystem transitionSystem, SchedulingPolicy schedulingPolicy) {
        super(extension, coreVersion, transitionSystem.getPolicy());

        setTransitionSystem(transitionSystem);
        setSchedulingPolicy(schedulingPolicy);
    }

    public TransitionSystem getTransitionSystem() {
        return transitionSystem;
    }

    public void setTransitionSystem(TransitionSystem transitionSystem) {
        this.transitionSystem = transitionSystem;
    }

    public void setSchedulingPolicy(SchedulingPolicy schedulingPolicy) {
        this.schedulingPolicy = schedulingPolicy;
    }

    public SchedulingPolicy getSchedulingPolicy() {
        return schedulingPolicy;
    }

    public boolean isFTTS() {
        return transitionSystem == TransitionSystem.TRANSITION_SYSTEM_FTTS;
    }
}
