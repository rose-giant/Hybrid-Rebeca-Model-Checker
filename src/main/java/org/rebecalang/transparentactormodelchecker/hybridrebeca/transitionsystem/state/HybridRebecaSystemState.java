package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class HybridRebecaSystemState implements Serializable {
    private Environment environment;
    private HashMap<String, HybridRebecaActorState> actorsState;
    private HybridRebecaNetworkState networkState;

    public HybridRebecaSystemState() {
        actorsState = new HashMap<String, HybridRebecaActorState>();
    }

    public Collection<HybridRebecaActorState> getActorsStatesValues() {
        return actorsState.values();
    }
    public Set<String> getActorsIds() {
        return actorsState.keySet();
    }
    public HashMap<String, HybridRebecaActorState> getActorsState() {
        return actorsState;
    }
    public void setActorState(String id, HybridRebecaActorState newState) {
        newState.setEnvironment(environment);
        actorsState.put(id, newState);
    }
    public HybridRebecaActorState getActorState(String id) {
        return actorsState.get(id);
    }

    public HybridRebecaNetworkState getNetworkState() {
        return networkState;
    }
    public void setNetworkState(HybridRebecaNetworkState networkState) {
        this.networkState = networkState;
    }
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    public String toString() {
        String result = "Env" + environment + "\n";
        for(HybridRebecaActorState actorState : actorsState.values())
            result += actorState + "\n";
        return result;
    }
}
