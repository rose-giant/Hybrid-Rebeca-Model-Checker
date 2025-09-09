package org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class HybridRebecaNetworkEnvSync1SOSRule extends AbstractHybridSOSRule<HybridRebecaNetworkState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(HybridRebecaNetworkState source) {
        ArrayList<Float> bounds = getAllBounds(source);
        Pair<Float, Float> now = source.getNow();
        float progressLowerBound=0, progressUpperBound=0;
        float firstB = bounds.get(0);
        float secondB = bounds.get(1);

        if (now.getSecond() < firstB) {
            progressUpperBound = now.getSecond();
            progressUpperBound = firstB;
        }
        else if (firstB < now.getSecond() && secondB <= now.getSecond()) {
            progressLowerBound = firstB;
            progressUpperBound = now.getSecond();
        }
        else if(firstB < now.getSecond() && now.getSecond() < secondB) {
            progressLowerBound = firstB;
            progressUpperBound = secondB;
        } else if (firstB == now.getSecond()) {
            progressLowerBound = firstB;
            progressUpperBound = secondB;
        }

        HybridRebecaNetworkState backup = HybridRebecaStateSerializationUtils.clone(source);
        backup.setNow(new Pair<>(progressLowerBound, progressUpperBound));
        TimeProgressAction action = new TimeProgressAction();
        action.setTimeProgress(new Pair<>(progressLowerBound, progressUpperBound));

        HybridRebecaDeterministicTransition<HybridRebecaNetworkState> result = new HybridRebecaDeterministicTransition<>();
        result.setAction(action);
        result.setDestination(backup);

        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(Action synchAction, HybridRebecaNetworkState source) {
        return null;
    }

    private ArrayList<Float> getAllBounds(HybridRebecaNetworkState networkState) {
        ArrayList<Float> bounds = new ArrayList<>();
        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : networkState.getReceivedMessages().entrySet()) {
            ArrayList<HybridRebecaMessage> messages = entry.getValue();
            for (HybridRebecaMessage message : messages) {
                bounds.add(message.getMessageArrivalInterval().getFirst());
                bounds.add(message.getMessageArrivalInterval().getSecond());
            }
        }
        Collections.sort(bounds);
        return bounds;
    }
}
