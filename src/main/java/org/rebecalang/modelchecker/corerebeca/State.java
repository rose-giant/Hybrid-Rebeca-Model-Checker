package org.rebecalang.modelchecker.corerebeca;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class State<T extends BaseActorState>  implements Serializable {

    protected HashMap<String, T> stateInfo;
    protected List<Pair<String, State<T>>> childStates;
    protected List<Pair<String, State<T>>> parentStates;
    private int id;

    public State() {
        super();
        stateInfo = new HashMap<String, T>();
        childStates = new LinkedList<Pair<String, State<T>>>();
        parentStates = new LinkedList<Pair<String, State<T>>>();
    }

    public void putActorState(String name, T actorState) {

        stateInfo.put(name, actorState);
    }

    public BaseActorState getActorState(String name) {
        return stateInfo.get(name);
    }

    public List<T> getAllActorStates() {
        LinkedList<T> allActorsState = new LinkedList<T>();
        Iterator<String> iterator = stateInfo.keySet().iterator();
        while (iterator.hasNext()) {
            allActorsState.add(stateInfo.get(iterator.next()));
        }
        return allActorsState;
    }

    public List<T> getEnabledActors()  {
        LinkedList<T> enabledActors = new LinkedList<T>();
        for (T baseActorState : getAllActorStates()) {
            if (!baseActorState.actorQueueIsEmpty())
                enabledActors.add(baseActorState);
            else if (baseActorState.variableIsDefined(InstructionUtilities.PC_STRING))
                enabledActors.add(baseActorState);
        }
        return enabledActors;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((stateInfo == null) ? 0 : stateInfo.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
		State<T> other = (State<T>) obj;
        if (stateInfo == null) {
			return other.stateInfo == null;
        } else return stateInfo.equals(other.stateInfo);
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addChildState(String label, State<T> childState) {
        childStates.add(new Pair<String, State<T>>(label, childState));
    }

    public void addParentState(String label, State<T> parentState) {
        parentStates.add(new Pair<String, State<T>>(label, parentState));
    }

    public List<Pair<String, State<T>>> getChildStates() {
        return childStates;
    }

    public List<Pair<String, State<T>>> getParentStates() {
        return parentStates;
    }

    public void setChildStates(List<Pair<String, State<T>>> childStates) {
        this.childStates = childStates;
    }

    public void setParentStates(List<Pair<String, State<T>>> parentStates) {
        this.parentStates = parentStates;
    }

    public void clearLinks() {
        childStates = new LinkedList<Pair<String, State<T>>>();
        parentStates = new LinkedList<Pair<String, State<T>>>();
    }

	public void exportState(PrintStream output) {
		output.println("<state id=\"" + id + "\" atomicpropositions=\"\"");
		for(String stateKey : stateInfo.keySet()) {
			BaseActorState baseActorState = stateInfo.get(stateKey);
			baseActorState.export(output);
		}
		output.println("</state>");
	}
	
	public String toString() {
		String retValue = String.valueOf(id);
		for(String key : stateInfo.keySet()) {
			retValue += "\n" + stateInfo.get(key);
		}
		retValue += "\n next: ";
		for(Pair<String, State<T>> child : childStates) {
			retValue += "[" + child.getFirst() + "] " + child.getSecond().id + ",";
		}
		return retValue;
	}
}
