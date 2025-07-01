package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkEnvSyncSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HybridRebecaCompositionLevelEnvProgressSOSRule extends AbstractHybridSOSRule<HybridRebecaSystemState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(HybridRebecaSystemState source) {
        HybridRebecaNetworkState networkState = source.getNetworkState();
        HybridRebecaNetworkEnvSyncSOSRule networkEnvSyncSOSRule = new HybridRebecaNetworkEnvSyncSOSRule();
        HashMap<String, HybridRebecaActorState> actorStates = source.getActorsState();

        boolean isEnvProgressAllowed = true;
        ArrayList<Float> etas = new ArrayList<>();
        etas.add(networkState.getMinETA());
        for (Map.Entry<String, HybridRebecaActorState> entry : actorStates.entrySet()) {
            HybridRebecaActorState actorState = entry.getValue();
            HybridRebecaMessage firstMessage = actorState.getFirstMessage();
            if (firstMessage != null) {
                Float lowerBound = firstMessage.getMessageArrivalInterval().getFirst();
                etas.add(lowerBound);
            }
        }
        Float minETA = Collections.min(etas);
        HybridRebecaDeterministicTransition<HybridRebecaSystemState> result = new HybridRebecaDeterministicTransition<>();
        if (minETA > source.getNow().getSecond()) {
            Pair<Float, Float> newNow = new Pair<>();
            newNow.setFirst((source.getNow().getSecond() + minETA) / 2);
            float timeShift = newNow.getFirst() - source.getNow().getFirst();
            newNow.setSecond(source.getNow().getSecond() + timeShift);
            source.setNow(newNow);
            TimeProgressAction timeProgressAction = new TimeProgressAction();
            timeProgressAction.setTimeProgress(timeShift);
            result.setDestination(source);
            result.setAction(timeProgressAction);
        } else {
            result.setAction(Action.TAU);
            result.setDestination(source);
        }
        return result;

    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(Action synchAction, HybridRebecaSystemState source) {
        return null;
    }
}

