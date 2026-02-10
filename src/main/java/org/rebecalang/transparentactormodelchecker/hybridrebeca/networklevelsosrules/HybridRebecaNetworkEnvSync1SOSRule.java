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
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.TimeSyncHelper;

import java.util.*;

public class HybridRebecaNetworkEnvSync1SOSRule extends AbstractHybridSOSRule<HybridRebecaNetworkState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(HybridRebecaNetworkState source) {
        ArrayList<Float> bounds = getAllBounds(source);
        Pair<Float, Float> now = source.getNow();
        Pair<Float, Float> progress = new Pair<>(0f, 0f);
        float firstB = bounds.get(0);
        float secondB = bounds.get(1);
        float minEte = source.getMinETE();
        float minEta = source.getMinETA();

        progress.setFirst(now.getSecond().floatValue());
        TimeSyncHelper timeSyncHelper = new TimeSyncHelper();
        if ( (!(minEta < now.getSecond().floatValue()) ) && (!(minEte == now.getSecond().floatValue())) ) {
            progress.setSecond(timeSyncHelper.Up(now.getSecond().floatValue(), firstB, secondB));
        }

        HybridRebecaNetworkState backup = HybridRebecaStateSerializationUtils.clone(source);
        backup.setNow(progress);
        TimeProgressAction action = new TimeProgressAction();
        action.setTimeProgress(progress);

        HybridRebecaDeterministicTransition<HybridRebecaNetworkState> result = new HybridRebecaDeterministicTransition<>();
        result.setAction(action);
        result.setDestination(backup);

        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(Action synchAction, HybridRebecaNetworkState source) {
        return null;
    }

    public ArrayList<Float> getAllBounds(HybridRebecaNetworkState networkState) {
        ArrayList<Float> bounds = new ArrayList<>();
        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : networkState.getReceivedMessages().entrySet()) {
            ArrayList<HybridRebecaMessage> messages = entry.getValue();
            for (HybridRebecaMessage message : messages) {
                bounds.add(message.getMessageArrivalInterval().getFirst());
                bounds.add(message.getMessageArrivalInterval().getSecond());
            }
        }
        List<Float> uniqueSortedBounds = bounds.stream().distinct().sorted().toList();
        bounds.clear();
        for (Float element: uniqueSortedBounds) {
            bounds.add(element);
        }
        if (bounds.size() == 1) bounds.add(bounds.get(0));
        return bounds;
    }
}
