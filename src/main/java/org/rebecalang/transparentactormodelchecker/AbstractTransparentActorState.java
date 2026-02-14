package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;

import java.util.ArrayList;

public class AbstractTransparentActorState {
    private HybridRebecaSystemState systemState = new HybridRebecaSystemState();
    private HybridRebecaAbstractTransition abstractTransition;
    private ArrayList<HybridRebecaAbstractTransition> transitions = new ArrayList<>();

    public void addTransition(HybridRebecaAbstractTransition<HybridRebecaSystemState> transition) {
        transitions.add(transition);
    }

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