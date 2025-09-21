package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractTransparentActorState;
import org.rebecalang.transparentactormodelchecker.TransparentActorStateSpace;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApplySystemLevelRules {

    ArrayList<HybridRebecaSystemState> states = new ArrayList<>();
    TransparentActorStateSpace transparentActorStateSpace = new TransparentActorStateSpace();
    HybridRebecaSystemState initialState;
    public ApplySystemLevelRules(HybridRebecaSystemState initialState) {
        levelExecuteStatementSOSRule = new HybridRebecaCompositionLevelExecuteStatementSOSRule();
        this.initialState = initialState;
        startApplyingRules(initialState);
        System.out.println(states.size());
    }

    HybridRebecaCompositionLevelExecuteStatementSOSRule levelExecuteStatementSOSRule;
    HybridRebecaCompositionLevelNetworkDeliverySOSRule networkDeliverySOSRule =
            new HybridRebecaCompositionLevelNetworkDeliverySOSRule();

    public void startApplyingRules(HybridRebecaSystemState initialState){
        HybridRebecaDeterministicTransition<HybridRebecaSystemState> executionResult = new HybridRebecaDeterministicTransition<>();
        executionResult.setDestination(initialState);
        executionResult.setAction(Action.TAU);
        runSystemRules(executionResult);
        System.out.println(transparentActorStateSpace.getStatesNumber());
    }

    public void printStateSpace(HybridRebecaAbstractTransition<HybridRebecaSystemState> executionResult) {
        AbstractTransparentActorState actorState = new AbstractTransparentActorState();
//        System.out.println("s" + states.size());
        actorState.addTransition(executionResult);
        transparentActorStateSpace.addStateToStateSpace(actorState);
    }

    public void printState(String transitionType, Object action, HybridRebecaSystemState systemState) {
        String actionStr = "TAU";
        if (action instanceof MessageAction){
            actionStr = ((MessageAction) action).getActionLabel();
        }
        else if (action instanceof TimeProgressAction) {
            actionStr = ((TimeProgressAction) action).getIntervalTimeProgress().toString();
        }
        if (states.isEmpty()) System.out.println("s" + states.size());
        else {
            System.out.println(transitionType+" ----- "+actionStr+" ----->s" + states.size());
        }
        states.add(systemState);
    }

    public void runSystemRules(HybridRebecaAbstractTransition<HybridRebecaSystemState> executionResult) {
//        printStateSpace(executionResult);
        if (executionResult instanceof HybridRebecaDeterministicTransition<HybridRebecaSystemState>) {
            HybridRebecaDeterministicTransition<HybridRebecaSystemState> source =
                    (HybridRebecaDeterministicTransition<HybridRebecaSystemState>) executionResult;
            HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source.getDestination());
            printState("Det", ((HybridRebecaDeterministicTransition<HybridRebecaSystemState>) executionResult).getAction(),backup);
            if (backup.getNow().getFirst() > backup.getInputInterval().getSecond()) {
                return;
            }
            executionResult = runApplicableRule(backup);
            runSystemRules(executionResult);
        }
        else if (executionResult instanceof HybridRebecaNondeterministicTransition<HybridRebecaSystemState>) {
            HybridRebecaNondeterministicTransition<HybridRebecaSystemState> source =
                    (HybridRebecaNondeterministicTransition<HybridRebecaSystemState>) executionResult;
            Iterator<Pair<? extends Action, HybridRebecaSystemState>> transitionsIterator = (source).getDestinations().iterator();

            while (transitionsIterator.hasNext()) {
                Pair<? extends Action, HybridRebecaSystemState> transition = transitionsIterator.next();
                HybridRebecaSystemState systemState = transition.getSecond();
                printState("Nondet", transition.getFirst(),systemState);
                if (systemState.getNow().getFirst() > systemState.getInputInterval().getSecond()) {
                    return;
                }
                executionResult = runApplicableRule(systemState);
                runSystemRules(executionResult);
            }
        }
    }

    public HybridRebecaAbstractTransition<HybridRebecaSystemState> runApplicableRule(HybridRebecaSystemState backup) {
        if(systemCanExecuteStatements(backup)) {
            HybridRebecaAbstractTransition<HybridRebecaSystemState> result = levelExecuteStatementSOSRule.applyRule(backup);
            if (result != null) {
                return result;
            }
        }

        HybridRebecaCompositionLevelTakeMessageSOSRule takeMessageSOSRule = new HybridRebecaCompositionLevelTakeMessageSOSRule();
        HybridRebecaAbstractTransition<HybridRebecaSystemState> executionResult = takeMessageSOSRule.applyRule(backup);
        if (executionResult != null) {
            return executionResult;
        }

        if (backup.getNetworkState().getReceivedMessages().size() > 0) {
            HybridRebecaAbstractTransition<HybridRebecaSystemState> deliveryResult = networkDeliverySOSRule.applyRule(backup);
            if (deliveryResult != null) {
                return deliveryResult;
            }
        }

//        System.out.println("time to sync!");
        HybridRebecaCompositionLevelEnvProgressSOSRule envProgressSOSRule = new HybridRebecaCompositionLevelEnvProgressSOSRule();
        executionResult = envProgressSOSRule.applyRule(backup);
        return executionResult;
    }

    public boolean systemCanExecuteStatements(HybridRebecaSystemState initialState) {
        for(String actorId : initialState.getActorsState().keySet()) {
            HybridRebecaActorState hybridRebecaActorState = initialState.getActorState(actorId);
            if (!hybridRebecaActorState.noScopeInstructions() && !hybridRebecaActorState.isSuspended()) {
                return true;
            }
        }

        return false;
    }
}