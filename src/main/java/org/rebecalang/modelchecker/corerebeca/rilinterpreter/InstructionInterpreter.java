package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;

public abstract class InstructionInterpreter {

	public abstract void interpret (InstructionBean ib, ActorState actorState, State globalState);
	
}
