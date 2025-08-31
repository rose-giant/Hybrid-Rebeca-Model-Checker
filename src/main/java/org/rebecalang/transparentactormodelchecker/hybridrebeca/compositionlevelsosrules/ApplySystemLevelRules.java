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
import java.util.Map;

public class ApplySystemLevelRules {

    TransparentActorStateSpace transparentActorStateSpace = new TransparentActorStateSpace();
    HybridRebecaSystemState initialState;
    public ApplySystemLevelRules(HybridRebecaSystemState initialState) {
        levelExecuteStatementSOSRule = new HybridRebecaCompositionLevelExecuteStatementSOSRule();
        this.initialState = initialState;
        startApplyingRules(initialState);
    }

//    @Autowired
    HybridRebecaCompositionLevelExecuteStatementSOSRule levelExecuteStatementSOSRule;
    HybridRebecaCompositionLevelNetworkDeliverySOSRule networkDeliverySOSRule =
            new HybridRebecaCompositionLevelNetworkDeliverySOSRule();

    public void startApplyingRules(HybridRebecaSystemState initialState){
        HybridRebecaAbstractTransition<HybridRebecaSystemState> executionResult = new HybridRebecaDeterministicTransition<>();
        HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(initialState);
        initialState.setNow(new Pair<>((float)0, (float)0));
        executionResult = levelExecuteStatementSOSRule.applyRule(initialState);

        if (executionResult instanceof HybridRebecaDeterministicTransition<HybridRebecaSystemState>) {
            HybridRebecaDeterministicTransition<HybridRebecaSystemState> source =
                    (HybridRebecaDeterministicTransition<HybridRebecaSystemState>) executionResult;

            if (systemCanExecuteStatements(((HybridRebecaDeterministicTransition<HybridRebecaSystemState>) executionResult).getDestination())) {
                startApplyingRules(((HybridRebecaDeterministicTransition<HybridRebecaSystemState>) executionResult).getDestination());
            }

            if (source.getDestination().getNetworkState().getReceivedMessages().size() > 0) {
                HybridRebecaAbstractTransition<HybridRebecaSystemState> deliveryResult =
                        networkDeliverySOSRule.applyRule(source.getDestination());

                executionResult = deliveryResult;
            }
        } else if (executionResult instanceof HybridRebecaNondeterministicTransition<HybridRebecaSystemState>) {
            HybridRebecaNondeterministicTransition<HybridRebecaSystemState> source =
                    (HybridRebecaNondeterministicTransition<HybridRebecaSystemState>) executionResult;

            Iterator<Pair<? extends Action, HybridRebecaSystemState>> transitionsIterator = (source).getDestinations().iterator();
            while(transitionsIterator.hasNext()) {
                Pair<? extends Action, HybridRebecaSystemState> transition = transitionsIterator.next();
                HybridRebecaSystemState systemState = transition.getSecond();

                if (systemCanExecuteStatements(systemState)) {
                    startApplyingRules(systemState);
                }

                if (systemState.getNetworkState().getReceivedMessages().size() > 0) {
                    HybridRebecaAbstractTransition<HybridRebecaSystemState> deliveryResult =
                            networkDeliverySOSRule.applyRule(systemState);

                    executionResult = deliveryResult;
                }
            }
        }

        AbstractTransparentActorState transparentActorState = new AbstractTransparentActorState();
        transparentActorStateSpace.addStateToStateSpace(transparentActorState);
        System.out.println("state number: "+transparentActorStateSpace.getStatesNumber());
    }

    public boolean systemCanExecuteStatements(HybridRebecaSystemState initialState) {
        for(String actorId : initialState.getActorsState().keySet()) {
            HybridRebecaActorState hybridRebecaActorState = initialState.getActorState(actorId);
            if (!hybridRebecaActorState.getSigma().isEmpty()) {
                return true;
            }
        }

        return false;
    }
}

