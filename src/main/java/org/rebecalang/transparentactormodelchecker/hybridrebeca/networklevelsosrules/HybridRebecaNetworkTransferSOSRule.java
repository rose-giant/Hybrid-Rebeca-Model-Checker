package org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

import java.util.*;

import static java.lang.System.in;

public class HybridRebecaNetworkTransferSOSRule  extends AbstractHybridSOSRule<HybridRebecaNetworkState> {

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(HybridRebecaNetworkState source) {
        HybridRebecaNetworkState backup = HybridRebecaStateSerializationUtils.clone(source);
        HybridRebecaNetworkState backup2 = HybridRebecaStateSerializationUtils.clone(source);
        HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> originalMessages = source.getReceivedMessages();
        List<HybridRebecaMessage> highPriorityMessages = getHighPriorityMessages(backup);
        if (highPriorityMessages.size() == 1) {
            HybridRebecaMessage selectedMessage = highPriorityMessages.get(0);
            HybridRebecaNondeterministicTransition<HybridRebecaNetworkState> result = new HybridRebecaNondeterministicTransition<>();
            if (source.getNow().getFirst().equals(selectedMessage.getMessageArrivalInterval().getFirst()) &&
                source.getNow().getSecond() < selectedMessage.getMessageArrivalInterval().getSecond()) {
                Pair<String, String> senderReceiver = new Pair<>(selectedMessage.getSender().getId(), selectedMessage.getReceiver().getId());

                HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> msgs = backup.getReceivedMessages();
                msgs.remove(senderReceiver);
                backup.setReceivedMessages(msgs);
                MessageAction messageAction = new MessageAction(selectedMessage);
                result.addDestination(messageAction, backup);

                Pair<Float, Float> newArrivalTime = new Pair<>(source.getNow().getSecond(), selectedMessage.getMessageArrivalInterval().getSecond());
                selectedMessage.setMessageArrivalInterval(newArrivalTime);
                HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> msg = originalMessages;
                ArrayList<HybridRebecaMessage> msgs22 = new ArrayList<>();
                msgs22.add(selectedMessage);
                msg.put(senderReceiver, msgs22);
                backup2.setReceivedMessages(msg);
                result.addDestination(messageAction, backup2);
                return result;

            } else if(source.getNow().getFirst().equals(selectedMessage.getMessageArrivalInterval().getFirst())) {
                HybridRebecaDeterministicTransition<HybridRebecaNetworkState> result2 = new HybridRebecaDeterministicTransition<>();
                Pair<String, String> senderReceiver = new Pair<>(selectedMessage.getSender().getId(), selectedMessage.getReceiver().getId());

                HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> msgs = backup.getReceivedMessages();
                msgs.remove(senderReceiver);
                backup.setReceivedMessages(msgs);
                MessageAction messageAction = new MessageAction(selectedMessage);
                result2.setDestination(backup);
                result2.setAction(messageAction);
                return result2;
            }
        } else {
            HybridRebecaNondeterministicTransition<HybridRebecaNetworkState> result = new HybridRebecaNondeterministicTransition<>();
            for (HybridRebecaMessage selectedMessage: highPriorityMessages) {
                if (source.getNow().getFirst().equals(selectedMessage.getMessageArrivalInterval().getFirst()) &&
                        source.getNow().getSecond() < selectedMessage.getMessageArrivalInterval().getSecond()) {
                    Pair<String, String> senderReceiver = new Pair<>(selectedMessage.getSender().getId(), selectedMessage.getReceiver().getId());

                    HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> msgs = backup.getReceivedMessages();
                    msgs.remove(senderReceiver);
                    backup.setReceivedMessages(msgs);
                    MessageAction messageAction = new MessageAction(selectedMessage);
                    result.addDestination(messageAction, backup);

                    Pair<Float, Float> newArrivalTime = new Pair<>(source.getNow().getSecond(), selectedMessage.getMessageArrivalInterval().getSecond());
                    selectedMessage.setMessageArrivalInterval(newArrivalTime);
                    HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> msg = originalMessages;
                    ArrayList<HybridRebecaMessage> msgs22 = new ArrayList<>();
                    msgs22.add(selectedMessage);
                    msg.put(senderReceiver, msgs22);
                    backup2.setReceivedMessages(msg);
                    result.addDestination(messageAction, backup2);

                } else if(source.getNow().getFirst().equals(selectedMessage.getMessageArrivalInterval().getFirst())) {
                    Pair<String, String> senderReceiver = new Pair<>(selectedMessage.getSender().getId(), selectedMessage.getReceiver().getId());
                    HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> msgs = backup.getReceivedMessages();
                    msgs.remove(senderReceiver);
                    backup.setReceivedMessages(msgs);
                    MessageAction messageAction = new MessageAction(selectedMessage);
                    result.addDestination(messageAction, backup);
                }
            }
            return result;
        }
        return null;
    }

    public static List<HybridRebecaMessage> getHighPriorityMessages(HybridRebecaNetworkState source) {
        float minETA = Float.MAX_VALUE;
        List<HybridRebecaMessage> earliestMessages = new ArrayList<>();

        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : source.getReceivedMessages().entrySet()) {
            ArrayList<HybridRebecaMessage> messages = entry.getValue();
            for (HybridRebecaMessage message : messages) {
                float etaLowerBound = message.getMessageArrivalInterval().getFirst();
                if (etaLowerBound < minETA) {
                    minETA = etaLowerBound;
                    earliestMessages.clear();
                    earliestMessages.add(message);
                } else if (etaLowerBound == minETA) {
                    earliestMessages.add(message);
                }
            }
        }
        return earliestMessages;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(Action synchAction, HybridRebecaNetworkState source) {
        return null;
    }
}
