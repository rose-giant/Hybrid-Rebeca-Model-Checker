package org.rebecalang.coarsegrainedmodelchecker;

import java.util.Hashtable;

public class ActivationRecord {

	Hashtable<String, Object> definedVariables;
	private ActivationRecord previousRecord;

	public ActivationRecord getPreviousRecord() {
		return previousRecord;
	}

	public void setPreviousRecord(ActivationRecord previousRecord) {
		this.previousRecord = previousRecord;
	}

	public void setVariableValue(String name, Object value) {
		definedVariables.put(name, value);
	}

	public Object getVariableValue(String name) {
		return definedVariables.get(name);
	}

	public void addVariable(String name, Object valueObject) {
		definedVariables.put(name, valueObject);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((definedVariables == null) ? 0 : definedVariables.hashCode());
		result = prime * result + ((previousRecord == null) ? 0 : previousRecord.hashCode());
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
		ActivationRecord other = (ActivationRecord) obj;
		if (definedVariables == null) {
			if (other.definedVariables != null)
				return false;
		} else if (!definedVariables.equals(other.definedVariables))
			return false;
		if (previousRecord == null) {
			if (other.previousRecord != null)
				return false;
		} else if (!previousRecord.equals(other.previousRecord))
			return false;
		return true;
	}

	public void initialize() {
		definedVariables = new Hashtable<String, Object>();
	}

}
