package org.rebecalang.transparentactormodelchecker.realtimerebeca;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules.HybridRebecaCompositionLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules.HybridRebecaCompositionLevelNetworkDeliverySOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules.HybridRebecaCompositionLevelTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HybridRebecaSOSRule extends AbstractHybridSOSRule<HybridRebecaSystemState> {
    @Autowired
    HybridRebecaCompositionLevelExecuteStatementSOSRule executeStatementSOSRule;

    @Autowired
    HybridRebecaCompositionLevelTakeMessageSOSRule takeMessageSOSRule;

    @Autowired
    HybridRebecaCompositionLevelNetworkDeliverySOSRule networkDeliverySOSRule;

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(HybridRebecaSystemState source) {
        HybridRebecaNondeterministicTransition transitions = new
                HybridRebecaNondeterministicTransition<HybridRebecaSystemState>();
        HybridRebecaSystemState backup = (HybridRebecaSystemState) HybridRebecaStateSerializationUtils.clone(source);

        List<Pair<? extends Action, HybridRebecaSystemState>> destinations = executeStatementSOSRule.applyRule(source).getDestinations();
        transitions.addAllDestinations(destinations);
        if(destinations.size() != 0)
            source = (HybridRebecaSystemState) HybridRebecaStateSerializationUtils.clone(backup);
        destinations = takeMessageSOSRule.applyRule(source).getDestinations();
        transitions.addAllDestinations(destinations);
        if(destinations.size() != 0)
            source = (HybridRebecaSystemState) HybridRebecaStateSerializationUtils.clone(backup);
        destinations = networkDeliverySOSRule.applyRule(source).getDestinations();
        transitions.addAllDestinations(destinations);
        return transitions;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(Action synchAction, HybridRebecaSystemState source) {
        return null;
    }
}
