package org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;

public class RealTimeRebecaDelaySOSRule extends AbstractRealTimeSOSRule<Pair<RealTimeRebecaActorState, InstructionBean>> {

    @Override
    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState,InstructionBean>> applyRule(Pair<RealTimeRebecaActorState, InstructionBean> source) {
        source.getFirst().setSuspent(true);
        MethodCallInstructionBean methodCallInstruction = (MethodCallInstructionBean) source.getSecond();
        Object delayParam = methodCallInstruction.getParameters().get("interval_bound");
        Float delayLowerBound = (float)0;
        Float delayUpperBound = (float) 0;
        if (delayParam instanceof ContnuousNonDetInstructionBean) {
            ContnuousNonDetInstructionBean delayInterval = (ContnuousNonDetInstructionBean) delayParam;
            delayLowerBound = Float.parseFloat(delayInterval.getLowerBound().toString());
            delayUpperBound = Float.parseFloat(delayInterval.getUpperBound().toString());
        }

        source.getFirst().setResumeTime(new Pair<>(source.getFirst().getResumeTime().getFirst() + delayLowerBound, source.getFirst().getResumeTime().getSecond() + delayUpperBound));
        RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =
                new RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState,InstructionBean>>();

        source.getFirst().moveToNextStatement();
        result.setDestination(source);
        result.setAction(Action.TAU);

        return result;
//        HybridRebecaResumeSOSRule rebecaResumeSOSRule = new HybridRebecaResumeSOSRule();
//        return rebecaResumeSOSRule.applyRule(source);
    }

    private static RealTimeRebecaActorState getHybridRebecaActorState(Pair<RealTimeRebecaActorState, InstructionBean> source) {
        ContnuousNonDetInstructionBean contnuousNonDetInstructionBean = (ContnuousNonDetInstructionBean) source.getSecond();
        RealTimeRebecaActorState realTimeRebecaActorState = source.getFirst();

        Pair<Float, Float> newResumeTime = new Pair<>();
        newResumeTime.setFirst(realTimeRebecaActorState.getResumeTime().getFirst() + (Float) contnuousNonDetInstructionBean.getLowerBound());
        newResumeTime.setSecond(realTimeRebecaActorState.getResumeTime().getSecond() + (Float) contnuousNonDetInstructionBean.getUpperBound());
        realTimeRebecaActorState.setResumeTime(newResumeTime);
        return realTimeRebecaActorState;
    }

    @Override
    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<RealTimeRebecaActorState, InstructionBean> source) {
        return null;
    }
}
