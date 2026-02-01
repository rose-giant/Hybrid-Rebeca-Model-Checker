package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.TimeSyncHelper;
import org.springframework.stereotype.Component;

@Component
public class HybridRebecaEnvSync1SOSRule extends AbstractHybridSOSRule<HybridRebecaActorState> {

    public HybridRebecaDeterministicTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source) {
        HybridRebecaActorState backup = HybridRebecaStateSerializationUtils.clone(source);
        Pair<Float, Float> now = backup.getNow();
        Pair<Float, Float> resumeTime = backup.getResumeTime();
        Pair<Float, Float> progress = new Pair<>(0f, 0f);

        TimeSyncHelper timeSyncHelper = new TimeSyncHelper();

        progress.setFirst(now.getSecond());
        if ((now.getFirst().floatValue() < resumeTime.getFirst().floatValue()) ||
            (now.getSecond().floatValue() == resumeTime.getFirst().floatValue() &&
            now.getFirst().floatValue() != now.getSecond().floatValue() &&
                    resumeTime.getFirst().floatValue() != resumeTime.getSecond().floatValue())
        ) {
            progress.setSecond(timeSyncHelper.Up(now.getSecond(), resumeTime.getFirst(), resumeTime.getSecond()));
        }

        //considered the maximum time progress:
        backup.setNow(new Pair<>(progress.getFirst(), progress.getSecond()));
        TimeProgressAction action = new TimeProgressAction();
        action.setTimeProgress(progress);

        HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                new HybridRebecaDeterministicTransition<HybridRebecaActorState>();
        result.setAction(action);
        result.setDestination(backup);

        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }
}
