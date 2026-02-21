package org.rebecalang.transparentactormodelchecker.realtimerebeca;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules.RealTimeRebecaCompositionLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules.RealTimeRebecaCompositionLevelNetworkDeliverySOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules.RealTimeRebecaCompositionLevelTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RealTimeRebecaSOSRule extends AbstractRealTimeSOSRule<RealTimeRebecaSystemState> {
    @Autowired
    RealTimeRebecaCompositionLevelExecuteStatementSOSRule executeStatementSOSRule;

    @Autowired
    RealTimeRebecaCompositionLevelTakeMessageSOSRule takeMessageSOSRule;

    @Autowired
    RealTimeRebecaCompositionLevelNetworkDeliverySOSRule networkDeliverySOSRule;

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaSystemState> applyRule(RealTimeRebecaSystemState source) {
        RealTimeRebecaNondeterministicTransition transitions = new
                RealTimeRebecaNondeterministicTransition<RealTimeRebecaSystemState>();
        RealTimeRebecaSystemState backup = (RealTimeRebecaSystemState) HybridRebecaStateSerializationUtils.clone(source);

        List<Pair<? extends Action, RealTimeRebecaSystemState>> destinations = executeStatementSOSRule.applyRule(source).getDestinations();
        transitions.addAllDestinations(destinations);
        if(destinations.size() != 0)
            source = (RealTimeRebecaSystemState) HybridRebecaStateSerializationUtils.clone(backup);
        destinations = takeMessageSOSRule.applyRule(source).getDestinations();
        transitions.addAllDestinations(destinations);
        if(destinations.size() != 0)
            source = (RealTimeRebecaSystemState) HybridRebecaStateSerializationUtils.clone(backup);
        destinations = networkDeliverySOSRule.applyRule(source).getDestinations();
        transitions.addAllDestinations(destinations);
        return transitions;
    }

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaSystemState> applyRule(Action synchAction, RealTimeRebecaSystemState source) {
        return null;
    }
}
