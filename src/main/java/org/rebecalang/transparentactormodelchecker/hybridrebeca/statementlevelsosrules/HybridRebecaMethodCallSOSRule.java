package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.ActorScope;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

import java.util.ArrayList;
import java.util.Map;

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

//        HybridRebecaActorState backup = HybridRebecaStateSerializationUtils.clone(source.getFirst());
//        backup.setRILModel(source.getFirst().getRILModel());
        source.getFirst().moveToNextStatement();
        source.getFirst().addScope(methodName);
        for(Map.Entry<String, Object> entry : methodCallInstruction.getParameters().entrySet()) {
            Object value = entry.getValue();
            if(value instanceof Variable) {
                Object variableValue = source.getFirst().getVariableValue(entry.getValue().toString());
                source.getFirst().addVariableToScope(entry.getKey().toString(), variableValue);
            }
            methodCallInstruction.addParameter(entry.getKey(), value);
        }

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result
                = new HybridRebecaDeterministicTransition<>();
        result.setDestination(source);
        result.setAction(Action.TAU);

        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}