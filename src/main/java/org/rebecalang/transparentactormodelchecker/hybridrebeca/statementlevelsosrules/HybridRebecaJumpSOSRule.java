package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import static org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridExpressionEvaluator.hybridExpressionEvaluator;

@Component
public class HybridRebecaJumpSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {

    //if: pc = 5
    //else: pc = 9
    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(source.getFirst());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();   // THIS is what we want
        }

        HybridRebecaActorState backup1 = HybridRebecaStateSerializationUtils.clone(source.getFirst());
        backup1.setRILModel(source.getFirst().getRILModel());
        HybridRebecaActorState backup2 = HybridRebecaStateSerializationUtils.clone(source.getFirst());
        backup2.setRILModel(source.getFirst().getRILModel());

        JumpIfNotInstructionBean jumpIfNotInstructionBean = (JumpIfNotInstructionBean) source.getSecond();
        HybridRebecaActorState originalState = HybridRebecaStateSerializationUtils.clone(source.getFirst());
        Pair<HybridRebecaActorState, InstructionBean> originalSource = new Pair<>();
        originalSource.setFirst(originalState);
        originalSource.setSecond(source.getSecond());
        Boolean conditionEval = null;
        if (jumpIfNotInstructionBean.getCondition() instanceof Variable) {
            Variable var = (Variable) jumpIfNotInstructionBean.getCondition();
            conditionEval = (Boolean) originalState.getVariableValue(var.getVarName());
        } else {
            conditionEval = (boolean) hybridExpressionEvaluator(source);
        }

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> ifResult =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState,InstructionBean>>();
        backup1.moveToNextStatement();
        source.setFirst(backup1);
        ifResult.setDestination(source);
        ifResult.setAction(Action.TAU);

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> elseResult =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState,InstructionBean>>();
        backup2.jumpToBranchInstruction(jumpIfNotInstructionBean.getLineNumber());
        originalSource.setFirst(backup2);
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
