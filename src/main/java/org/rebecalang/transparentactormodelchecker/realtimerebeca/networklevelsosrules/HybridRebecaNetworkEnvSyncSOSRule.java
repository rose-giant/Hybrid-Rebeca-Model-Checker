package org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules;

import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;

public class HybridRebecaNetworkEnvSyncSOSRule  extends AbstractHybridSOSRule<HybridRebecaNetworkState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(HybridRebecaNetworkState source) {
        if (source.getReceivedMessages().isEmpty()) {
            HybridRebecaNetworkEnvSync2SOSRule envSync2SOSRule = new HybridRebecaNetworkEnvSync2SOSRule();
            return envSync2SOSRule.applyRule(source);
        }
        HybridRebecaNetworkEnvSync1SOSRule envSync1SOSRule = new HybridRebecaNetworkEnvSync1SOSRule();
        return envSync1SOSRule.applyRule(source);
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(Action synchAction, HybridRebecaNetworkState source) {
        return null;
    }
}
