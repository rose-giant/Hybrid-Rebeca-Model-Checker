package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.translator.AbstractStatementTranslator;

public class EndMethodInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
		Object retreivedReturnVariableValue = actorState.retreiveVariableValue(AbstractStatementTranslator.RETURN_VALUE);
		actorState.popFromActorScope();
		actorState.setVariableValue(AbstractStatementTranslator.RETURN_VALUE, retreivedReturnVariableValue);
	}

	@Override
	public String toString() {
		return "endMethod";
	}
}
