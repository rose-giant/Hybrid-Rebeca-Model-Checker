package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules.HybridRebecaActorEnvSync;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkEnvSyncSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

import java.util.*;

public class HybridRebecaCompositionLevelEnvProgressSOSRule extends AbstractHybridSOSRule<HybridRebecaSystemState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(HybridRebecaSystemState source) {
        ArrayList<Pair<Pair<Float, Float>, Pair<Float, Float>>> progressIntervals = new ArrayList<>();
        progressIntervals.add(getNetworkSyncInterval(source));

        HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
        for(String actorId : backup.getActorsState().keySet()) {
            HybridRebecaActorState actorState = source.getActorState(actorId);
            progressIntervals.add(getActorSyncInterval(actorState));
        }

        //compute the correct intersection
        float left = Float.MAX_VALUE;
        float right = Float.MAX_VALUE;
        for (Pair<Pair<Float, Float>, Pair<Float, Float>> interval: progressIntervals) {
            if (interval.getFirst().getSecond() < left) {
                left = interval.getFirst().getSecond();
            }

            if (interval.getSecond().getSecond() < right) {
                right = interval.getSecond().getSecond();
            }
        }

        Pair<Float, Float> newNow = new Pair<>(left, right);
        HybridRebecaNetworkState networkState = source.getNetworkState();
        networkState.setNow(newNow);

        for(String actorId : backup.getActorsState().keySet()) {
            HybridRebecaActorState actorState = backup.getActorState(actorId);
            actorState.setNow(newNow);
        }

        HybridRebecaDeterministicTransition<HybridRebecaSystemState> result = new HybridRebecaDeterministicTransition<>();
        TimeProgressAction timeAction = new TimeProgressAction();
        timeAction.setTimeProgress(newNow);
        result.setAction(timeAction);
        backup.setNow(newNow);
        result.setDestination(backup);

        return result;
    }

    private static Pair<Pair<Float, Float>, Pair<Float, Float>> getActorSyncInterval(HybridRebecaActorState source) {
        HybridRebecaActorEnvSync envSync = new HybridRebecaActorEnvSync();
        HybridRebecaDeterministicTransition actorTransition = (HybridRebecaDeterministicTransition) envSync.applyRule(source);
        TimeProgressAction timeAction = (TimeProgressAction) actorTransition.getAction();
        return timeAction.getNonDetIntervalTimeProgress();
    }

    private static Pair<Pair<Float, Float>, Pair<Float, Float>> getNetworkSyncInterval(HybridRebecaSystemState source) {
        HybridRebecaNetworkEnvSyncSOSRule envSyncSOSRule = new HybridRebecaNetworkEnvSyncSOSRule();
        HybridRebecaAbstractTransition<HybridRebecaNetworkState> networkTransition =
                envSyncSOSRule.applyRule(source.getNetworkState());
        HybridRebecaDeterministicTransition detNetworkTransition = (HybridRebecaDeterministicTransition) networkTransition;
        TimeProgressAction timeAction = (TimeProgressAction) detNetworkTransition.getAction();
        return timeAction.getNonDetIntervalTimeProgress();
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(Action synchAction, HybridRebecaSystemState source) {
        return null;
    }

}

