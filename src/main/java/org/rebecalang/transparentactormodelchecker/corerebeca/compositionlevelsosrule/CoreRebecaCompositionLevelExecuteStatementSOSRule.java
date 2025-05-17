package org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule;

import java.util.Iterator;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.actorlevelsosrule.CoreRebecaActorLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.networklevelsosrule.CoreRebecaNetworkLevelReceiveMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.RebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaCompositionLevelExecuteStatementSOSRule extends AbstractSOSRule<CoreRebecaSystemState>{

	@Autowired
	CoreRebecaActorLevelExecuteStatementSOSRule coreRebecaActorLevelExecuteStatementSOSRule;
	
	@Autowired
	CoreRebecaNetworkLevelReceiveMessageSOSRule coreRebecaNetworkLevelReceiveMessageSOSRule;
	
	@Override
	public CoreRebecaNondeterministicTransition<CoreRebecaSystemState> applyRule(CoreRebecaSystemState source) {
		CoreRebecaNondeterministicTransition<CoreRebecaSystemState> transitions = 
				new CoreRebecaNondeterministicTransition<CoreRebecaSystemState>();

		CoreRebecaSystemState backup = RebecaStateSerializationUtils.clone(source);
		for(String actorId : backup.getActorsState().keySet()) {
			CoreRebecaActorState coreRebecaActorState = source.getActorState(actorId);
			if(!coreRebecaActorState.hasVariableInScope(CoreRebecaActorState.PC))
				continue;

			CoreRebecaAbstractTransition<CoreRebecaActorState> executionResult = 
					coreRebecaActorLevelExecuteStatementSOSRule.applyRule(coreRebecaActorState);

			if(executionResult instanceof CoreRebecaDeterministicTransition<CoreRebecaActorState>) {
				CoreRebecaDeterministicTransition<CoreRebecaActorState> transition = 
						(CoreRebecaDeterministicTransition<CoreRebecaActorState>)executionResult;
				if(transition.getAction() instanceof MessageAction) {
					coreRebecaNetworkLevelReceiveMessageSOSRule.applyRule(
							transition.getAction(), source.getNetworkState());
				}
				transitions.addDestination(transition.getAction(), source);
			} else if(executionResult instanceof CoreRebecaNondeterministicTransition<CoreRebecaActorState>) {
				Iterator<Pair<? extends Action, CoreRebecaActorState>> transitionsIterator = 
						((CoreRebecaNondeterministicTransition<CoreRebecaActorState>) executionResult).getDestinations().iterator();
				while(transitionsIterator.hasNext()) {
					Pair<? extends Action, CoreRebecaActorState> transition = transitionsIterator.next();
					CoreRebecaActorState actorState = transition.getSecond();
					source.setActorState(actorState.getId(), coreRebecaActorState);
					transitions.addDestination(transition.getFirst(), source);
					if(transitionsIterator.hasNext()) {
						source = RebecaStateSerializationUtils.clone(backup);
					}
				}
			} else {
				throw new RebecaRuntimeInterpreterException("Unknown transition type");						
			}
			source = RebecaStateSerializationUtils.clone(backup);
		}

		return transitions;
	}

	@Override
	public CoreRebecaNondeterministicTransition<CoreRebecaSystemState> applyRule(Action action, CoreRebecaSystemState source) {
		throw new RebecaRuntimeInterpreterException("Composition level execute statement rule does not accept input action");	
	}

}
