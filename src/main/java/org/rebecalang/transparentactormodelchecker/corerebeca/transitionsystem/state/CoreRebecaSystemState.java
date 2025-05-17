package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

@SuppressWarnings("serial")
public class CoreRebecaSystemState extends CoreRebecaAbstractState implements Serializable {
	private Environment environment;
	private HashMap<String, CoreRebecaActorState> actorsState;
	private CoreRebecaNetworkState networkState;

	public CoreRebecaSystemState() {
		actorsState = new HashMap<String, CoreRebecaActorState>();
	}
	
	public Collection<CoreRebecaActorState> getActorsStatesValues() {
		return actorsState.values();
	}
	public Set<String> getActorsIds() {
		return actorsState.keySet();
	}
	public HashMap<String, CoreRebecaActorState> getActorsState() {
		return actorsState;
	}
	public void setActorState(String id, CoreRebecaActorState newState) {
		newState.setEnvironment(environment);
		actorsState.put(id, newState);
	}
	public CoreRebecaActorState getActorState(String id) {
		return actorsState.get(id);
	}

	public CoreRebecaNetworkState getNetworkState() {
		return networkState;
	}
	public void setNetworkState(CoreRebecaNetworkState networkState) {
		this.networkState = networkState;
	}
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	public String toString() {
		String result = "Env" + environment + "\n";
		for(CoreRebecaActorState actorState : actorsState.values())
			result += actorState + "\n";
		return result;
	}
}
