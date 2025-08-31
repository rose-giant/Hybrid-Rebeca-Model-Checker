package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.stereotype.Component;

import static org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridExpressionEvaluator.hybridExpressionEvaluator;

@Component
public class HybridRebecaJumpSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        JumpIfNotInstructionBean jumpIfNotInstructionBean = (JumpIfNotInstructionBean) source.getSecond();
        HybridRebecaActorState originalState = HybridRebecaStateSerializationUtils.clone(source.getFirst());
        Pair<HybridRebecaActorState, InstructionBean> originalSource = new Pair<>();
        originalSource.setFirst(originalState);
        originalSource.setSecond(source.getSecond());
        Boolean conditionEval = null;
        if (jumpIfNotInstructionBean.getCondition() instanceof Variable) {
            Variable var = (Variable) jumpIfNotInstructionBean.getCondition();
            Object conditionVar = originalState.getVariableValue(var.getVarName());
        }
//        Boolean conditionEval = (boolean) hybridExpressionEvaluator(jumpIfNotInstructionBean.getCondition());

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> ifResult =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState,InstructionBean>>();
        source.getFirst().jumpToBranchInstruction(jumpIfNotInstructionBean.getLineNumber());
        ifResult.setDestination(source);
        ifResult.setAction(Action.TAU);

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> elseResult =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState,InstructionBean>>();
        originalSource.getFirst().movePCtoTheNextInstruction();
        elseResult.setDestination(originalSource);
        elseResult.setAction(Action.TAU);

        if (conditionEval == null) {
            HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> nondetResult =
                    new HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState,InstructionBean>>();
            nondetResult.addDestination(ifResult.getAction(), ifResult.getDestination());
            nondetResult.addDestination(elseResult.getAction(), elseResult.getDestination());
            return nondetResult;
        }

        if (conditionEval) {
            return ifResult;
        }
        else {
            return elseResult;
        }
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}
