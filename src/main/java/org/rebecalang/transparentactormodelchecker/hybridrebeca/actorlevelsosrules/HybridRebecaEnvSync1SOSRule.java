package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.stereotype.Component;

@Component
public class HybridRebecaEnvSync1SOSRule {

    public HybridRebecaDeterministicTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source) {
        HybridRebecaActorState backup = HybridRebecaStateSerializationUtils.clone(source);
        Pair<Float, Float> now = backup.getNow();
        Pair<Float, Float> resumeTime = backup.getResumeTime();
        float progressLowerBound = 0, progressUpperBound = 0;

        if(now.getFirst().floatValue() < resumeTime.getFirst().floatValue() &&
                resumeTime.getFirst().floatValue() <= now.getSecond().floatValue()) {
            progressLowerBound = resumeTime.getFirst().floatValue();
            progressUpperBound = resumeTime.getSecond().floatValue();
        }
        else if (now.getSecond().floatValue() < resumeTime.getFirst().floatValue()) {
            progressLowerBound = now.getSecond().floatValue();
            progressUpperBound = resumeTime.getFirst().floatValue();
        }

        backup.setNow(new Pair<>(progressLowerBound, progressUpperBound));
        TimeProgressAction action = new TimeProgressAction();
        action.setTimeProgress(new Pair(progressLowerBound, progressUpperBound));

        HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                new HybridRebecaDeterministicTransition<HybridRebecaActorState>();
        result.setAction(action);
        result.setDestination(backup);

        return result;
    }
}
