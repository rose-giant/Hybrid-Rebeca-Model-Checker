package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MsgsrvCallInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, BaseActorState baseActorState, State<? extends ActorState> globalState) {
		MsgsrvCallInstructionBean mcib = (MsgsrvCallInstructionBean) ib;
		Map<String, Object> parameters = new TreeMap<String, Object>();
		for (Entry<String, Object> entry : mcib.getParameters().entrySet()) {
            String paramName = entry.getKey();
			Object paramValue = entry.getValue();
            if (paramValue instanceof Variable)
                paramValue = baseActorState.retrieveVariableValue((Variable) paramValue);
            parameters.put(paramName, paramValue);
        }
		MessageSpecification msgSpec = new MessageSpecification(mcib.getMethodName(), parameters, baseActorState);
		BaseActorState receiverState = (BaseActorState) baseActorState.retrieveVariableValue(mcib.getBase());
		receiverState.addToQueue(msgSpec);
		baseActorState.increasePC();
	}
}