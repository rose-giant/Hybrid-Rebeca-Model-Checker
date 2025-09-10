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
        Pair<Float, Float> progressLower = new Pair<>(0f, 0f), progressUpper = new Pair<>(0f, 0f);

        if(now.getFirst().floatValue() < resumeTime.getFirst().floatValue() &&
                resumeTime.getFirst().floatValue() <= now.getSecond().floatValue()) {
            progressLower.setFirst(now.getFirst());
            progressLower.setSecond(resumeTime.getFirst());

            progressUpper.setFirst(progressLower.getSecond());
            progressUpper.setSecond(resumeTime.getSecond());
        }
        else if (now.getSecond().floatValue() < resumeTime.getFirst().floatValue()) {
            progressLower.setFirst(now.getFirst());
            progressLower.setSecond(now.getSecond());

            progressUpper.setFirst(progressLower.getSecond());
            progressUpper.setSecond(resumeTime.getFirst());
        }

        //considered the maximum time progress:
        backup.setNow(new Pair<>(progressLower.getSecond(), progressUpper.getSecond()));
        TimeProgressAction action = new TimeProgressAction();
        action.setTimeIntervalProgress(new Pair(progressLower, progressUpper));

        HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                new HybridRebecaDeterministicTransition<HybridRebecaActorState>();
        result.setAction(action);
        result.setDestination(backup);

        return result;
    }
}
