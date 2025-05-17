package org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaPopSOSRule extends AbstractSOSRule<Pair<CoreRebecaActorState, InstructionBean>> {

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
			applyRule(Pair<CoreRebecaActorState, InstructionBean> source) {

		PopARInstructionBean pib = (PopARInstructionBean) source.getSecond();
		for(int cnt = 0; cnt < pib.getNumberOfPops(); cnt++)
			source.getFirst().popFromScope();
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
