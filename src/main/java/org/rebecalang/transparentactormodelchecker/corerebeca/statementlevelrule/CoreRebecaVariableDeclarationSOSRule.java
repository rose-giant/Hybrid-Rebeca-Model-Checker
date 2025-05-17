package org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaVariableDeclarationSOSRule extends AbstractSOSRule<Pair<CoreRebecaActorState, InstructionBean>> {

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
			applyRule(Pair<CoreRebecaActorState, InstructionBean> source) {

		DeclarationInstructionBean vdib = (DeclarationInstructionBean) source.getSecond();
		source.getFirst().addVariableToScope(vdib.getVarName());
		source.getFirst().movePCtoTheNextInstruction();

		CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> result = 
				new CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState,InstructionBean>>();
		result.setDestination(source);
		result.setAction(Action.TAU);

		return result;
	}

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
			applyRule(Action action, Pair<CoreRebecaActorState, InstructionBean> source) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}

}
