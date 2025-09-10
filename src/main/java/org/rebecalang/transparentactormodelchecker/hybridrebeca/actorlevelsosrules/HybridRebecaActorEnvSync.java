package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkEnvSync1SOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkEnvSync2SOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;

public class HybridRebecaActorEnvSync extends AbstractHybridSOSRule<HybridRebecaActorState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source) {
        if (source.messageQueueIsEmpty()) {
            HybridRebecaEnvSync2SOSRule envSync2SOSRule = new HybridRebecaEnvSync2SOSRule();
            return envSync2SOSRule.applyRule(source);
        }
        HybridRebecaEnvSync1SOSRule envSync1SOSRule = new HybridRebecaEnvSync1SOSRule();
        return envSync1SOSRule.applyRule(source);
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }
}
