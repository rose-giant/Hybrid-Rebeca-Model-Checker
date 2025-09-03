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
        HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> originalMessages = source.getReceivedMessages();
        List<HybridRebecaMessage> sortedList = sortMessages(backup);
        Float secondEarliest = getSecondEarliestEtaLowerBound(backup);
        //TODO: check + remove the old message in the second case
        //Transfer2 condition check
        if (secondEarliest != null) {
            HybridRebecaMessage selectedMessage = sortedList.get(0);
            if (selectedMessage.getMessageArrivalInterval().getFirst().floatValue() == backup.getNow().getFirst().floatValue()
                    && backup.getNow().getFirst().floatValue() < secondEarliest.floatValue()
                    && secondEarliest.floatValue() < backup.getNow().getSecond().floatValue()) {

                for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : backup.getReceivedMessages().entrySet()) {
                    ArrayList<HybridRebecaMessage> messageList = entry.getValue();
                    for (HybridRebecaMessage message : messageList) {
                        message.setMessageArrivalInterval(new Pair<>(secondEarliest, message.getMessageArrivalInterval().getSecond()));
                    }
                }

                HybridRebecaDeterministicTransition<HybridRebecaNetworkState> result = new HybridRebecaDeterministicTransition<>();
                MessageAction messageAction = new MessageAction(selectedMessage);
                result.setDestination(backup);
                result.setAction(messageAction);
                return result;
            }
        }
        //Nondeterministic case of both transfer and postpone
        else if (sortedList.get(0).getMessageArrivalInterval().getFirst().floatValue() == backup.getNow().getFirst().floatValue()) {
            HybridRebecaNondeterministicTransition<HybridRebecaNetworkState> result = new HybridRebecaNondeterministicTransition<>();

            for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : backup.getReceivedMessages().entrySet()) {
                ArrayList<HybridRebecaMessage> messageList = entry.getValue();
                for (HybridRebecaMessage message : messageList) {
                    if (message.getMessageArrivalInterval().getFirst().floatValue() == backup.getNow().getFirst().floatValue()
                            && backup.getNow().getSecond().floatValue() < message.getMessageArrivalInterval().getSecond().floatValue()) {

                        message.setMessageArrivalInterval(new Pair<>(backup.getNow().getSecond(), message.getMessageArrivalInterval().getSecond()));
                        result.addDestination(Action.TAU, backup);

                        message.setMessageArrivalInterval(backup.getNow());
                        MessageAction messageAction = new MessageAction(message);
                        result.addDestination(messageAction, backup);
                    }
                    else if(message.getMessageArrivalInterval().getFirst().floatValue() == backup.getNow().getFirst().floatValue()) {
                        message.setMessageArrivalInterval(backup.getNow());
                        MessageAction messageAction = new MessageAction(message);
                        result.addDestination(messageAction, backup);
                    }
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

    public static Float getSecondEarliestEtaLowerBound(HybridRebecaNetworkState source) {
        Float min = Float.MAX_VALUE;
        Float secondMin = Float.MAX_VALUE;
        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : source.getReceivedMessages().entrySet()) {
            for (HybridRebecaMessage message : entry.getValue()) {
                float etaLowerBound = message.getMessageArrivalInterval().getFirst();
                if (etaLowerBound < min) {
                    secondMin = min;
                    min = etaLowerBound;
                } else if (etaLowerBound > min && etaLowerBound < secondMin) {
                    secondMin = etaLowerBound;
                }
            }
        }
        return (secondMin == Float.MAX_VALUE) ? null : secondMin;
    }


    public static List<HybridRebecaMessage> sortMessages(HybridRebecaNetworkState source) {
        List<HybridRebecaMessage> sortedMessages = new ArrayList<>();
        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : source.getReceivedMessages().entrySet()) {
            sortedMessages.addAll(entry.getValue());
        }
        sortedMessages.sort(Comparator.comparingDouble(msg -> msg.getMessageArrivalInterval().getFirst()));
        return sortedMessages;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(Action synchAction, HybridRebecaNetworkState source) {
        return null;
    }
}
