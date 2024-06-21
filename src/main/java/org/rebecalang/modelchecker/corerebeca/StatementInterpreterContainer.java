package org.rebecalang.modelchecker.corerebeca;

import java.util.Hashtable;

import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class StatementInterpreterContainer {

	Hashtable<Class<? extends InstructionBean>, InstructionInterpreter> interpreters;

	public void registerInterpreter(Class<? extends InstructionBean> clazz,
			InstructionInterpreter instructionInterpreter) {
		interpreters.put(clazz, instructionInterpreter);
	}
	public InstructionInterpreter retrieveInterpreter(InstructionBean ib) {
		return (interpreters.get(ib.getClass()));
	}

	private boolean hasNonterminism;
	public void reportNondeterminism() {
		hasNonterminism = true;
	}
	
	public void clearNondeterminism() {
		hasNonterminism = false;
	}
	public boolean hasNondeterminism() {
		return hasNonterminism;
	}
	public void clear() {
		interpreters.clear();
		clearNondeterminism();
	}
	
}
