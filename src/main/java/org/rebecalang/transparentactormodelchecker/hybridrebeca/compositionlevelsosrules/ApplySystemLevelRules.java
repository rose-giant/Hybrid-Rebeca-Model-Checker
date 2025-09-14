package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractTransparentActorState;
import org.rebecalang.transparentactormodelchecker.TransparentActorStateSpace;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
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
    
    public void startApplyingRules(HybridRebecaSystemState initialState){
        HybridRebecaDeterministicTransition<HybridRebecaSystemState> executionResult = new HybridRebecaDeterministicTransition<>();
        executionResult.setDestination(initialState);
        executionResult.setAction(Action.TAU);

        runSystemRules(executionResult);
        AbstractTransparentActorState transparentActorState = new AbstractTransparentActorState();
        transparentActorStateSpace.addStateToStateSpace(transparentActorState);
    }

    public void runSystemRules(HybridRebecaAbstractTransition<HybridRebecaSystemState> executionResult) {

        if (executionResult instanceof HybridRebecaDeterministicTransition<HybridRebecaSystemState>) {
            HybridRebecaDeterministicTransition<HybridRebecaSystemState> source =
                    (HybridRebecaDeterministicTransition<HybridRebecaSystemState>) executionResult;
            HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source.getDestination());
            if (backup.getNow().getFirst() > backup.getInputInterval().getSecond()) {
                return;
            }
            executionResult = runApplicableRule(backup);
            //make the state
            runSystemRules(executionResult);
        }
        else if (executionResult instanceof HybridRebecaNondeterministicTransition<HybridRebecaSystemState>) {
            HybridRebecaNondeterministicTransition<HybridRebecaSystemState> source =
                    (HybridRebecaNondeterministicTransition<HybridRebecaSystemState>) executionResult;
            Iterator<Pair<? extends Action, HybridRebecaSystemState>> transitionsIterator = (source).getDestinations().iterator();

            while (transitionsIterator.hasNext()) {
                Pair<? extends Action, HybridRebecaSystemState> transition = transitionsIterator.next();
                HybridRebecaSystemState systemState = transition.getSecond();
                if (systemState.getNow().getFirst() > systemState.getInputInterval().getSecond()) {
                    return;
                }
                executionResult = runApplicableRule(systemState);
                //make the state
                runSystemRules(executionResult);
            }
        }
    }

    public HybridRebecaAbstractTransition<HybridRebecaSystemState> runApplicableRule(HybridRebecaSystemState backup) {
        if(systemCanExecuteStatements(backup)) {
            HybridRebecaAbstractTransition<HybridRebecaSystemState> result = levelExecuteStatementSOSRule.applyRule(backup);
            return result;
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

        System.out.println("time to sync!");
        HybridRebecaCompositionLevelEnvProgressSOSRule envProgressSOSRule = new HybridRebecaCompositionLevelEnvProgressSOSRule();
        executionResult = envProgressSOSRule.applyRule(backup);
        return executionResult;
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