package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;

import java.util.ArrayList;

public class AbstractTransparentActorState {
    private RealTimeRebecaSystemState systemState = new RealTimeRebecaSystemState();
    private RealTimeRebecaAbstractTransition abstractTransition;
    private ArrayList<RealTimeRebecaAbstractTransition> transitions = new ArrayList<>();

    public void addTransition(RealTimeRebecaAbstractTransition<RealTimeRebecaSystemState> transition) {
        transitions.add(transition);
    }

    public void AbstractTransparentActorState(RealTimeRebecaSystemState newSystemState, RealTimeRebecaAbstractTransition newAbstractTransition) {
        systemState = newSystemState;
        abstractTransition = newAbstractTransition;
    }

    public RealTimeRebecaAbstractTransition getAbstractTransition() {
        return abstractTransition;
    }

    public void setAbstractTransition(RealTimeRebecaAbstractTransition abstractTransition) {
        this.abstractTransition = abstractTransition;
    }

    public RealTimeRebecaSystemState getSystemState() {
        return systemState;
    }

    public void setSystemState(RealTimeRebecaSystemState systemState) {
        this.systemState = systemState;
    }
}