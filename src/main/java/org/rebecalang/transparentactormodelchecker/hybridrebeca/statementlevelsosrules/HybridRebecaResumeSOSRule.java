package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;

public class HybridRebecaResumeSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {
    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        //TODO
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}

//if(sourceNow.getFirst().equals(sourceResumeTime.getFirst()) &&
//        sourceNow.getSecond() < sourceResumeTime.getSecond() ) {
//HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaNondeterministicTransition<>();
//Pair<HybridRebecaActorState, InstructionBean> resultInnerPair = new Pair<>(originalSource, source.getSecond());
//
//            originalSource.setResumeTime(sourceNow);
//            result.addDestination(Action.TAU, resultInnerPair);
//
//HybridRebecaActorState sourceState = source.getFirst();
//Pair<Float, Float> postponeResumeTime = new Pair<>(sourceNow.getSecond() ,sourceResumeTime.getSecond());
//            sourceState.setResumeTime(postponeResumeTime);
//TimeProgressAction timeProgressAction = new TimeProgressAction();
//Pair<HybridRebecaActorState, InstructionBean> resultInnerPair2 = new Pair<>(sourceState, source.getSecond());
//
//            result.addDestination(timeProgressAction, resultInnerPair2);
//            return result;
//        }
//                else if(sourceNow.getFirst().equals(sourceResumeTime.getFirst())) {
//HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaDeterministicTransition<>();
//HybridRebecaActorState sourceState = source.getFirst();
//            sourceState.setResumeTime(sourceNow);
//Pair<HybridRebecaActorState, InstructionBean> resultInnerPair = new Pair<>(sourceState, source.getSecond());
//            result.setDestination(resultInnerPair);
//            result.setAction(Action.TAU);
//            return result;
//        }