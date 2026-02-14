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

}
