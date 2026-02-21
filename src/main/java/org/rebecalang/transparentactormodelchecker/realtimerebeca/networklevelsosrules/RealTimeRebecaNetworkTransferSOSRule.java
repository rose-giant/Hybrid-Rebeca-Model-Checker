package org.rebecalang.transparentactormodelchecker.realtimerebeca.networklevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;

import java.util.*;

public class RealTimeRebecaNetworkTransferSOSRule extends AbstractRealTimeSOSRule<RealTimeRebecaNetworkState> {

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaNetworkState> applyRule(RealTimeRebecaNetworkState source) {
        RealTimeRebecaNondeterministicTransition<RealTimeRebecaNetworkState> result = new RealTimeRebecaNondeterministicTransition<>();
        ArrayList<RealTimeRebecaAbstractTransition> transitions = new ArrayList<>();
        float secondMinETA = source.getSecondMinETA();
        Pair<Float, Float> now = source.getNow();
        Pair<Float, Float> bounds = source.getTwoSmallestDistinctETAs();
//        float firstB = bounds.getFirst();
        float secondB = bounds.getSecond();

        //Nondeterministic case of both transfer and postpone
        List<RealTimeRebecaMessage> sortedList2 = sortMessages(source);
//        if (sortedList2.get(0).getMessageArrivalInterval().getFirst().floatValue() >= source.getNow().getFirst().floatValue() &&
//                sortedList2.get(0).getMessageArrivalInterval().getFirst().floatValue() < source.getNow().getSecond().floatValue()) {
            for (Map.Entry<Pair<String, String>, ArrayList<RealTimeRebecaMessage>> entry : source.getReceivedMessages().entrySet()) {
                ArrayList<RealTimeRebecaMessage> messageList = entry.getValue();
                Iterator<RealTimeRebecaMessage> iterator = messageList.iterator();
                while (iterator.hasNext()) {
                    RealTimeRebecaMessage message = iterator.next();

                    //transfer case
                    if (message.getMessageArrivalInterval().getFirst().floatValue() == source.getNow().getFirst().floatValue()) {
                        RealTimeRebecaNetworkState backup2 = HybridRebecaStateSerializationUtils.clone(source);
                        HashMap<Pair<String, String>, ArrayList<RealTimeRebecaMessage>> clonedMap = new HashMap<>();
                        for (Map.Entry<Pair<String, String>, ArrayList<RealTimeRebecaMessage>> e : source.getReceivedMessages().entrySet()) {
                            ArrayList<RealTimeRebecaMessage> clonedList = new ArrayList<>(e.getValue());
                            clonedMap.put(e.getKey(), clonedList);
                        }
                        ArrayList<RealTimeRebecaMessage> ms = clonedMap.get(entry.getKey());
                        ms.remove(message); // safe: source is untouched
                        //ms = removeAndUpdateNetworkMessages(ms, message.getMessageArrivalInterval().getFirst());
                        clonedMap.put(entry.getKey(), ms);
                        List<Pair<String, String>> keysToRemove = new ArrayList<>();
                        for (Map.Entry<Pair<String, String>, ArrayList<RealTimeRebecaMessage>> entry2 : clonedMap.entrySet()) {
                            if (entry2.getValue().isEmpty()) {
                                keysToRemove.add(entry2.getKey());
                            }
                        }
                        for (Pair<String, String> key : keysToRemove) {
                            clonedMap.remove(key);
                        }

                        backup2.setReceivedMessages(clonedMap);
                        RealTimeRebecaMessage clonedMessage = HybridRebecaStateSerializationUtils.clone(message);
                        clonedMessage.setMessageArrivalInterval(new Pair<>(message.getMessageArrivalInterval().getFirst().floatValue(), Math.min(
                                source.getNow().getSecond().floatValue(), message.getMessageArrivalInterval().getSecond().floatValue()
                        )));
                        MessageAction messageAction = new MessageAction(clonedMessage);
                        result.addDestination(messageAction, backup2);
                        transitions.add(result);
                    }

                    if (source.transferable(source)) {
                        RealTimeRebecaNetworkState backup = HybridRebecaStateSerializationUtils.clone(source);
                        Pair<Float, Float> progress = new Pair<>(0f, 0f);
                        progress.setFirst(now.getSecond());

                        if (message.getMessageArrivalInterval().getFirst().floatValue() == now.getFirst().floatValue() &&
                        message.getMessageArrivalInterval().getSecond().floatValue() > now.getSecond().floatValue()) {
                            progress.setSecond(message.getMessageArrivalInterval().getSecond());
                            backup.setNow(progress);
                            HashMap<Pair<String, String>, ArrayList<RealTimeRebecaMessage>> clonedMap = new HashMap<>();
                            for (Map.Entry<Pair<String, String>, ArrayList<RealTimeRebecaMessage>> e : source.getReceivedMessages().entrySet()) {
                                ArrayList<RealTimeRebecaMessage> clonedList = new ArrayList<>(e.getValue());
                                clonedMap.put(e.getKey(), clonedList);
                            }
                            ArrayList<RealTimeRebecaMessage> ms = clonedMap.get(entry.getKey());
                            ms.remove(message); // safe: source is untouched
                            RealTimeRebecaMessage clonedMessage = HybridRebecaStateSerializationUtils.clone(message);
                            clonedMessage.setMessageArrivalInterval(new Pair<>(now.getSecond(), message.getMessageArrivalInterval().getSecond().floatValue()));
                            ms.add(clonedMessage);
                            clonedMap.put(entry.getKey(), ms);
                            backup.setReceivedMessages(clonedMap);
                            TimeProgressAction timeProgressAction = new TimeProgressAction();
                            timeProgressAction.setTimeProgress(progress);
                            result.addDestination(timeProgressAction, backup);
                            transitions.add(result);
                        }
//                        HybridRebecaNetworkEnvSync3SOSRule envSync3SOSRule = new HybridRebecaNetworkEnvSync3SOSRule();
//                        result = (HybridRebecaNondeterministicTransition<HybridRebecaNetworkState>) envSync3SOSRule.applyRule(source);
//                        transitions.add(result);
//                        return envSync3SOSRule.applyRule(source);
                    }
                }
            }

        if (transitions.size() == 0) {
            //TODO: Ottokke!
            return null;
        }

        if (transitions.size() == 1) {
            RealTimeRebecaDeterministicTransition firstTransition = new RealTimeRebecaDeterministicTransition<>();
            firstTransition.setDestination(result.getDestinations().get(0).getSecond());
            firstTransition.setAction(result.getDestinations().get(0).getFirst());
            return firstTransition;
        }

        return result;
    }

