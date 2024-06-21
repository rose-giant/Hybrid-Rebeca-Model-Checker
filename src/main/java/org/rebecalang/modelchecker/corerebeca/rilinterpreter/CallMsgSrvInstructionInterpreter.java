package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.util.ArrayList;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.CallMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class CallMsgSrvInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, BaseActorState baseActorState, State globalState) {
		CallMsgSrvInstructionBean cmib = (CallMsgSrvInstructionBean) ib;
		MessageSpecification msgSpec = new MessageSpecification(cmib.getMsgsrvName(), new ArrayList<Object>(), baseActorState);
		BaseActorState receiverState = (BaseActorState) baseActorState.retrieveVariableValue(cmib.getReceiver());
		receiverState.addToQueue(msgSpec);
		baseActorState.increasePC();
	}
}