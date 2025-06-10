package org.rebecalang.transparentactormodelchecker.hybridrebeca;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.RebecaStateSerializationUtils;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules.HybridRebecaCompositionLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules.HybridRebecaCompositionLevelNetworkDeliverySOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules.HybridRebecaCompositionLevelTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
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
        HybridRebecaSystemState backup = (HybridRebecaSystemState) RebecaStateSerializationUtils.clone(source);

        List<Pair<? extends Action, HybridRebecaSystemState>> destinations = executeStatementSOSRule.applyRule(source).getDestinations();
        transitions.addAllDestinations(destinations);
        if(destinations.size() != 0)
            source = (HybridRebecaSystemState) RebecaStateSerializationUtils.clone(backup);
        destinations = takeMessageSOSRule.applyRule(source).getDestinations();
        transitions.addAllDestinations(destinations);
        if(destinations.size() != 0)
            source = (HybridRebecaSystemState) RebecaStateSerializationUtils.clone(backup);
        destinations = networkDeliverySOSRule.applyRule(source).getDestinations();
        transitions.addAllDestinations(destinations);
        return transitions;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }
}
