package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProgramCounter implements Serializable {
	private String methodName;
//	private String reactiveclassName;
	private int lineNumber;
	
	public ProgramCounter() {}
	
	public ProgramCounter(String methodName, int lineNumber) {
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}

	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getActorName() {
		return methodName.split("\\.")[0];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lineNumber;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
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
		ProgramCounter other = (ProgramCounter) obj;
		if (lineNumber != other.lineNumber)
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "(" + methodName + ":" + lineNumber + ")";
	}
}
