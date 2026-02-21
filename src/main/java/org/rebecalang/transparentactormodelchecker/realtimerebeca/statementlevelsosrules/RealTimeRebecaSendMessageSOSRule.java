package org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AbstractCallingInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.MsgsrvCallWithAfterInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RealTimeRebecaSendMessageSOSRule extends AbstractRealTimeSOSRule<Pair<RealTimeRebecaActorState, InstructionBean>> {

    @Override
    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> applyRule(Pair<RealTimeRebecaActorState, InstructionBean> source) {
        AbstractCallingInstructionBean msgsrvCall = (AbstractCallingInstructionBean) source.getSecond();
        float lowerUpdatedArrival = source.getFirst().getNow().getFirst();
        float upperUpdatedArrival = source.getFirst().getNow().getSecond();
        Pair<Float, Float> updatedArrival = new Pair<>(lowerUpdatedArrival, upperUpdatedArrival);

        if (source.getSecond() instanceof MsgsrvCallWithAfterInstructionBean) {
            MsgsrvCallWithAfterInstructionBean msgsrvCallWithAfterInstructionBean = (MsgsrvCallWithAfterInstructionBean) msgsrvCall;
            lowerUpdatedArrival = source.getFirst().getNow().getFirst() + (float)msgsrvCallWithAfterInstructionBean.getArrivalInterval().getFirst();
            upperUpdatedArrival = source.getFirst().getNow().getSecond() + (float)msgsrvCallWithAfterInstructionBean.getArrivalInterval().getSecond();
            updatedArrival = new Pair<>(lowerUpdatedArrival, upperUpdatedArrival);
        }
        RealTimeRebecaMessage message = new RealTimeRebecaMessage();
        RealTimeRebecaActorState senderActor = source.getFirst();

        message.setName(msgsrvCall.getMethodName());
        message.setSender(senderActor);
        if (msgsrvCall.getBase().getVarName().startsWith("self")) {
            message.setReceiver(senderActor);
        } else {
            String receiverName = msgsrvCall.getBase().getVarName();
            RealTimeRebecaActorState receiverState = new RealTimeRebecaActorState(receiverName);
            message.setReceiver(receiverState);
        }

        //TODO: Check for all arg cases
        for(Map.Entry<String, Object> entry : msgsrvCall.getParameters().entrySet()) {
            Object value = entry.getValue();
            if(value instanceof Variable) {
                value = senderActor.getVariableValue(((Variable) value).getVarName());
            }
            message.addParameter(entry.getKey(), value);
        }

        message.setMessageArrivalInterval(updatedArrival);
        MessageAction action = new MessageAction(message);
        senderActor.moveToNextStatement();
        RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =
                new RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState,InstructionBean>>();
        result.setDestination(source);
        result.setAction(action);

        return result;
    }

    @Override
    public RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<RealTimeRebecaActorState, InstructionBean> source) {
        return null;
    }

}
