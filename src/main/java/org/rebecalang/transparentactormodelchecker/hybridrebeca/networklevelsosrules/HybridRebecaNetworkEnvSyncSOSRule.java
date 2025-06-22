package org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HybridRebecaNetworkEnvSyncSOSRule  extends AbstractHybridSOSRule<HybridRebecaNetworkState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(HybridRebecaNetworkState source) {
        float minETA = Float.MAX_VALUE;
        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : source.getReceivedMessages().entrySet()) {
            ArrayList<HybridRebecaMessage> messages = entry.getValue();
            for (HybridRebecaMessage message : messages) {
                float etaLowerBound = message.getMessageArrivalInterval().getFirst();
                if (etaLowerBound < minETA) minETA = etaLowerBound;
                if (etaLowerBound < source.getNow().getSecond()) {
                    return null;
                }
            }
        }

        float nowLowerBound = (source.getNow().getFirst() + source.getNow().getSecond()) / 2;
        float nowUpperBound = minETA + (float) 0.1;
        Pair<Float, Float> newNow = new Pair<>(nowLowerBound, nowUpperBound);
        source.setNow(newNow);
        HybridRebecaDeterministicTransition<HybridRebecaNetworkState> result = new HybridRebecaDeterministicTransition<>();
        TimeProgressAction timeProgressAction = new TimeProgressAction();
        result.setAction(timeProgressAction);
        result.setDestination(source);

        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(Action synchAction, HybridRebecaNetworkState source) {
        return null;
    }
}