    public static List<RealTimeRebecaMessage> getHighPriorityMessages(RealTimeRebecaNetworkState source) {
        float minETA = Float.MAX_VALUE;
        List<RealTimeRebecaMessage> earliestMessages = new ArrayList<>();

        for (Map.Entry<Pair<String, String>, ArrayList<RealTimeRebecaMessage>> entry : source.getReceivedMessages().entrySet()) {
            ArrayList<RealTimeRebecaMessage> messages = entry.getValue();
            for (RealTimeRebecaMessage message : messages) {
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

    private ArrayList<RealTimeRebecaMessage> removeAndUpdateNetworkMessages(ArrayList<RealTimeRebecaMessage> messages, Float arrival_l) {
        for (RealTimeRebecaMessage msg: messages) {
            msg.setMessageArrivalInterval(new Pair<>(Math.max(arrival_l.floatValue(), msg.getMessageArrivalInterval().getFirst().floatValue()),
                    msg.getMessageArrivalInterval().getSecond().floatValue()));
        }

        return messages;
    }

    public static Float getSecondEarliestEtaLowerBound(RealTimeRebecaNetworkState source) {
        Float min = Float.MAX_VALUE;
        Float secondMin = Float.MAX_VALUE;
        for (Map.Entry<Pair<String, String>, ArrayList<RealTimeRebecaMessage>> entry : source.getReceivedMessages().entrySet()) {
            for (RealTimeRebecaMessage message : entry.getValue()) {
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



    public static List<RealTimeRebecaMessage> sortMessages(RealTimeRebecaNetworkState source) {
        List<RealTimeRebecaMessage> sortedMessages = new ArrayList<>();
        for (Map.Entry<Pair<String, String>, ArrayList<RealTimeRebecaMessage>> entry : source.getReceivedMessages().entrySet()) {
            sortedMessages.addAll(entry.getValue());
        }
        sortedMessages.sort(Comparator.comparingDouble(msg -> msg.getMessageArrivalInterval().getFirst()));
        return sortedMessages;
    }

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaNetworkState> applyRule(Action synchAction, RealTimeRebecaNetworkState source) {
        return null;
    }
}
