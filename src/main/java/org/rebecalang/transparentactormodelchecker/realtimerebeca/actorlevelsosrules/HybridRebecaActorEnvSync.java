package org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules;

import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;

public class HybridRebecaActorEnvSync extends AbstractHybridSOSRule<HybridRebecaActorState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source) {
        if (source.messageQueueIsEmpty() && source.getNow().getFirst().floatValue() == source.getResumeTime().getFirst().floatValue()) {
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
