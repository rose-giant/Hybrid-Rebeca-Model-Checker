package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

import java.util.ArrayList;

public class HybridRebecaMethodCallSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {
    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        HybridRebecaActorState actorState = source.getFirst();
        MethodCallInstructionBean methodCallInstruction = (MethodCallInstructionBean) source.getSecond();
        String methodName = methodCallInstruction.getMethodName();
        if (methodName.equals("delay")) {
            HybridRebecaDelaySOSRule delaySOSRule = new HybridRebecaDelaySOSRule();
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>result = delaySOSRule.applyRule(source);
            return result;
        }

        ArrayList<InstructionBean> methodInstructionList = actorState.getRILModel().getInstructionList(methodName);

        actorState.setCurrentBlockName(methodName);

        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}