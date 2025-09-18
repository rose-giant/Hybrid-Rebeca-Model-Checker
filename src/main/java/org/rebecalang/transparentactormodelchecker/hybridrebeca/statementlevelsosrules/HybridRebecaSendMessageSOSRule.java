package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AbstractCallingInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.MsgsrvCallWithAfterInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HybridRebecaSendMessageSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
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
        HybridRebecaMessage message = new HybridRebecaMessage();
        HybridRebecaActorState senderActor = source.getFirst();

        message.setName(msgsrvCall.getMethodName());
        message.setSender(senderActor);
        if (msgsrvCall.getBase().getVarName().startsWith("self")) {
            message.setReceiver(senderActor);
        } else {
            String receiverName = msgsrvCall.getBase().getVarName();
            HybridRebecaActorState receiverState = new HybridRebecaActorState(receiverName);
            message.setReceiver(receiverState);
        }

        //TODO: Check for all arg cases
        for(Map.Entry<String, Object> entry : msgsrvCall.getParameters().entrySet()) {
            Object value = entry.getValue();
            if(value instanceof Variable) {
                value = senderActor.getVariableValue(msgsrvCall.getBase().getVarName());
            }
            message.addParameter(entry.getKey(), value);
        }

        message.setMessageArrivalInterval(updatedArrival);
        MessageAction action = new MessageAction(message);
        senderActor.moveToNextStatement();
        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState,InstructionBean>>();
        result.setDestination(source);
        result.setAction(action);

        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }

}
