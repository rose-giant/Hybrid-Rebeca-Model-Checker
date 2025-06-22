package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaEventListUtils;

public class HybridRebecaCompositionLevelTimeProgressSOSRule extends AbstractHybridSOSRule<HybridRebecaSystemState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(HybridRebecaSystemState source) {
        HybridRebecaSystemState backup = source;
        HybridRebecaSystemState backup2 = source;
        float earliestEventArrival = HybridRebecaEventListUtils.GetEarliestEventArrival(source);
        Pair<Boolean,Pair<Float, Float>> secondEarliestEventArrival = HybridRebecaEventListUtils.getSecondEarliestEventInTheCurrentInterval(source);
        if (!secondEarliestEventArrival.getFirst()) {
            HybridRebecaDeterministicTransition<HybridRebecaSystemState> result =
                    new HybridRebecaDeterministicTransition<HybridRebecaSystemState>();
            Pair<Float, Float> newNow = backup.getNow();
            if (backup.getNow().getFirst() < earliestEventArrival &&
                    earliestEventArrival < backup.getNow().getSecond()) {
                newNow.setFirst(earliestEventArrival);
                newNow.setSecond(source.getNow().getSecond() + (float)0.1);
            }
            backup.setNow(newNow);
            TimeProgressAction timeProgressAction = new TimeProgressAction();
            result.setAction(timeProgressAction);
            result.setDestination(backup);
            return result;
        } else if (earliestEventArrival == source.getNow().getFirst()) {

            HybridRebecaNondeterministicTransition<HybridRebecaSystemState> result =
                    new HybridRebecaNondeterministicTransition<HybridRebecaSystemState>();

            Pair<Float, Float> newNow = backup.getNow();
            if (backup.getNow().getFirst() < earliestEventArrival &&
                    earliestEventArrival < backup.getNow().getSecond()) {
                newNow.setFirst(earliestEventArrival);
                newNow.setSecond(source.getNow().getSecond() + (float)0.1);
            }
            backup.setNow(newNow);
            TimeProgressAction timeProgressAction = new TimeProgressAction();
            result.addDestination(timeProgressAction, backup);

            Pair<Float, Float> newNow2 = backup.getNow();
            newNow2.setFirst(secondEarliestEventArrival.getSecond().getFirst());
            backup2.setNow(newNow2);
            result.addDestination(timeProgressAction, backup2);

            return result;
        }

        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(Action synchAction, HybridRebecaSystemState source) {
        return null;
    }
}
