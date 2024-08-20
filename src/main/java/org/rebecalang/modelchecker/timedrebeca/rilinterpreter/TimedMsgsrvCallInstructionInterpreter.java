package org.rebecalang.modelchecker.timedrebeca.rilinterpreter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modelchecker.timedrebeca.TimedActorState;
import org.rebecalang.modelchecker.timedrebeca.TimedMessageSpecification;
import org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelChecker;
import org.rebecalang.modelchecker.timedrebeca.TimedState;
import org.rebecalang.modeltransformer.ril.RILUtilities;
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
	public void interpret(InstructionBean ib, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel) {
		TimedMsgsrvCallInstructionBean tmcib = (TimedMsgsrvCallInstructionBean) ib;
		Map<String, Object> parameters = setMsgSrvParameters(baseActorState, tmcib.getParameters());

		int after = (int) tmcib.getAfter();
		int deadline = (int) tmcib.getDeadline();
		int period = Integer.MAX_VALUE;

		TimedActorState receiverActorState = (TimedActorState) baseActorState.retrieveVariableValue(tmcib.getBase());

		for (ReactiveClassDeclaration reactiveClassDeclaration : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			if (reactiveClassDeclaration.getName().equals(receiverActorState.getTypeName())) {
				for (MsgsrvDeclaration msgsrv : reactiveClassDeclaration.getMsgsrvs()) {
					if (tmcib.getMethodName().equals(RILUtilities.computeMethodName(reactiveClassDeclaration, msgsrv))) {
						List<Annotation> annotations;
						String periodStr;
						if (!(annotations = msgsrv.getAnnotations()).isEmpty() && !(Objects.requireNonNull( periodStr = TimedRebecaModelChecker.getAnnotation(annotations, "period"))).isEmpty()) {
							period = Integer.parseInt(periodStr);
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
				tmcib.getMethodName(), parameters, baseActorState, after, absoluteDeadline, deadline, period);

		receiverActorState.addToQueue(msgSpec);
		baseActorState.increasePC();
	}
}