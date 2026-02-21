package org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules;

import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;

public class RealTimeRebecaNetworkEnvSyncSOSRule extends AbstractRealTimeSOSRule<RealTimeRebecaNetworkState> {
    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaNetworkState> applyRule(RealTimeRebecaNetworkState source) {
        if (source.getReceivedMessages().isEmpty()) {
            RealTimeRebecaNetworkEnvSync2SOSRule envSync2SOSRule = new RealTimeRebecaNetworkEnvSync2SOSRule();
            return envSync2SOSRule.applyRule(source);
        }
        if (source.transferable(source)) {
            RealTimeRebecaNetworkEnvSync3SOSRule envSync3SOSRule = new RealTimeRebecaNetworkEnvSync3SOSRule();
            return envSync3SOSRule.applyRule(source);
        }
        if((!source.transferable(source)) && (!source.isEmpty())) {
            RealTimeRebecaNetworkEnvSync1SOSRule envSync1SOSRule = new RealTimeRebecaNetworkEnvSync1SOSRule();
            return envSync1SOSRule.applyRule(source);
        }

        return null;
    }

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaNetworkState> applyRule(Action synchAction, RealTimeRebecaNetworkState source) {
        return null;
    }
}
