package org.rebecalang.modelchecker.timedrebeca.rilinterpreter;

import java.util.List;
import java.util.Map;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.timedrebeca.TimedActorState;
import org.rebecalang.modelchecker.timedrebeca.TimedMessageSpecification;
import org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelChecker;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.TimedMsgsrvCallInstructionBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TimedMsgsrvCallInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel) {
		TimedMsgsrvCallInstructionBean tmcib = (TimedMsgsrvCallInstructionBean) ib;
		Map<String, Object> parameters = setMsgSrvParameters(baseActorState, tmcib.getParameters());

		int after = 0;
		if (tmcib.getAfter() instanceof NonDetValue) {
			after = (int) InstructionUtilities.getValue(statementInterpreterContainer, tmcib.getAfter(), baseActorState);
		} else if (tmcib.getAfter() instanceof Variable) {
			Object afterValue = baseActorState.retrieveVariableValue((Variable) tmcib.getAfter());

			if (afterValue instanceof Integer) {
				after = (int) afterValue;
			} else if (afterValue instanceof String) {
				after = Integer.parseInt((String) afterValue);
			}
		}else {
			after = (int) tmcib.getAfter();
		}

		int deadline = Integer.MAX_VALUE;
		if (tmcib.getDeadline() instanceof NonDetValue) {
			deadline = (int) InstructionUtilities.getValue(statementInterpreterContainer, tmcib.getDeadline(), baseActorState);
		} else if (tmcib.getDeadline() instanceof Variable) {
			Object deadlineValue = baseActorState.retrieveVariableValue((Variable) tmcib.getDeadline());

			if (deadlineValue instanceof Integer) {
				deadline = (int) deadlineValue;
			} else if (deadlineValue instanceof String) {
				deadline = Integer.parseInt((String) deadlineValue);
			}
		} else {
			deadline = (int) tmcib.getDeadline();
		}

		int period = Integer.MAX_VALUE;
		int priority = Integer.MAX_VALUE;

		TimedActorState receiverActorState = (TimedActorState) baseActorState.retrieveVariableValue(tmcib.getBase());

		for (ReactiveClassDeclaration reactiveClassDeclaration : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			if (reactiveClassDeclaration.getName().equals(receiverActorState.getTypeName())) {
				for (MsgsrvDeclaration msgsrv : reactiveClassDeclaration.getMsgsrvs()) {
					if (tmcib.getMethodName().equals(RILUtilities.computeMethodName(reactiveClassDeclaration, msgsrv))) {
						List<Annotation> annotations = msgsrv.getAnnotations();
						String periodStr, priorityStr;

						if (!annotations.isEmpty()) {
							if ((periodStr = TimedRebecaModelChecker.getAnnotation(annotations, "period")) != null) {
								period = Integer.parseInt(periodStr);
							}
							if ((priorityStr = TimedRebecaModelChecker.getAnnotation(annotations, "priority")) != null) {
								priority = Integer.parseInt(priorityStr);
							}
						}
					}
				}
			}
		}

		int currentTime = ((TimedActorState)baseActorState).getCurrentTime();
		after += currentTime;

		int absoluteDeadline = deadline;
		if (deadline != Integer.MAX_VALUE) {
			absoluteDeadline = deadline+currentTime;
		}

		TimedMessageSpecification msgSpec = new TimedMessageSpecification(
				tmcib.getMethodName(), priority, parameters, baseActorState, after, absoluteDeadline, deadline, period);

		receiverActorState.addToQueue(msgSpec);
		baseActorState.increasePC();
	}
}