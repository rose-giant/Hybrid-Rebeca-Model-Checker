package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractTransparentActorState;
import org.rebecalang.transparentactormodelchecker.TransparentActorStateSpace;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApplySystemLevelRules {

    TransparentActorStateSpace transparentActorStateSpace = new TransparentActorStateSpace();
    HybridRebecaSystemState initialState;
    public ApplySystemLevelRules(HybridRebecaSystemState initialState) {
        levelExecuteStatementSOSRule = new HybridRebecaCompositionLevelExecuteStatementSOSRule();
        this.initialState = initialState;
        startApplyingRules(initialState);
    }

    HybridRebecaCompositionLevelExecuteStatementSOSRule levelExecuteStatementSOSRule;
    HybridRebecaCompositionLevelNetworkDeliverySOSRule networkDeliverySOSRule =
            new HybridRebecaCompositionLevelNetworkDeliverySOSRule();

    //TODO: communication of Network --> Actor is handled but after receiving no scope is added to the actor's scopes
    public void startApplyingRules(HybridRebecaSystemState initialState){
        HybridRebecaAbstractTransition<HybridRebecaSystemState> executionResult;
        HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(initialState);
        initialState.setNow(new Pair<>((float)0, (float)0));

        while (backup.getNow().getFirst() <= backup.getInputInterval().getSecond()) {

            executionResult = levelExecuteStatementSOSRule.applyRule(backup);
            if (executionResult instanceof HybridRebecaDeterministicTransition<HybridRebecaSystemState>) {
                HybridRebecaDeterministicTransition<HybridRebecaSystemState> source =
                        (HybridRebecaDeterministicTransition<HybridRebecaSystemState>) executionResult;
                backup = HybridRebecaStateSerializationUtils.clone(source.getDestination());

                if (source.getDestination().getNetworkState().getReceivedMessages().size() > 0) {
                    HybridRebecaAbstractTransition<HybridRebecaSystemState> deliveryResult =
                            networkDeliverySOSRule.applyRule(source.getDestination());

                    executionResult = deliveryResult;
                }

                if (!systemCanExecuteStatements(source.getDestination()) && executionResult == null) {
                    System.out.println("time to sync!");
                    HybridRebecaCompositionLevelEnvProgressSOSRule envProgressSOSRule = new HybridRebecaCompositionLevelEnvProgressSOSRule();
                    executionResult = envProgressSOSRule.applyRule(backup);
                    backup = (HybridRebecaSystemState) ((HybridRebecaDeterministicTransition)executionResult).getDestination();
//                    break;
                } else if(executionResult != null) {

                }
            }

            else if (executionResult instanceof HybridRebecaNondeterministicTransition<HybridRebecaSystemState>) {
                HybridRebecaNondeterministicTransition<HybridRebecaSystemState> source =
                        (HybridRebecaNondeterministicTransition<HybridRebecaSystemState>) executionResult;
                Iterator<Pair<? extends Action, HybridRebecaSystemState>> transitionsIterator = (source).getDestinations().iterator();
                while (transitionsIterator.hasNext()) {
                    Pair<? extends Action, HybridRebecaSystemState> transition = transitionsIterator.next();
                    HybridRebecaSystemState systemState = transition.getSecond();

                    if (systemState.getNetworkState().getReceivedMessages().size() > 0) {
                        HybridRebecaAbstractTransition<HybridRebecaSystemState> deliveryResult =
                                networkDeliverySOSRule.applyRule(systemState);

                        executionResult = deliveryResult;
                    }

                    if (!systemCanExecuteStatements(systemState) && executionResult == null) {
                        System.out.println("time to sync!");
                        HybridRebecaCompositionLevelEnvProgressSOSRule envProgressSOSRule = new HybridRebecaCompositionLevelEnvProgressSOSRule();
                        executionResult = envProgressSOSRule.applyRule(backup);
                        backup = (HybridRebecaSystemState) ((HybridRebecaDeterministicTransition)executionResult).getDestination();
//                        break;
                    }
                }
            }

//            if (executionResult == null) {
//                //TODO: Call envProgress Method and pass the current system state to it and progress the global time
//                System.out.println("time to sync!");
//                break;
//            }
            AbstractTransparentActorState transparentActorState = new AbstractTransparentActorState();
            transparentActorStateSpace.addStateToStateSpace(transparentActorState);
        }

        System.out.println("state number: "+transparentActorStateSpace.getStatesNumber());
    }

    public boolean systemCanExecuteStatements(HybridRebecaSystemState initialState) {
        for(String actorId : initialState.getActorsState().keySet()) {
            HybridRebecaActorState hybridRebecaActorState = initialState.getActorState(actorId);
            if (!hybridRebecaActorState.noScopeInstructions()) {
                return true;
            }
        }

        return false;
    }
}

