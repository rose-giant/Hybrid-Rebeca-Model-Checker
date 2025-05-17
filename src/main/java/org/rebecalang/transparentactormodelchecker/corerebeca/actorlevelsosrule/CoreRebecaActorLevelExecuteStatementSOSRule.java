package org.rebecalang.transparentactormodelchecker.corerebeca.actorlevelsosrule;

import java.util.Arrays;
import java.util.List;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaAssignmentSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaSendMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaVariableDeclarationSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaEndMSGSrvSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaPopSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaPushSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaActorLevelExecuteStatementSOSRule extends AbstractSOSRule<CoreRebecaActorState> {

	@Autowired
	CoreRebecaAssignmentSOSRule assignmentSOSRule;
	
	@Autowired
	CoreRebecaSendMessageSOSRule sendMessageSOSRule;
	
	@Autowired
	CoreRebecaVariableDeclarationSOSRule variableDeclarationSOSRule;
	
	@Autowired
	CoreRebecaPopSOSRule popSOSRule;
	
	@Autowired
	CoreRebecaPushSOSRule pushSOSRule;
	
	@Autowired
	CoreRebecaEndMSGSrvSOSRule endMSGSrvSOSRule;
	
	List<Pair<? extends Action, CoreRebecaActorState>> decorateStatementExecutionResult(
			Pair<? extends Action, Pair<CoreRebecaActorState, InstructionBean>> statementExecutionResult) {
		return Arrays.asList(new Pair<Action, CoreRebecaActorState>(Action.TAU, statementExecutionResult.getSecond().getFirst())); 
	}
	
	CoreRebecaDeterministicTransition<CoreRebecaActorState> convertStatementResultToActorResult(
			CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> result) {
		return new CoreRebecaDeterministicTransition<CoreRebecaActorState>(
				result.getAction(), result.getDestination().getFirst());
	}

	@Override
	public CoreRebecaAbstractTransition<CoreRebecaActorState> applyRule(CoreRebecaActorState source) {
		CoreRebecaAbstractTransition<CoreRebecaActorState> destinations = null;
		InstructionBean instruction = source.getEnabledInstruction();
		if(instruction instanceof AssignmentInstructionBean) {
			CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
				executionResult = assignmentSOSRule.applyRule(new Pair<>(source, instruction));
			destinations = convertStatementResultToActorResult(executionResult);
		} else if(instruction instanceof MsgsrvCallInstructionBean) {
			CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
				executionResult = sendMessageSOSRule.applyRule(new Pair<>(source, instruction));
			destinations = convertStatementResultToActorResult(executionResult);
		} else if(instruction instanceof DeclarationInstructionBean) {
			CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
				executionResult = variableDeclarationSOSRule.applyRule(new Pair<>(source, instruction));
			destinations = convertStatementResultToActorResult(executionResult);
		} else if(instruction instanceof PushARInstructionBean) {
			CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
				executionResult = pushSOSRule.applyRule(new Pair<>(source, instruction));
			destinations = convertStatementResultToActorResult(executionResult);
		} else if(instruction instanceof PopARInstructionBean) {
			CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
				executionResult = popSOSRule.applyRule(new Pair<>(source, instruction));
			destinations = convertStatementResultToActorResult(executionResult);
		} else if(instruction instanceof EndMsgSrvInstructionBean) {
			CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
				executionResult = endMSGSrvSOSRule.applyRule(new Pair<>(source, instruction));
			destinations = convertStatementResultToActorResult(executionResult);
		} else {
			throw new RebecaRuntimeInterpreterException("Unknown rule for the statement " + instruction);
		}
		return destinations;
	}
	
	@Override
	public boolean isEnable(CoreRebecaActorState source) {
		return !source.hasVariableInScope(CoreRebecaActorState.PC) && 
			   !source.messageQueueIsEmpty();
	}

	@Override
	public CoreRebecaAbstractTransition<CoreRebecaActorState> applyRule(Action action, CoreRebecaActorState synchSource) {
		throw new RebecaRuntimeInterpreterException("Actor Level execute statement rule does not accept input action");
	}

}
