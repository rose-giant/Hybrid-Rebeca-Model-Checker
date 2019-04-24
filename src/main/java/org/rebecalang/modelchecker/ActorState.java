package org.rebecalang.modelchecker;

import java.util.LinkedList;

public class ActorState {
	private LinkedList<MessageSpecification> queue;
	private ActorScopeStack actorScopeStack;
	private String name;
	private String typeName;


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actorScopeStack == null) ? 0 : actorScopeStack.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((queue == null) ? 0 : queue.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActorState other = (ActorState) obj;
		if (actorScopeStack == null) {
			if (other.actorScopeStack != null)
				return false;
		} else if (!actorScopeStack.equals(other.actorScopeStack))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (queue == null) {
			if (other.queue != null)
				return false;
		} else if (!queue.equals(other.queue))
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}

	public LinkedList<MessageSpecification> getQueue() {
		return queue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setQueue(LinkedList<MessageSpecification> queue) {
		this.queue = queue;
	}

	public void addToQueue(MessageSpecification msgSpec) {
		queue.add(msgSpec);
	}

	// public LinkedList<Hashtable<String, Object>> getActorScopeStack() {
	// return actorScopeStack;
	// }
	// public void setActorScopeStack(LinkedList<Hashtable<String, Object>>
	// actorScopeStack) {
	// this.actorScopeStack = actorScopeStack;
	// }

	// public Object retreiveVariableValue(String varName) {
	// Iterator<Hashtable<String, Object>> iterator =
	// actorScopeStack.descendingIterator();
	// while (iterator.hasNext()) {
	// Hashtable<String, Object> next = iterator.next();
	// if (next.containsKey(varName))
	// return next.get(varName);
	// }
	// throw new RebecaRuntimeInterpreterException("Failure in retreiving variable "
	// + varName + " from scope");
	// }
	//
	// public void setVariableValue(String varName, Object valueObject) {
	// Iterator<Hashtable<String, Object>> iterator =
	// actorScopeStack.descendingIterator();
	// while (iterator.hasNext()) {
	// Hashtable<String, Object> next = iterator.next();
	// if (next.containsKey(varName)) {
	// next.put(varName, valueObject);
	// return;
	// }
	// }
	// throw new RebecaRuntimeInterpreterException("Failure in retreiving variable "
	// + varName + " from scope");
	// }

	public boolean actorQueueIsEmpty() {
		return queue.isEmpty();
	}

	public void pushInActorScope() {
		actorScopeStack.pushInScopeStack();
	}

	public void popFromActorScope() {
		actorScopeStack.popFromScopeStack();
	}

	public void addVariableToRecentScope(String varName, Object valueObject) {
		actorScopeStack.addVariable(varName, valueObject);
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void initializeScopeStack() {
		actorScopeStack = new ActorScopeStack();
		actorScopeStack.initialize();
	}

	public Object retreiveVariableValue(String varName) {
		return actorScopeStack.retreiveVariableValue(varName);
	}

	public void setVariableValue(String varName, Object valueObject) {
		actorScopeStack.setVariableValue(varName, valueObject);
	}
}
