package org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class HybridRebecaVariableDeclarationSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {
    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        DeclarationInstructionBean vdib = (DeclarationInstructionBean) source.getSecond();
        source.getFirst().addVariableToScope(vdib.getVarName());
        source.getFirst().moveToNextStatement();

        HybridRebecaDeterministicTransition<org.rebecalang.compiler.utils.Pair<HybridRebecaActorState, InstructionBean>> result =
                new HybridRebecaDeterministicTransition<>();
        result.setDestination(source);
        result.setAction(Action.TAU);
        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<org.rebecalang.compiler.utils.Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, org.rebecalang.compiler.utils.Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}
