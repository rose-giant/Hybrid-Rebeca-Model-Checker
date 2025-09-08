package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

public class HybridRebecaResumeSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {
    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        Pair<Float, Float> now = source.getFirst().getNow();
        Pair<Float, Float> resumeTime = source.getFirst().getResumeTime();

        if (now.getFirst().floatValue() == resumeTime.getFirst().floatValue() && now.getSecond().floatValue() < resumeTime.getSecond().floatValue()) {
            HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaNondeterministicTransition<>();
            HybridRebecaActorState backup1 = HybridRebecaStateSerializationUtils.clone(source.getFirst());
            backup1.setResumeTime(now);
            Pair<HybridRebecaActorState, InstructionBean> newSource = new Pair<>(backup1, source.getSecond());
            result.addDestination(Action.TAU, newSource);

            HybridRebecaActorState backup2 = HybridRebecaStateSerializationUtils.clone(source.getFirst());
            backup2.setResumeTime(new Pair<>(now.getSecond(), resumeTime.getSecond()));
            Pair<HybridRebecaActorState, InstructionBean> newSource2 = new Pair<>(backup2, source.getSecond());
            result.addDestination(Action.TAU, newSource2);
            return result;
        }
        if (now.getFirst().floatValue() == resumeTime.getFirst().floatValue()) {
            HybridRebecaActorState backup = HybridRebecaStateSerializationUtils.clone(source.getFirst());
            backup.setResumeTime(source.getFirst().getNow());
            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaDeterministicTransition<>();
            source.setFirst(backup);
            result.setDestination(source);
            return result;
        }

        //TODO: what if none is applicable?
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}