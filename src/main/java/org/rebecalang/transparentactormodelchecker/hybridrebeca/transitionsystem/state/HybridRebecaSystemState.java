package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import org.rebecalang.compiler.utils.Pair;
import java.io.Serializable;
import java.util.*;

public class HybridRebecaSystemState implements Serializable {
    private Environment environment;
    private HashMap<String, HybridRebecaActorState> actorsState;
    private HybridRebecaNetworkState networkState;
    private Pair<Float, Float> now = new Pair<>();
    private Pair<Float, Float> resumeTime = new Pair<>();

    private List<Object> eventList = new ArrayList<>();

    public void setEventList(List<Object> eventList) {
        this.eventList = eventList;
    }

    public List<Object> getEventList() {
        return eventList;
    }

    public Pair<Float, Float> getNow() {
        return now;
    }

    public void setNow(Pair<Float, Float> now) {
        this.now = now;
    }

    public void setResumeTime(Pair<Float, Float> resumeTime) {
        this.resumeTime = resumeTime;
    }

    public Pair<Float, Float> getResumeTime() {
        return resumeTime;
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
}
