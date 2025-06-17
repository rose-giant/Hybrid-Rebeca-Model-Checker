package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.SendMessageWithAfterInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HybridRebecaSendMessageSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        SendMessageWithAfterInstructionBean msgsrvCall = (SendMessageWithAfterInstructionBean) source.getSecond();
        HybridRebecaMessage message = new HybridRebecaMessage();
        HybridRebecaActorState senderActor = source.getFirst();

        message.setName(msgsrvCall.getMethodName());
        message.setSender(senderActor);
        message.setReceiver((HybridRebecaActorState) senderActor.getVariableValue(msgsrvCall.getBase().getVarName()));

        for(Map.Entry<String, Object> entry : msgsrvCall.getParameters().entrySet()) {
            Object value = entry.getValue();
            if(value instanceof Variable) {
                value = senderActor.getVariableValue(msgsrvCall.getBase().getVarName());
            }
            message.addParameter(entry.getKey(), value);
        }

        if (msgsrvCall.getArrivalInterval() != null) {
            float lowerUpdatedArrival = source.getFirst().getNow().getFirst() + msgsrvCall.getArrivalInterval().getFirst();
            float upperUpdatedArrival = source.getFirst().getNow().getSecond() + msgsrvCall.getArrivalInterval().getSecond();
            Pair<Float, Float> updatedArrival = new Pair<>(lowerUpdatedArrival, upperUpdatedArrival);
            message.setMessageArrivalInterval(updatedArrival);
        } else {
            message.setMessageArrivalInterval(source.getFirst().getNow());
        }

        MessageAction action = new MessageAction(message);
        //ASK Ehsan
        senderActor.movePCtoTheNextInstruction();
        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState,InstructionBean>>();
        result.setDestination(source);
        result.setAction(action);
        System.out.println(result.getDestination());

        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }
}
