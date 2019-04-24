package org.rebecalang.modelchecker;

import java.util.LinkedList;

public class ActorScopeStack {

	LinkedList<ActivationRecord> activationRecords;

	public Object retreiveVariableValue(String varName) {

		ActivationRecord cursor = activationRecords.getLast();
		while (cursor != null) {
			Object result = cursor.getVariableValue(varName);
			if (result != null)
				return result;
			cursor = cursor.getPreviousRecord();
		}
		throw new RebecaRuntimeInterpreterException("Failure in retreiving variable " + varName + " from scope");
	}

	public void setVariableValue(String varName, Object valueObject) {

		ActivationRecord cursor = activationRecords.getLast();
		while (cursor != null) {
			Object result = cursor.getVariableValue(varName);
			if (result != null) {
				cursor.setVariableValue(varName, valueObject);
				return;

			}
			cursor = cursor.getPreviousRecord();
		}
		throw new RebecaRuntimeInterpreterException("Failure in retreiving variable " + varName + " from scope");
	}

	public void addVariable(String name, Object valueObject) {
		ActivationRecord cursor = activationRecords.getLast();
		cursor.addVariable(name, valueObject);
	}

	public void pushInScopeStack() {
		ActivationRecord newRecord = new ActivationRecord();
		newRecord.initialize();
		if (!activationRecords.isEmpty())
			newRecord.setPreviousRecord(activationRecords.getLast());
		activationRecords.addLast(newRecord);
	}

	public void popFromScopeStack() {
		activationRecords.removeLast();
	}

	public void initialize() {
		activationRecords = new LinkedList<ActivationRecord>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activationRecords == null) ? 0 : activationRecords.hashCode());
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
		ActorScopeStack other = (ActorScopeStack) obj;
		if (activationRecords == null) {
			if (other.activationRecords != null)
				return false;
		} else if (!activationRecords.equals(other.activationRecords))
			return false;
		return true;
	}

}
