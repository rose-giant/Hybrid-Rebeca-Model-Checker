package org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;

public class RealTimeRebecaEndMethodSOSRule extends AbstractRealTimeSOSRule<Pair<RealTimeRebecaActorState, InstructionBean>> {
    @Override
    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> applyRule(Pair<RealTimeRebecaActorState, InstructionBean> source) {
        source.getFirst().popFromScope();
        RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =
                new RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState,InstructionBean>>();
        result.setDestination(source);
        result.setAction(Action.TAU);
        return result;
    }

    @Override
    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<RealTimeRebecaActorState, InstructionBean> source) {
        return null;
    }
}
