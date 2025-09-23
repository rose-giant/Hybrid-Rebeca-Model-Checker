package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

import java.io.Serializable;
import java.util.*;

public class HybridRebecaSystemState implements Serializable {
    private Environment environment;
    private HashMap<String, HybridRebecaActorState> actorsState;
    private HybridRebecaNetworkState networkState;
    private Pair<Float, Float> now = new Pair<>();
    private Pair<Float, Float> inputInterval = new Pair<>();

//    private List<Object> eventList = new ArrayList<>();

//    public void setEventList(List<Object> eventList) {
//        this.eventList = eventList;
//    }
//
//    public List<Object> getEventList() {
//        return eventList;
//    }

    public Pair<Float, Float> getNow() {
        return now;
    }

    public void setNow(Pair<Float, Float> now) {
        this.now = now;
    }

    public void setInputInterval(Pair<Float, Float> inputInterval) {
        this.inputInterval = inputInterval;
    }

    public Pair<Float, Float> getInputInterval() {
        return inputInterval;
    }

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

    public boolean isTakeEnable() {
        HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(this);
        for (String actorId : backup.getActorsIds()) {
            HybridRebecaActorState hybridRebecaActorState = this.getActorState(actorId);
            if (!hybridRebecaActorState.messageQueueIsEmpty()) {
                return true;
            }
        }

        return false;
    }

    public boolean thereIsSuspension() {
        for(String actorId : this.getActorsState().keySet()) {
            HybridRebecaActorState hybridRebecaActorState = this.getActorState(actorId);
            if (hybridRebecaActorState.isSuspent()) {
                return true;
            }
        }

        return false;
    }
}
