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
        HybridRebecaNondeterministicTransition<HybridRebecaNetworkState> result = new HybridRebecaNondeterministicTransition<>();
        List<HybridRebecaMessage> sortedList = sortMessages(source);
        Float secondEarliest = getSecondEarliestEtaLowerBound(source);
        ArrayList<HybridRebecaAbstractTransition> transitions = new ArrayList<>();
        //Transfer2 condition check
        if (secondEarliest != null) {
            HybridRebecaMessage selectedMessage = sortedList.get(0);
            if (selectedMessage.getMessageArrivalInterval().getFirst().floatValue() == source.getNow().getFirst().floatValue()
                    && source.getNow().getFirst().floatValue() < secondEarliest.floatValue()
                    && secondEarliest.floatValue() < source.getNow().getSecond().floatValue()) {

                HybridRebecaNetworkState backup = HybridRebecaStateSerializationUtils.clone(source);
                for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : backup.getReceivedMessages().entrySet()) {
                    ArrayList<HybridRebecaMessage> messageList = entry.getValue();
                    for (HybridRebecaMessage message : messageList) {
                        if (message.getMessageArrivalInterval().getFirst().floatValue() == selectedMessage.getMessageArrivalInterval().getFirst().floatValue()) {
                            message.setMessageArrivalInterval(new Pair<>(secondEarliest, message.getMessageArrivalInterval().getSecond()));
                        }
                    }
                }

                MessageAction messageAction = new MessageAction(selectedMessage);
                result.addDestination(messageAction, backup);
                transitions.add(result);
            }
        }
        //Nondeterministic case of both transfer and postpone
        List<HybridRebecaMessage> sortedList2 = sortMessages(source);
        if (sortedList2.get(0).getMessageArrivalInterval().getFirst().floatValue() == source.getNow().getFirst().floatValue()) {
            for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : source.getReceivedMessages().entrySet()) {
                ArrayList<HybridRebecaMessage> messageList = entry.getValue();
                Iterator<HybridRebecaMessage> iterator = messageList.iterator();
                while (iterator.hasNext()) {
                    HybridRebecaMessage message = iterator.next();

                    //transfer case
                    if (message.getMessageArrivalInterval().getFirst().floatValue() == source.getNow().getFirst().floatValue()) {
                        HybridRebecaNetworkState backup2 = HybridRebecaStateSerializationUtils.clone(source);
                        HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> clonedMap = new HashMap<>();
                        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> e : source.getReceivedMessages().entrySet()) {
                            ArrayList<HybridRebecaMessage> clonedList = new ArrayList<>(e.getValue());
                            clonedMap.put(e.getKey(), clonedList);
                        }
                        ArrayList<HybridRebecaMessage> ms = clonedMap.get(entry.getKey());
                        ms.remove(message); // safe: source is untouched
                        clonedMap.put(entry.getKey(), ms);
                        backup2.setReceivedMessages(clonedMap);

                        HybridRebecaMessage clonedMessage = HybridRebecaStateSerializationUtils.clone(message);
                        clonedMessage.setMessageArrivalInterval(backup2.getNow());
                        MessageAction messageAction = new MessageAction(clonedMessage);
                        result.addDestination(messageAction, backup2);
                        transitions.add(result);
                    }
                    //postpone case
                    if (message.getMessageArrivalInterval().getFirst().floatValue() == source.getNow().getFirst().floatValue()
                            && source.getNow().getSecond().floatValue() < message.getMessageArrivalInterval().getSecond().floatValue()) {

                        HybridRebecaNetworkState backup3 = HybridRebecaStateSerializationUtils.clone(source);
                        HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> clonedMap = new HashMap<>();
                        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> e : source.getReceivedMessages().entrySet()) {
                            ArrayList<HybridRebecaMessage> clonedList = new ArrayList<>(e.getValue());
                            clonedMap.put(e.getKey(), clonedList);
                        }
                        ArrayList<HybridRebecaMessage> ms = clonedMap.get(entry.getKey());
                        ms.remove(message); // safe: source is untouched
                        HybridRebecaMessage clonedMessage = HybridRebecaStateSerializationUtils.clone(message);
                        clonedMessage.setMessageArrivalInterval(new Pair<>(backup3.getNow().getSecond(), message.getMessageArrivalInterval().getSecond()));
                        ms.add(clonedMessage);
                        clonedMap.put(entry.getKey(), ms);
                        backup3.setReceivedMessages(clonedMap);
                        result.addDestination(Action.TAU, backup3);
                        transitions.add(result);
                    }
                }
            }
        }

        if (transitions.size() == 0) {
            //TODO: Ottokke!
            return null;
        }

        return result;
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
