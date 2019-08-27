package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modeltransformer.ril.rilinstructions.Variable;

public class InstructionUtilities {
	public static final String PC_STRING = "$PC$";

	public static Object getValue(Object operand, ActorState actorState) {
		if (operand instanceof Variable)
			return actorState.retreiveVariableValue(((Variable)operand).getVarName());
		return operand;
	}
	
}
