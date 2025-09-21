package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.RebecaStateSerializationUtils;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaInternalProgressSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkTransferSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;

@Component
public class HybridRebecaCompositionLevelExecuteStatementSOSRule extends AbstractHybridSOSRule<HybridRebecaSystemState> {

    @Autowired
    HybridRebecaInternalProgressSOSRule hybridRebecaActorLevelExecuteStatementSOSRule = new HybridRebecaInternalProgressSOSRule();

    @Autowired
    HybridRebecaNetworkReceiveSOSRule hybridRebecaNetworkLevelReceiveMessageSOSRule = new HybridRebecaNetworkReceiveSOSRule();

//    public boolean isLastTransitionNonDet = false;

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(HybridRebecaSystemState source) {
//        HybridRebecaNondeterministicTransition<HybridRebecaSystemState> transitions =
//                new HybridRebecaNondeterministicTransition<HybridRebecaSystemState>();
        ArrayList<HybridRebecaAbstractTransition> transitions = new ArrayList<>();

        HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
            for(String actorId : backup.getActorsState().keySet()) {
                HybridRebecaActorState hybridRebecaActorState = source.getActorState(actorId);
                hybridRebecaActorState.setNow(source.getNow());
                if (hybridRebecaActorState.isSuspended() || hybridRebecaActorState.noScopeInstructions()) {
                    continue;
                }

                for (int i = 0 ; i <= hybridRebecaActorState.getSigma().size() ; i++) {
                    if (hybridRebecaActorState.isSuspended()) {
//                        HybridRebecaDeterministicTransition<HybridRebecaActorState> result = new HybridRebecaDeterministicTransition<>();
//                        result.setAction(Action.TAU);
//                        result.setDestination(hybridRebecaActorState);
                        return transitions.get(transitions.size() - 1);
                    }
                    HybridRebecaAbstractTransition<HybridRebecaActorState> executionResult =
                            hybridRebecaActorLevelExecuteStatementSOSRule.applyRule(hybridRebecaActorState);

                    if(executionResult instanceof HybridRebecaDeterministicTransition<HybridRebecaActorState>) {
                        HybridRebecaDeterministicTransition<HybridRebecaActorState> transition =
                                (HybridRebecaDeterministicTransition<HybridRebecaActorState>)executionResult;
                        if(transition.getAction() instanceof MessageAction) {
                            HybridRebecaDeterministicTransition<HybridRebecaNetworkState> networkTransition =
                                    (HybridRebecaDeterministicTransition<HybridRebecaNetworkState>)
                                            hybridRebecaNetworkLevelReceiveMessageSOSRule.applyRule(transition.getAction(), backup.getNetworkState());
                            backup.setNetworkState(networkTransition.getDestination());
                        } else {
                            transition.setAction(Action.TAU);
                        }
                        backup.setActorState(actorId, ((HybridRebecaDeterministicTransition<HybridRebecaActorState>) executionResult).getDestination());
                        transitions.add(new HybridRebecaDeterministicTransition(transition.getAction(), backup));
                    }
                    else if(executionResult instanceof HybridRebecaNondeterministicTransition<HybridRebecaActorState>) {
                        Iterator<Pair<? extends Action, HybridRebecaActorState>> transitionsIterator = executionResult.getDestinations().iterator();

                        while(transitionsIterator.hasNext()) {
                            Pair<? extends Action, HybridRebecaActorState> transition = transitionsIterator.next();
                            HybridRebecaActorState actorState = transition.getSecond();
                            backup.setActorState(actorState.getId(), hybridRebecaActorState);
//                            transitions.addDestination(transition.getFirst(), backup);
                            transitions.add(new HybridRebecaDeterministicTransition(transition.getFirst(), backup));

                            if(transition.getFirst() instanceof MessageAction) {
                                MessageAction action = (MessageAction) transition.getFirst();
                                HybridRebecaDeterministicTransition<HybridRebecaNetworkState> networkTransition =
                                        (HybridRebecaDeterministicTransition<HybridRebecaNetworkState>)
                                                hybridRebecaNetworkLevelReceiveMessageSOSRule.applyRule(action, backup.getNetworkState());
                                backup.setNetworkState(networkTransition.getDestination());
                            }
                        }
                    }

                    else {
                        throw new RebecaRuntimeInterpreterException("Unknown actor transition type");
                    }
                }
            }

        if (transitions.isEmpty()) return new HybridRebecaDeterministicTransition<>(Action.TAU, backup);
        return transitions.get(transitions.size() - 1);
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(Action synchAction, HybridRebecaSystemState source) {
        return null;
    }

}


//            HybridRebecaNetworkState hybridRebecaNetworkState = source.getNetworkState();
//            HybridRebecaAbstractTransition<HybridRebecaNetworkState> executionResult =
//                    hybridRebecaNetworkLevelReceiveMessageSOSRule.applyRule(hybridRebecaNetworkState);
//            if(executionResult instanceof HybridRebecaDeterministicTransition<HybridRebecaNetworkState>) {
//                HybridRebecaDeterministicTransition<HybridRebecaNetworkState> transition =
//                        (HybridRebecaDeterministicTransition<HybridRebecaNetworkState>)executionResult;
//                if(transition.getAction() instanceof MessageAction) {
//                    hybridRebecaNetworkTransferSOSRule.applyRule(
//                            transition.getAction(), source.getNetworkState());
//                }
//                transitions.addDestination(transition.getAction(), source);
//            } else if(executionResult instanceof HybridRebecaNondeterministicTransition<HybridRebecaNetworkState>) {
//                Iterator<Pair<? extends Action, HybridRebecaNetworkState>> transitionsIterator =
//                        ((HybridRebecaNondeterministicTransition<HybridRebecaNetworkState>) executionResult).getDestinations().iterator();
//                while(transitionsIterator.hasNext()) {
//                    Pair<? extends Action, HybridRebecaNetworkState> transition = transitionsIterator.next();
//                    HybridRebecaNetworkState networkState = transition.getSecond();
//                    source.setNetworkState(networkState);
//                    transitions.addDestination(transition.getFirst(), source);
//                    if(transitionsIterator.hasNext()) {
//                        source = HybridRebecaStateSerializationUtils.clone(backup);
//                    }
//                }
//            } else {
//                throw new RebecaRuntimeInterpreterException("Unknown network transition type");
//            }