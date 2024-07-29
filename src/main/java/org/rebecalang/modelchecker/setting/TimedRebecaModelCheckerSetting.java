package org.rebecalang.modelchecker.setting;

import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;
import org.rebecalang.modelchecker.timedrebeca.utils.TransitionSystem;

import java.util.Set;

public class TimedRebecaModelCheckerSetting extends CoreRebecaModelCheckerSetting {
    private TransitionSystem transitionSystem;
    private boolean isBounded;

    public TimedRebecaModelCheckerSetting() {
    }

    public TimedRebecaModelCheckerSetting(Set<CompilerExtension> extension, CoreVersion coreVersion, Policy policy, TransitionSystem transitionSystem, boolean isBounded) {
        super(extension, coreVersion, policy);

        setTransitionSystem(transitionSystem);
        setIsBounded(isBounded);
    }

    public TransitionSystem getTransitionSystem() {
        return transitionSystem;
    }

    public void setTransitionSystem(TransitionSystem transitionSystem) {
        this.transitionSystem = transitionSystem;
    }

    public boolean getIsBounded() {
        return isBounded;
    }

    public void setIsBounded(boolean isBounded) {
        this.isBounded = isBounded;
    }
}
