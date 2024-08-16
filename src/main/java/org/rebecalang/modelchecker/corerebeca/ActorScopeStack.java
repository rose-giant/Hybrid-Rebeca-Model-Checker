package org.rebecalang.modelchecker.corerebeca;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

@SuppressWarnings("serial")
public class ActorScopeStack implements Serializable {

	LinkedList<ActivationRecord> activationRecords;

	public boolean variableIsDefined(String varName) {
		try {
			retrieveVariableValue(varName);
			return true;
		} catch (RebecaRuntimeInterpreterException e) {
		}
		return false;

	}

	public Object retrieveVariableValue(String varName) {

		ActivationRecord cursor = activationRecords.getLast();
		while (cursor != null) {
			Object variableValue = cursor.getVariableValue(varName);
			if (variableValue != null)
				return variableValue;
			cursor = cursor.getPreviousScope();
		}
		throw new RebecaRuntimeInterpreterException("Failure in retrieving variable " + varName + " from scope");

	}

	public void setVariableValue(String varName, Object valueObject) {

		ActivationRecord cursor = activationRecords.getLast();
		do {
			if (cursor.hasVariable(varName)) {
				cursor.setVariableValue(varName, valueObject);
				return;
			}
		} while ((cursor = cursor.getPreviousScope()) != null);
		throw new RebecaRuntimeInterpreterException("Failure in retreiving variable " + varName + " from scope");

	}

	public void addVariable(String name, Object valueObject) {
		ActivationRecord cursor = activationRecords.getLast();
		cursor.addVariable(name, valueObject);
	}


    public void addVariable(String name, Object valueObject, int index) {
        ActivationRecord cursor = activationRecords.get(index);
        cursor.addVariable(name, valueObject);
    }

    private void pushInScopeStack(String typeName, ActivationRecord prev) {
    	ActivationRecord newRecord = new ActivationRecord();
		newRecord.initialize();
		newRecord.setPreviousScope(prev);
        newRecord.setRelatedRebecType(typeName);
        activationRecords.addLast(newRecord);
    }
    
    public void pushInScopeStackForMethodCallInitialization(
    		String relatedRebecType) {
    	ListIterator<ActivationRecord> iterator = activationRecords.listIterator(
    			activationRecords.size());
    	while(iterator.hasPrevious()) {
            ActivationRecord ar = iterator.previous();
			if (relatedRebecType.equals(ar.getRelatedRebecType())) {
                pushInScopeStack(null, ar);
                return;
            }    		
    	}
    }
    
    public void pushInScopeStackForInsideMethod() {
    	pushInScopeStack(null, activationRecords.getLast());
    }
    
    public void pushInScopeStackForInheritanceStack(String typeName) {
    	pushInScopeStack(typeName, activationRecords.isEmpty()? null : activationRecords.getLast());
    }
    
//    public void pushInScopeStack(String relatedRebecType) {
//    	ActivationRecord newRecord = new ActivationRecord();
//		newRecord.initialize();
//		ActivationRecord last = null;
//		try {
//			last = activationRecords.getLast();
//		} catch (NoSuchElementException e) {
//
//		}
//		newRecord.setPreviousScope(last);
//        newRecord.setRelatedRebecType(relatedRebecType);
//        activationRecords.addLast(newRecord);
//    }

//    public void pushInScopeStack(String relatedRebecType, String previousRebecType) {
//        ActivationRecord newRecord = new ActivationRecord();
//        newRecord.initialize();
//        ActivationRecord prev = null;
//        for (ActivationRecord record: activationRecords) {
//            if (record.getRelatedRebecType().equals(previousRebecType)) {
//                prev = record;
//            }
//        }
//        newRecord.setPreviousScope(prev);
//        newRecord.setRelatedRebecType(relatedRebecType);
//        activationRecords.addLast(newRecord);
//    }

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
		result = prime * result ;
		result += ((activationRecords == null) ? 0 : activationRecords.hashCode());
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
			return other.activationRecords == null;
        } else return activationRecords.equals(other.activationRecords);
	}

    public LinkedList<ActivationRecord> getActivationRecords() {
        return activationRecords;
    }

    public void export(PrintStream output) {
		for(ActivationRecord activationRecord : activationRecords) {
			activationRecord.export(output);
		}
	}
}
