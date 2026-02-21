package org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;

import java.util.Map;

public class RealTimeRebecaMethodCallSOSRule extends AbstractRealTimeSOSRule<Pair<RealTimeRebecaActorState, InstructionBean>> {
    @Override
    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> applyRule(Pair<RealTimeRebecaActorState, InstructionBean> source) {
        RealTimeRebecaActorState actorState = source.getFirst();
        MethodCallInstructionBean methodCallInstruction = (MethodCallInstructionBean) source.getSecond();
        String methodName = methodCallInstruction.getMethodName();
        if (methodName.equals("delay")) {
            RealTimeRebecaDelaySOSRule delaySOSRule = new RealTimeRebecaDelaySOSRule();
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result = delaySOSRule.applyRule(source);
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

        RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result
                = new RealTimeRebecaDeterministicTransition<>();
        result.setDestination(source);
        result.setAction(Action.TAU);

        return result;
    }

    @Override
    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<RealTimeRebecaActorState, InstructionBean> source) {
        return null;
    }
}