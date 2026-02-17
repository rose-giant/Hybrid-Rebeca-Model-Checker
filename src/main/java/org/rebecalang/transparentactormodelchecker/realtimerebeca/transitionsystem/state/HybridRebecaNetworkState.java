package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state;

import org.rebecalang.compiler.utils.Pair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class HybridRebecaNetworkState implements Serializable {
    private HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> receivedMessages;
    private Pair<Float, Float> now;

    public Pair<Float, Float> getNow() {
        return now;
    }

    public void setNow(Pair<Float, Float> now) {
        this.now = now;
    }

    public HybridRebecaNetworkState() {
        receivedMessages = new HashMap<Pair<String,String>, ArrayList<HybridRebecaMessage>>();
    }

    public HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> getReceivedMessages() {
        return receivedMessages;
    }
    public void setReceivedMessages(HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public void addMessage(HybridRebecaMessage message) {
        Pair<String, String> key = new Pair<String, String>(
                message.getSender().getId(), message.getReceiver().getId());
        if(!receivedMessages.containsKey(key))
            receivedMessages. put(key, new ArrayList<HybridRebecaMessage>());
        receivedMessages.get(key).add(message);
    }

    public float getMinETA() {
        float minETA = Float.MAX_VALUE;
        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : this.getReceivedMessages().entrySet()) {
            ArrayList<HybridRebecaMessage> messages = entry.getValue();
            for (HybridRebecaMessage message : messages) {
                float etaLowerBound = message.getMessageArrivalInterval().getFirst();
                if (etaLowerBound < minETA) minETA = etaLowerBound;
            }
        }

        return minETA;
    }

    public float getSecondMinETA() {
        float min = Float.MAX_VALUE;
        float secondMin = Float.MAX_VALUE;
        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry: this.getReceivedMessages().entrySet()) {
            ArrayList<HybridRebecaMessage> messages = entry.getValue();
            for (HybridRebecaMessage message : messages) {
                float eta = message.getMessageArrivalInterval().getFirst();

                if (eta < min) {
                    secondMin = min;
                    min = eta;
                }
                else if (eta > min && eta < secondMin) {
                    secondMin = eta;
                }
            }
        }

        return secondMin;
    }

    public float getMinETE() {
        float minETE = Float.MAX_VALUE;
        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : this.getReceivedMessages().entrySet()) {
            ArrayList<HybridRebecaMessage> messages = entry.getValue();
            for (HybridRebecaMessage message : messages) {
                float etaLowerBound = message.getMessageArrivalInterval().getSecond();
                if (etaLowerBound < minETE) minETE = etaLowerBound;
            }
        }

        return minETE;
    }

    public Pair<Float, Float> getTwoSmallestDistinctETAs() {
        float min = Float.MAX_VALUE;
        float secondMin = Float.MAX_VALUE;
        for (Map.Entry<Pair<String, String>, ArrayList<HybridRebecaMessage>> entry : this.getReceivedMessages().entrySet()) {
            ArrayList<HybridRebecaMessage> messages = entry.getValue();
            for (HybridRebecaMessage message : messages) {
                float lower = message.getMessageArrivalInterval().getFirst();
                float upper = message.getMessageArrivalInterval().getSecond();
                // process both bounds
                float[] values = { lower, upper };
                for (float eta : values) {
                    if (eta < min) {
                        secondMin = min;
                        min = eta;
                    }
                    else if (eta > min && eta < secondMin) {
                        secondMin = eta;
                    }
                }
            }
        }
        if (secondMin == Float.MAX_VALUE) {
            return null; // or new Pair<>(min, null) depending on your semantics
        }
        return new Pair<>(min, secondMin);
    }

    public boolean isEmpty() {
        if (receivedMessages == null || receivedMessages.isEmpty()) {
            return true;
        }
        for (ArrayList<HybridRebecaMessage> messages : receivedMessages.values()) {
            if (messages != null && !messages.isEmpty()) {
                return false; // found at least one message
            }
        }
        return true; // all lists were empty
    }

}
