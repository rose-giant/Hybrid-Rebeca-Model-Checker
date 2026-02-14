package org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules.HybridRebecaInternalProgressSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules.HybridRebecaNetworkReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;
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
        ArrayList<HybridRebecaAbstractTransition> transitions = new ArrayList<>();
        HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
            for(String actorId : backup.getActorsState().keySet()) {
                HybridRebecaActorState hybridRebecaActorState = source.getActorState(actorId);
                hybridRebecaActorState.setNow(source.getNow());
                if (hybridRebecaActorState.noScopeInstructions()) {
                    continue;
                }

                for (int i = 0 ; i <= hybridRebecaActorState.getSigma().size() ; i++) {
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
                        HybridRebecaNondeterministicTransition result = new HybridRebecaNondeterministicTransition<HybridRebecaSystemState>();
                        Action action = Action.TAU;
                        Iterator<Pair<? extends Action, HybridRebecaActorState>> transitionsIterator = executionResult.getDestinations().iterator();
                        while(transitionsIterator.hasNext()) {
                            HybridRebecaSystemState backup1 = HybridRebecaStateSerializationUtils.clone(source);
                            Pair<? extends Action, HybridRebecaActorState> transition = transitionsIterator.next();
                            HybridRebecaActorState actorState = transition.getSecond();
                            backup1.setActorState(actorState.getId(), actorState);
//                            transitions.addDestination(transition.getFirst(), backup1);
                            transitions.add(new HybridRebecaDeterministicTransition(transition.getFirst(), backup1));
                            if(transition.getFirst() instanceof MessageAction) {
                                action = (MessageAction) transition.getFirst();
                                HybridRebecaDeterministicTransition<HybridRebecaNetworkState> networkTransition =
                                        (HybridRebecaDeterministicTransition<HybridRebecaNetworkState>)
                                                hybridRebecaNetworkLevelReceiveMessageSOSRule.applyRule(action, backup1.getNetworkState());
                                backup1.setNetworkState(networkTransition.getDestination());
                            }
                            result.addDestination(action, backup1);
                        }

                        return result;
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