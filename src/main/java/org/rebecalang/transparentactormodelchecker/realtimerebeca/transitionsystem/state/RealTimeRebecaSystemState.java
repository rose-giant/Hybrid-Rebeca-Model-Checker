package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;

import java.io.Serializable;
import java.util.*;

public class RealTimeRebecaSystemState implements Serializable {
    private Environment environment;
    private HashMap<String, RealTimeRebecaActorState> actorsState;
    private RealTimeRebecaNetworkState networkState;
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

    public RealTimeRebecaSystemState() {
        actorsState = new HashMap<String, RealTimeRebecaActorState>();
    }

    public Collection<RealTimeRebecaActorState> getActorsStatesValues() {
        return actorsState.values();
    }
    public Set<String> getActorsIds() {
        return actorsState.keySet();
    }
    public HashMap<String, RealTimeRebecaActorState> getActorsState() {
        return actorsState;
    }
    public void setActorState(String id, RealTimeRebecaActorState newState) {
        newState.setEnvironment(environment);
        actorsState.put(id, newState);
    }
    public RealTimeRebecaActorState getActorState(String id) {
        return actorsState.get(id);
    }

    public RealTimeRebecaNetworkState getNetworkState() {
        return networkState;
    }
    public void setNetworkState(RealTimeRebecaNetworkState networkState) {
        this.networkState = networkState;
    }
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    public String toString() {
        String result = "Env" + environment + "\n";
        for(RealTimeRebecaActorState actorState : actorsState.values())
            result += actorState + "\n";
        return result;
    }

    public boolean isTakeEnable() {
        RealTimeRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(this);
        for (String actorId : backup.getActorsIds()) {
            RealTimeRebecaActorState realTimeRebecaActorState = this.getActorState(actorId);
            if (!realTimeRebecaActorState.messageQueueIsEmpty()) {
                return true;
            }
        }

        return false;
    }

    public boolean thereIsSuspension() {
        for(String actorId : this.getActorsState().keySet()) {
            RealTimeRebecaActorState realTimeRebecaActorState = this.getActorState(actorId);
            if (realTimeRebecaActorState.isSuspent()) {
                return true;
            }
        }

        return false;
    }
}
