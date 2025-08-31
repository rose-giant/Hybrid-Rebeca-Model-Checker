package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;

public class AbstractTransparentActorState {
    private HybridRebecaSystemState systemState = new HybridRebecaSystemState();
    private HybridRebecaAbstractTransition abstractTransition;

    public void AbstractTransparentActorState(HybridRebecaSystemState newSystemState, HybridRebecaAbstractTransition newAbstractTransition) {
        systemState = newSystemState;
        abstractTransition = newAbstractTransition;
    }

    public HybridRebecaAbstractTransition getAbstractTransition() {
        return abstractTransition;
    }

    public void setAbstractTransition(HybridRebecaAbstractTransition abstractTransition) {
        this.abstractTransition = abstractTransition;
    }

    public HybridRebecaSystemState getSystemState() {
        return systemState;
    }

    public void setSystemState(HybridRebecaSystemState systemState) {
        this.systemState = systemState;
    }
}