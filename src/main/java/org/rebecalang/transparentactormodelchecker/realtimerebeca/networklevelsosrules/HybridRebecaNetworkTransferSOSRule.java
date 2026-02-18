package org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;

import java.util.*;

public class HybridRebecaNetworkTransferSOSRule extends AbstractHybridSOSRule<HybridRebecaNetworkState> {

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaNetworkState> applyRule(HybridRebecaNetworkState source) {
        HybridRebecaNondeterministicTransition<HybridRebecaNetworkState> result = new HybridRebecaNondeterministicTransition<>();
        ArrayList<HybridRebecaAbstractTransition> transitions = new ArrayList<>();
        float secondMinETA = source.getSecondMinETA();
        Pair<Float, Float> now = source.getNow();
        Pair<Float, Float> bounds = source.getTwoSmallestDistinctETAs();
//        float firstB = bounds.getFirst();
        float secondB = bounds.getSecond();

        //Nondeterministic case of both transfer and postpone
        List<HybridRebecaMessage> sortedList2 = sortMessages(source);
//        if (sortedList2.get(0).getMessageArrivalInterval().getFirst().floatValue() >= source.getNow().getFirst().floatValue() &&
//                sortedList2.get(0).getMessageArrivalInterval().getFirst().floatValue() < source.getNow().getSecond().floatValue()) {
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
                        //ms = removeAndUpdateNetworkMessages(ms, message.getMessageArrivalInterval().getFirst());
                        clonedMap.put(entry.getKey(), ms);
                        List<Pair<String, String>> keysToRemove = new ArrayList<>();
                        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry2 : clonedMap.entrySet()) {
                            if (entry2.getValue().isEmpty()) {
                                keysToRemove.add(entry2.getKey());
                            }
                        }
                        for (Pair<String, String> key : keysToRemove) {
                            clonedMap.remove(key);
                        }

                        backup2.setReceivedMessages(clonedMap);
                        HybridRebecaMessage clonedMessage = HybridRebecaStateSerializationUtils.clone(message);
                        clonedMessage.setMessageArrivalInterval(new Pair<>(message.getMessageArrivalInterval().getFirst().floatValue(), Math.min(
                                source.getNow().getSecond().floatValue(), message.getMessageArrivalInterval().getSecond().floatValue()
                        )));
                        MessageAction messageAction = new MessageAction(clonedMessage);
                        result.addDestination(messageAction, backup2);
                        transitions.add(result);
                    }

//                    interleaving case
                    if (message.getMessageArrivalInterval().getFirst().floatValue() == source.getNow().getFirst().floatValue() &&
                            (now.getFirst().floatValue() < secondMinETA && secondMinETA < now.getSecond().floatValue() )
                    ) {
                        HybridRebecaNetworkState backup2 = HybridRebecaStateSerializationUtils.clone(source);
                        HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> clonedMap = new HashMap<>();
                        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> e : source.getReceivedMessages().entrySet()) {
                            ArrayList<HybridRebecaMessage> clonedList = new ArrayList<>(e.getValue());
                            clonedMap.put(e.getKey(), clonedList);
                        }
                        ArrayList<HybridRebecaMessage> ms = clonedMap.get(entry.getKey());
                        ms.remove(message); // safe: source is untouched
                        HybridRebecaMessage clonedMessage = HybridRebecaStateSerializationUtils.clone(message);
                        clonedMessage.setMessageArrivalInterval(new Pair<>(secondB, message.getMessageArrivalInterval().getSecond().floatValue()));
//                        clonedMessage.setMessageArrivalInterval(new Pair<>(message.getMessageArrivalInterval().getSecond().floatValue(), now.getSecond().floatValue()));
                        ms.add(clonedMessage);
                        clonedMap.put(entry.getKey(), ms);
                        backup2.setReceivedMessages(clonedMap);
                        result.addDestination(Action.TAU, backup2);
                        transitions.add(result);
                    }

                    //postpone case
                    if (message.getMessageArrivalInterval().getFirst().floatValue() == source.getNow().getFirst().floatValue() &&
                            source.getNow().getSecond().floatValue() < message.getMessageArrivalInterval().getSecond().floatValue() &&
                            !(now.getFirst().floatValue() < secondMinETA && secondMinETA < now.getSecond().floatValue() )
                    ) {

                        HybridRebecaNetworkState backup3 = HybridRebecaStateSerializationUtils.clone(source);
                        HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> clonedMap = new HashMap<>();
                        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> e : source.getReceivedMessages().entrySet()) {
                            ArrayList<HybridRebecaMessage> clonedList = new ArrayList<>(e.getValue());
                            clonedMap.put(e.getKey(), clonedList);
                        }
                        ArrayList<HybridRebecaMessage> ms = clonedMap.get(entry.getKey());
                        ms.remove(message); // safe: source is untouched
                        HybridRebecaMessage clonedMessage = HybridRebecaStateSerializationUtils.clone(message);
//                        clonedMessage.setMessageArrivalInterval(new Pair<>(secondB, message.getMessageArrivalInterval().getSecond()));
                        clonedMessage.setMessageArrivalInterval(new Pair<>(now.getSecond().floatValue(), message.getMessageArrivalInterval().getSecond().floatValue()));
                        ms.add(clonedMessage);
                        clonedMap.put(entry.getKey(), ms);
                        backup3.setReceivedMessages(clonedMap);
                        result.addDestination(Action.TAU, backup3);
                        transitions.add(result);
                    }
                }
            }
//        }

        if (transitions.size() == 0) {
            //TODO: Ottokke!
            return null;
        }

        if (transitions.size() == 1) {
            HybridRebecaDeterministicTransition firstTransition = new HybridRebecaDeterministicTransition<>();
            firstTransition.setDestination(result.getDestinations().get(0).getSecond());
            firstTransition.setAction(result.getDestinations().get(0).getFirst());
            return firstTransition;
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

    private ArrayList<HybridRebecaMessage> removeAndUpdateNetworkMessages(ArrayList<HybridRebecaMessage> messages, Float arrival_l) {
        for (HybridRebecaMessage msg: messages) {
            msg.setMessageArrivalInterval(new Pair<>(Math.max(arrival_l.floatValue(), msg.getMessageArrivalInterval().getFirst().floatValue()),
                    msg.getMessageArrivalInterval().getSecond().floatValue()));
        }

        return messages;
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
