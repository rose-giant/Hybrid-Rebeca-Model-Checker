package org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

public class HybridRebecaNetworkEnvSync2SOSRule extends AbstractHybridSOSRule<HybridRebecaNetworkState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(HybridRebecaNetworkState source) {
        if(!source.getReceivedMessages().isEmpty())
            throw new RebecaRuntimeInterpreterException("envSync2 rule is disabled");

        HybridRebecaNetworkState backup = HybridRebecaStateSerializationUtils.clone(source);
        float lowerBound = source.getNow().getSecond().floatValue();
        float upperBound = Float.MAX_VALUE;
        backup.setNow(new Pair<>(lowerBound, upperBound));
        HybridRebecaDeterministicTransition<HybridRebecaNetworkState> result = new HybridRebecaDeterministicTransition<>();
        result.setDestination(backup);
        TimeProgressAction timeAction = new TimeProgressAction();
        timeAction.setTimeProgress(new Pair<>(lowerBound, upperBound));
        result.setAction(timeAction);
        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(Action synchAction, HybridRebecaNetworkState source) {
        return null;
    }
}
