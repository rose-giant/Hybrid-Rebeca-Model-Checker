package org.rebecalang.modelchecker.timedrebeca.rilinterpreter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modelchecker.timedrebeca.TimedActorState;
import org.rebecalang.modelchecker.timedrebeca.TimedMessageSpecification;
import org.rebecalang.modelchecker.timedrebeca.TimedState;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.TimedMsgsrvCallInstructionBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TimedMsgsrvCallInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, BaseActorState baseActorState, State<? extends BaseActorState> globalState) {
		TimedMsgsrvCallInstructionBean tmcib = (TimedMsgsrvCallInstructionBean) ib;
		Map<String, Object> parameters = setMsgSrvParameters(baseActorState, tmcib.getParameters());

		int after = (int) tmcib.getAfter();
		int deadline = (int) tmcib.getDeadline();

		TimedActorState receiverState = (TimedActorState) baseActorState.retrieveVariableValue(tmcib.getBase());

//		if (((TimedState) globalState).isFTTS()) {
//			int currentTime = ((TimedActorState)baseActorState).getCurrentTime();
//			after += currentTime;
//			deadline += currentTime;
//		}

		TimedMessageSpecification msgSpec = new TimedMessageSpecification(
				tmcib.getMethodName(), parameters, baseActorState, after, deadline);

		receiverState.addToQueue(msgSpec);
		baseActorState.increasePC();
	}
}