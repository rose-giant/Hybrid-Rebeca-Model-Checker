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
		Map<String, Object> parameters = new TreeMap<String, Object>();
		for (Entry<String, Object> entry : tmcib.getParameters().entrySet()) {
            String paramName = entry.getKey();
			Object paramValue = entry.getValue();
            if (paramValue instanceof Variable)
                paramValue = baseActorState.retrieveVariableValue((Variable) paramValue);
            parameters.put(paramName, paramValue);
        }

		TimedMessageSpecification msgSpec = new TimedMessageSpecification(
				tmcib.getMethodName(), parameters, baseActorState, 
				0, 0);
		TimedActorState receiverState = (TimedActorState) baseActorState.retrieveVariableValue(tmcib.getBase());
		receiverState.addToQueue(msgSpec);
		baseActorState.increasePC();
		
		
//        TimedMsgSrvCallInstructionBean ctmib = (TimedMsgSrvCallInstructionBean) ib;
//        TimedActorState receiverState = (TimedActorState) baseActorState.retrieveVariableValue(ctmib.getReceiver());
//        String msgSrvName = receiverState.getTypeName() + "." + ctmib.getMsgsrvName().split("\\.")[1];
//        MessageSpecification msgSpec = new TimedMessageSpecification(msgSrvName, new ArrayList<>(),
//                baseActorState, (int)ctmib.getAfter(), (int)ctmib.getDeadline());
//        receiverState.addToQueue(msgSpec);
//        baseActorState.increasePC();
	}
}