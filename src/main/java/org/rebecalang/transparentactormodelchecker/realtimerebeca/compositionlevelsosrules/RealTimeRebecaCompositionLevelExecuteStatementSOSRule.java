package org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules.RealTimeRebecaInternalProgressSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules.RealTimeRebecaNetworkReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;

@Component
public class RealTimeRebecaCompositionLevelExecuteStatementSOSRule extends AbstractRealTimeSOSRule<RealTimeRebecaSystemState> {

    @Autowired
    RealTimeRebecaInternalProgressSOSRule hybridRebecaActorLevelExecuteStatementSOSRule = new RealTimeRebecaInternalProgressSOSRule();

    @Autowired
    RealTimeRebecaNetworkReceiveSOSRule hybridRebecaNetworkLevelReceiveMessageSOSRule = new RealTimeRebecaNetworkReceiveSOSRule();

//    public boolean isLastTransitionNonDet = false;

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaSystemState> applyRule(RealTimeRebecaSystemState source) {
        ArrayList<RealTimeRebecaAbstractTransition> transitions = new ArrayList<>();
        RealTimeRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
            for(String actorId : backup.getActorsState().keySet()) {
                RealTimeRebecaActorState realTimeRebecaActorState = source.getActorState(actorId);
                realTimeRebecaActorState.setNow(source.getNow());
                if (realTimeRebecaActorState.noScopeInstructions()) {
                    continue;
                }

                for (int i = 0; i <= realTimeRebecaActorState.getSigma().size() ; i++) {
                    if (realTimeRebecaActorState.isSuspent()) {
                        continue;
                    }
                    RealTimeRebecaAbstractTransition<RealTimeRebecaActorState> executionResult =
                            hybridRebecaActorLevelExecuteStatementSOSRule.applyRule(realTimeRebecaActorState);

                    if(executionResult instanceof RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState>) {
                        RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState> transition =
                                (RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState>)executionResult;
                        if(transition.getAction() instanceof MessageAction) {
                            RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState> networkTransition =
                                    (RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState>)
                                            hybridRebecaNetworkLevelReceiveMessageSOSRule.applyRule(transition.getAction(), backup.getNetworkState());
                            backup.setNetworkState(networkTransition.getDestination());
                        } else {
                            transition.setAction(Action.TAU);
                        }
                        backup.setActorState(actorId, ((RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState>) executionResult).getDestination());
                        transitions.add(new RealTimeRebecaDeterministicTransition(transition.getAction(), backup));
                    }
                    else if(executionResult instanceof RealTimeRebecaNondeterministicTransition<RealTimeRebecaActorState>) {
                        RealTimeRebecaNondeterministicTransition result = new RealTimeRebecaNondeterministicTransition<RealTimeRebecaSystemState>();
                        Action action = Action.TAU;
                        Iterator<Pair<? extends Action, RealTimeRebecaActorState>> transitionsIterator = executionResult.getDestinations().iterator();
                        while(transitionsIterator.hasNext()) {
                            RealTimeRebecaSystemState backup1 = HybridRebecaStateSerializationUtils.clone(source);
                            Pair<? extends Action, RealTimeRebecaActorState> transition = transitionsIterator.next();
                            RealTimeRebecaActorState actorState = transition.getSecond();
                            backup1.setActorState(actorState.getId(), actorState);
//                            transitions.addDestination(transition.getFirst(), backup1);
                            transitions.add(new RealTimeRebecaDeterministicTransition(transition.getFirst(), backup1));
                            if(transition.getFirst() instanceof MessageAction) {
                                action = (MessageAction) transition.getFirst();
                                RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState> networkTransition =
                                        (RealTimeRebecaDeterministicTransition<RealTimeRebecaNetworkState>)
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

        if (transitions.isEmpty()) return new RealTimeRebecaDeterministicTransition<>(Action.TAU, backup);
        return transitions.get(transitions.size() - 1);
    }

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaSystemState> applyRule(Action synchAction, RealTimeRebecaSystemState source) {
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