//package org.rebecalang.transparentactormodelchecker.hybridrebeca.utils;
//
//import org.rebecalang.compiler.utils.Pair;
//import org.rebecalang.transparentactormodelchecker.hybridrebeca.networklevelsosrules.HybridRebecaNetworkTransferSOSRule;
//import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
//import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class HybridRebecaEventListUtils {
//    public static float GetEarliestEventArrival(HybridRebecaSystemState systemState) {
//
//        List<HybridRebecaMessage> highPriorityMessages =
//                HybridRebecaNetworkTransferSOSRule.getHighPriorityMessages(systemState.getNetworkState());
//        float earliestEventArrival = highPriorityMessages.get(0).getMessageArrivalInterval().getFirst();
//
//        return earliestEventArrival;
//    }
//
//    public static Pair<Boolean,Pair<Float, Float>> getSecondEarliestEventInTheCurrentInterval(HybridRebecaSystemState systemState) {
//        float earliestEventArrival = GetEarliestEventArrival(systemState);
//        boolean isThereSecond = false;
//        float secondEarliestETA = Float.MAX_VALUE;
//        float secondEarliestUpperBound = Float.MAX_VALUE;
//        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : systemState.getNetworkState().getReceivedMessages().entrySet()) {
//            ArrayList<HybridRebecaMessage> messages = entry.getValue();
//            for (HybridRebecaMessage message : messages) {
//                float etaLowerBound = message.getMessageArrivalInterval().getFirst();
//                if (etaLowerBound > earliestEventArrival && etaLowerBound < systemState.getNow().getSecond()) {
//                    if (etaLowerBound < secondEarliestETA) {
//                        secondEarliestETA = etaLowerBound;
//                        secondEarliestUpperBound = message.getMessageArrivalInterval().getSecond();
//                        isThereSecond = true;
//                    }
//                }
//            }
//        }
//
//        Pair<Boolean, Pair<Float, Float>> secondEarliest = new Pair<>(isThereSecond, new Pair<>(secondEarliestETA, secondEarliestUpperBound));
//        return secondEarliest;
//    }
//}
