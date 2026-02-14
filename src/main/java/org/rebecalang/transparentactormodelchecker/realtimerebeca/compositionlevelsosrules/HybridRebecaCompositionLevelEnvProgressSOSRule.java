package org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules.HybridRebecaActorEnvSync;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules.HybridRebecaNetworkEnvSync1SOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules.HybridRebecaNetworkEnvSyncSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;

import java.util.*;

public class HybridRebecaCompositionLevelEnvProgressSOSRule extends AbstractHybridSOSRule<HybridRebecaSystemState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(HybridRebecaSystemState source) {
//        return applyGlobalRule(source);
        return applyLocalRule(source);
    }

    private static HybridRebecaDeterministicTransition<HybridRebecaSystemState> applyLocalRule(HybridRebecaSystemState source) {
        ArrayList<Pair<Float, Float>> progressIntervals = new ArrayList<>();
        progressIntervals.add(getNetworkSyncInterval(source));

        HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
        for(String actorId : backup.getActorsState().keySet()) {
            HybridRebecaActorState actorState = source.getActorState(actorId);
            progressIntervals.add(getActorSyncInterval(actorState));
        }

        // compute the correct intersection
        float left = source.getNow().getSecond().floatValue();
        float right = Float.MAX_VALUE;
        for (Pair<Float, Float> interval: progressIntervals) {
            if (interval.getSecond() == null) {
                System.out.println();
            }
            if (interval.getSecond() < right) {
                right = interval.getSecond();
            }
        }

        Pair<Float, Float> newNow = new Pair<>(left, right);
        HybridRebecaNetworkState networkState = source.getNetworkState();
        networkState.setNow(newNow);
        backup.setNetworkState(networkState);

//        TODO: should I remove this??
        for(String actorId : backup.getActorsState().keySet()) {
            HybridRebecaActorState actorState = backup.getActorState(actorId);
            actorState.setNow(newNow);
            if (actorState.getResumeTime().getFirst().floatValue() < newNow.getFirst().floatValue()) {
                actorState.setResumeTime(new Pair<>(newNow.getFirst(), actorState.getResumeTime().getSecond()));
            }
            if (!actorState.isSuspent()) {
                actorState.setResumeTime(newNow);
            }
        }

        HybridRebecaDeterministicTransition<HybridRebecaSystemState> result = new HybridRebecaDeterministicTransition<>();
        TimeProgressAction timeAction = new TimeProgressAction();
        timeAction.setTimeProgress(newNow);
        result.setAction(timeAction);
        backup.setNow(newNow);
        result.setDestination(backup);

        return result;
    }

    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyGlobalRule(HybridRebecaSystemState source) {
        HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
        HybridRebecaNetworkEnvSync1SOSRule networkEnvSync1SOSRule = new HybridRebecaNetworkEnvSync1SOSRule();
        HybridRebecaNetworkState networkState = source.getNetworkState();
        ArrayList<Float> bounds = networkEnvSync1SOSRule.getAllBounds(networkState);
        for(String actorId: source.getActorsIds()) {
            HybridRebecaActorState actorState = source.getActorState(actorId);
            if (actorState.isSuspent()) {
                bounds.add(actorState.getResumeTime().getFirst());
                bounds.add(actorState.getResumeTime().getSecond());
            }
        }
//        bounds.add(source.getNow().getSecond());

        List<Float> uniqueSortedBounds = bounds.stream().distinct().sorted().toList();
        bounds.clear();
        for (Float element: uniqueSortedBounds) {
            bounds.add(element);
        }

        float left = Float.MAX_VALUE;
        float right = Float.MAX_VALUE;

        if (bounds.size() > 1) {
            if (backup.getNow().getFirst().floatValue() == bounds.get(0).floatValue() && backup.getNow().getSecond().floatValue() != 0) {
                left = bounds.get(1);
                right = bounds.get(2);
            } else {
                left = bounds.get(0);
                right = bounds.get(1);
            }
        }
//        if (bounds.size() == 1) left = bounds.get(0);

        Pair<Float, Float> newNow = new Pair<>(left, right);
        networkState.setNow(newNow);
        backup.setNetworkState(networkState);

        for(String actorId : backup.getActorsState().keySet()) {
            HybridRebecaActorState actorState = backup.getActorState(actorId);
            actorState.setNow(newNow);
            if (actorState.getResumeTime().getFirst().floatValue() < newNow.getFirst().floatValue()) {
                actorState.setResumeTime(new Pair<>(newNow.getFirst(), actorState.getResumeTime().getSecond()));
            }
            if (!actorState.isSuspent()) {
                actorState.setResumeTime(newNow);
            } else if(actorState.getResumeTime().getFirst().floatValue() == left &&
                    actorState.getResumeTime().getSecond().floatValue() == right) {
                actorState.setSuspent(false);
            }
        }

        HybridRebecaDeterministicTransition<HybridRebecaSystemState> result = new HybridRebecaDeterministicTransition<>();
        TimeProgressAction timeAction = new TimeProgressAction();
        timeAction.setTimeProgress(newNow);
        result.setAction(timeAction);
        backup.setNow(newNow);
        result.setDestination(backup);

        return result;
    }

    private static Pair<Float, Float> getActorSyncInterval(HybridRebecaActorState source) {
        HybridRebecaActorEnvSync envSync = new HybridRebecaActorEnvSync();
        HybridRebecaDeterministicTransition actorTransition = (HybridRebecaDeterministicTransition) envSync.applyRule(source);
        TimeProgressAction timeAction = (TimeProgressAction) actorTransition.getAction();
        return timeAction.getIntervalTimeProgress();
    }

    private static Pair<Float, Float> getNetworkSyncInterval(HybridRebecaSystemState source) {
        HybridRebecaNetworkEnvSyncSOSRule envSyncSOSRule = new HybridRebecaNetworkEnvSyncSOSRule();
        HybridRebecaAbstractTransition<HybridRebecaNetworkState> networkTransition =
                envSyncSOSRule.applyRule(source.getNetworkState());
        HybridRebecaDeterministicTransition detNetworkTransition = (HybridRebecaDeterministicTransition) networkTransition;
        TimeProgressAction timeAction = (TimeProgressAction) detNetworkTransition.getAction();
        return timeAction.getIntervalTimeProgress();
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(Action synchAction, HybridRebecaSystemState source) {
        return null;
    }

}

